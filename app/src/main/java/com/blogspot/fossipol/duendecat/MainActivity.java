package com.blogspot.fossipol.duendecat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech langSpeak;
    private TextToSpeech enSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView langSentence = (TextView) findViewById(R.id.langSentence);
        final TextView pinyin = (TextView) findViewById(R.id.pinyin);
        final TextView enSentence = (TextView) findViewById(R.id.enSentence);

        final String lang;
        final int level;
        final boolean reverse;
        final boolean speak;

        SharedPreferences prefs = getSharedPreferences("Config",0);
        lang = prefs.getString("language", "Chinese");
        level = prefs.getInt("level",5);
        reverse = prefs.getBoolean("reverse", false);
        speak = prefs.getBoolean("speak",false);

        langSpeak = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(lang.equals("Chinese"))
                    langSpeak.setLanguage(Locale.CHINA);
                else
                    langSpeak.setLanguage(Locale.JAPAN);
            }
        });

        enSpeak = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                enSpeak.setLanguage(Locale.US);
            }
        });

        final Random rand = new Random();
        final DatabaseReader database = new DatabaseReader(this);

        final int limit = database.getRowLimit();
        final Sentence[] sentence = {database.getSentence(rand.nextInt(limit))};

        if(!reverse) {
            langSentence.setText(sentence[0].getSentence());
            if(speak) langSpeak.speak(sentence[0].getSentence(), TextToSpeech.QUEUE_FLUSH, null);
        } else {
            langSentence.setText(sentence[0].getEnglish());
            if(speak) enSpeak.speak(sentence[0].getEnglish(), TextToSpeech.QUEUE_FLUSH, null);
        }

        pinyin.setText("");
        enSentence.setText("Click to show answer");

        final Button button = (Button) findViewById(R.id.button);
        final boolean[] shown_answer = {false};
        button.setText("Show Answer");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!shown_answer[0]) {
                    shown_answer[0] = true;
                    button.setText("New Sentence");
                    pinyin.setText(sentence[0].getPinyin());
                    if(!reverse) {
                        enSentence.setText(sentence[0].getEnglish());
                        if(speak) enSpeak.speak(sentence[0].getEnglish(), TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        enSentence.setText(sentence[0].getSentence());
                        if(speak) langSpeak.speak(sentence[0].getSentence(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    shown_answer[0] = false;
                    button.setText("Show Answer");
                    sentence[0] = database.getSentence(rand.nextInt(limit));

                    if(!reverse) {
                        langSentence.setText(sentence[0].getSentence());
                        if(speak) langSpeak.speak(sentence[0].getSentence(), TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        langSentence.setText(sentence[0].getEnglish());
                        if(speak) enSpeak.speak(sentence[0].getEnglish(), TextToSpeech.QUEUE_FLUSH, null);
                    }

                    pinyin.setText("");
                    enSentence.setText("Click to show answer");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
