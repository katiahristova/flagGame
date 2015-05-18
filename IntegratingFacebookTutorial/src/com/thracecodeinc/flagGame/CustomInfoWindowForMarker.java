package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by katiahristova on 4/15/15.
 */
    class CustomInfoWindowForMarker implements GoogleMap.InfoWindowAdapter {
        private final View markerView;
        public static Map<String, Integer> populationMap = new HashMap<String, Integer>();

    CustomInfoWindowForMarker() {
        this.markerView = null;
    }

    CustomInfoWindowForMarker(Activity a, String fileName, Drawable flag_old) {

            markerView = a.getLayoutInflater()
                    .inflate(R.layout.flags_custom_marker_layout, null);

            Bitmap bitmap = ((BitmapDrawable) flag_old).getBitmap();

            final ImageView image = ((ImageView) markerView.findViewById(R.id.badge));
            image.setImageBitmap(bitmap);

            final TextView titleUi = ((TextView) markerView.findViewById(R.id.title));


            String country = fileName.substring(fileName.indexOf("-")+1);

            Log.d("Country info", "Country: " + country);
            titleUi.setText(OnlineGame.getCountryNameFromStrings(a, country));

            final TextView snippetUi = ((TextView) markerView
                    .findViewById(R.id.snippet));

            int pop = 0;
            if (populationMap.containsKey(country))
                pop = populationMap.get(country);

            Log.d("Country info", "Country: " + country + " Population: " + pop);

            if (pop!=0)
                snippetUi.setText(a.getResources().getString(R.string.population) + ": " + NumberFormat.getNumberInstance(Locale.US).format(pop));


        }




    public View getInfoWindow(Marker marker) {
            render(marker, markerView);
            return markerView;
        }

        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            // Add the code to set the required values
            // for each element in your custominfowindow layout file
        }

    public void getPopulations(AssetManager assetManager)
    {
        InputStream is = null;
        try {

            is = assetManager.open("countriesData.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] RowData = line.split(",");
                String name = RowData[0];
                String capital = RowData[1];
                String population = RowData[2];
                String territory = RowData[3];
                populationMap.put(name, Integer.valueOf(population));
                //Log.d("Reading Info", "name: " + name + " population: " + population);
            }

        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // handle exception
            }
        }

    }
}
