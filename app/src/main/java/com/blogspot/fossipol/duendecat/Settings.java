package com.blogspot.fossipol.duendecat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Settings extends AppCompatActivity {
    private SharedPreferences prefs;

    private Spinner langBox;
    private EditText levelBox;
    private Spinner setBox;
    private ToggleButton reverseBox;
    private ToggleButton speakBox;
    private ToggleButton showAnswerBox;
    private ToggleButton nextQuestionBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("Config",0);

        langBox = (Spinner) findViewById(R.id.lang);
        levelBox = (EditText) findViewById(R.id.level);
        setBox = (Spinner) findViewById(R.id.set);
        reverseBox = (ToggleButton) findViewById(R.id.reverse);
        speakBox = (ToggleButton) findViewById(R.id.speak);
        showAnswerBox = (ToggleButton) findViewById(R.id.autoShow);
        nextQuestionBox = (ToggleButton) findViewById(R.id.nextQuestion);

        langBox.setSelection(indexOf(getResources().getStringArray(R.array.language),
                prefs.getString("language","Chinese")));
        final String[] lang = {String.valueOf(langBox.getSelectedItem())};
        setSetLanguage(lang[0]);

        langBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lang[0] = parent.getItemAtPosition(position).toString();
                setSetLanguage(lang[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button button = (Button) findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        levelBox.setText(Integer.toString(prefs.getInt("level",5)));
        reverseBox.setChecked(prefs.getBoolean("reverse",false));
        speakBox.setChecked(prefs.getBoolean("speak",false));
        showAnswerBox.setChecked(prefs.getBoolean("showAnswer",false));
        nextQuestionBox.setChecked(prefs.getBoolean("nextQuestion",false));
    }

    public void sendMessage() {
        Intent intent = new Intent(this, MainActivity.class);

        String lang = String.valueOf(langBox.getSelectedItem());
        int level = Integer.parseInt(levelBox.getText().toString());
        String set = String.valueOf(setBox.getSelectedItem());
        boolean reverse = reverseBox.isChecked();
        boolean speak = speakBox.isChecked();
        boolean showAnswer = showAnswerBox.isChecked();
        boolean nextQuestion = nextQuestionBox.isChecked();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language",lang);
        editor.putInt("level",level);
        editor.putString("set",set);
        editor.putBoolean("reverse", reverse);
        editor.putBoolean("speak", speak);
        editor.putBoolean("showAnswer", showAnswer);
        editor.putBoolean("nextQuestion",nextQuestion);
        editor.commit();

        startActivity(intent);
    }

    private void setSetLanguage(String lang) {
        String[] sets;
        if(lang.equals("Chinese")) {
            sets = getResources().getStringArray(R.array.set_chinese);
        } else {
            sets = getResources().getStringArray(R.array.set_japanese);
        }

        ArrayAdapter<String> setAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sets);
        setBox.setAdapter(setAdapter);
        setBox.setSelection(indexOf(sets, prefs.getString("set","SpoonFed")));
    }

    private int indexOf(String[] array, String string) {
        for(int i=0; i < array.length; i++)
            if(array[i].contains(string))
                return i;

        return -1;
    }
}
