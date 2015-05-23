package com.thracecodeinc.flagGame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HangmanPlayActivity extends Activity {
	private Random randomGenerator = new Random();
	private List<String> fileNameList; // flag file names
	private List<String> quizCountriesList;
	private Map<String, Boolean> regionsMap;
	String fileName;
	private ImageView flagImageView;

	private int curMan = 0;
	private ArrayList<Boolean> curAnswer;
	private String key;
	
	private void inputLetter(char c){
		boolean isContain = false;
		for(int i=0; i < key.length();++i){
			char ans = key.charAt(i);
			if((c == ans) || (Character.toUpperCase(c) == ans)){
				isContain = true;
				curAnswer.set(i, true);
			}
		}
		if(curMan > 0 &&isContain){
			curMan--;
		}
		disableLetter(c);
	}
	
	private void disableLetter(char c){
		char C = Character.toUpperCase(c);
		String buttonID = "button" + C;
	    int resID = getResources().getIdentifier(buttonID, "id", "com.thracecodeinc.flagGame");
	    Button b = (Button) findViewById(resID);
	    b.setEnabled(false);
	}
	
	private String getCurAnser(){
		String result = new String();
		for(int i=0;i<curAnswer.size();++i){
			if(curAnswer.get(i)){
				result += (key.charAt(i)+" ");
			}
			else{
				result += "_ ";
			}
		}
        Log.d("test", result);
        
		return result;
	}


	private void selectKey(){
		int numOfBlanks = 5;
		key = getCountryNameFromStrings(this, fileName);
        
		curAnswer = new ArrayList<Boolean>();
		for (int i = 0; i < key.length(); i++) {
			if (key.charAt(i)!=' ' && key.charAt(i)!='.')
				curAnswer.add(false);
			else
				curAnswer.add(true);
		}
		HashSet<Character> letterSet = new HashSet<Character>();
		for(int i=0;i<key.length();++i){
			letterSet.add(key.charAt(i));
		}
		
		int numOfLetters = letterSet.size();
		int numOfShow = 0;
		if(numOfLetters > numOfBlanks){
			curMan = 0;
			numOfShow = numOfLetters - numOfBlanks;
		}
		else if(numOfLetters < numOfBlanks){
			curMan = numOfBlanks - numOfLetters ; 
			numOfShow = 0;
		}


        Log.d("test","curMan"+curMan);

        Log.d("test","numOfShow"+numOfShow);
        
		for(int i=0;i<numOfShow;++i){
			int itemIndex = randomGenerator.nextInt(letterSet.size());
			int j = 0;
			for(Character c : letterSet)
			{
			    if (j == itemIndex){
			        inputLetter(c);
			        letterSet.remove(c);
			        break;
			    }
			    ++j;
			}
		}		
	}
	
	private void checkResult(){
		boolean isComplete = true;
		for(boolean b:curAnswer){
			if(!b){
				isComplete = false;
				break;
			}
		}
        TextView textFill = (TextView)findViewById(R.id.textFill);
        
        if(isComplete){
        	for(int i=0;i<26;i++){
        		char c = (char) ('a' + i);
        		disableLetter(c);
        	}
        	textFill.setText(getCurAnser());
        	return;
		}
       
        //not complete
        if(curMan < 8){
        	textFill.setText(getCurAnser());
        }
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hangman_activity_play);

		TextView textFill = (TextView)findViewById(R.id.textFill);
		fileNameList = new ArrayList<String>();
		quizCountriesList = new ArrayList<String>();
		regionsMap = new HashMap<String, Boolean>();
		String[] regionNames =
				getResources().getStringArray(R.array.regionsList);
		for (String region : regionNames )
			regionsMap.put(region, true);
		//regionsMap.putAll((HashMap<String,Boolean>) getIntent().getSerializableExtra("regionsMap"));

		flagImageView = (ImageView) findViewById(R.id.flagImageView);

		setFlag();
		selectKey();

    	textFill.setText(getCurAnser());

    	checkResult();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.flags_options_menu_myactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_item_return_home:
				SharedMethods.quitGamePopup(this);
				break;
			case R.id.menu_new_game:
				resetGamePopup();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void resetGame()
	{
		TextView textFill = (TextView)findViewById(R.id.textFill);
		fileNameList = new ArrayList<String>();
		quizCountriesList = new ArrayList<String>();
		regionsMap = new HashMap<String, Boolean>();
		String[] regionNames =
				getResources().getStringArray(R.array.regionsList);
		for (String region : regionNames )
			regionsMap.put(region, true);
		//regionsMap.putAll((HashMap<String,Boolean>) getIntent().getSerializableExtra("regionsMap"));

		flagImageView = (ImageView) findViewById(R.id.flagImageView);

		setFlag();
		selectKey();

		textFill.setText(getCurAnser());

		checkResult();
	}
	public void resetGamePopup(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setCustomTitle(SharedMethods.customText(getApplicationContext()));

		builder.setCancelable(true);
		builder.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						resetGame();
					}
				}
		);
		builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog resetDialog = builder.create();
		resetDialog.show();
	}

	public void clickLetter(View view) {   
		curMan++;
		switch (view.getId())
		  {
		    case R.id.buttonA:  inputLetter('a');
		                        break;
		    case R.id.buttonB:  inputLetter('b');
                                break;
		    case R.id.buttonC:  inputLetter('c');
                                break;
            case R.id.buttonD:  inputLetter('d');
                                break;
		    case R.id.buttonE:  inputLetter('e');
                                break;
            case R.id.buttonF:  inputLetter('f');
                                break;
            case R.id.buttonG:  inputLetter('g');
                                 break;
            case R.id.buttonH:  inputLetter('h');
                                 break;
		    case R.id.buttonI:  inputLetter('i');
                                 break;
            case R.id.buttonJ:  inputLetter('j');
                                  break;
            case R.id.buttonK:  inputLetter('k');
                                  break;
            case R.id.buttonL:  inputLetter('l');
                                 break;
            case R.id.buttonM:  inputLetter('m');
                                 break;
            case R.id.buttonN:  inputLetter('n');
                                  break;
            case R.id.buttonO:  inputLetter('o');
                                  break;
            case R.id.buttonP:  inputLetter('p');
                                 break;
             case R.id.buttonQ:  inputLetter('q');
                                 break;
             case R.id.buttonR:  inputLetter('r');
                                  break;
              case R.id.buttonS:  inputLetter('s');
                                 break;
              case R.id.buttonT:  inputLetter('t');
                                 break;
              case R.id.buttonU:  inputLetter('u');
                                break;
              case R.id.buttonV:  inputLetter('v');
                                   break;
              case R.id.buttonW:  inputLetter('w');
                                   break;
               case R.id.buttonX:  inputLetter('x');
                                 break;
               case R.id.buttonY:  inputLetter('y');
                                   break;
               case R.id.buttonZ:  inputLetter('z');
                                     break;
		  }
		
    	checkResult();
	}
	
	public void goHome(View view) {   
	       Intent intent = new Intent(this, GameStartPage.class);
	       startActivity(intent);
	}
	
	public void nextOne(View view) {   
	       Intent intent = new Intent(this, GameStartPage.class);
	       startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	public static String getCountryNameFromStrings(Activity a, String fileName)
	{
		int resId = a.getResources().getIdentifier(fileName.substring(fileName.indexOf("-") + 1), "string", a.getPackageName());
		Log.d("MyApp", "Country: " + fileName.substring(fileName.indexOf("-") + 1));
		return a.getString(resId);

	}


	//Find and set the corresponding flag
	public void setFlag()
	{
		AssetManager assets = getAssets();
		fileNameList.clear();

		try
		{
			Set<String> regions = regionsMap.keySet();

			for (String region : regions)
			{
				if (regionsMap.get(region))
				{   String[] paths = assets.list(region);
					for (String path : paths) {
						fileNameList.add(path.replace(".png", ""));
					}
				}
			}
		}
		catch (IOException e)
		{
			//Log.e(TAG, "Error loading image file names", e);
		}

		int numberOfFlags = fileNameList.size();
		int randomIndex = randomGenerator.nextInt(numberOfFlags);
		fileName = fileNameList.get(randomIndex);

		String region = fileName.substring(0, fileName.indexOf('-'));
		InputStream stream;
		try
		{
			stream = assets.open(region + "/" + fileName + ".png");
			Drawable flag = Drawable.createFromStream(stream, fileName);
			flagImageView.setImageDrawable(flag);
		}
		catch (IOException e)
		{
			Log.e("MyApp", "Error loading " + fileName, e);
		}

	}
	
}