package com.blogspot.fossipol.duendecat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean ttsInit = false;
    private String lang, queueLang;
    private String queueSentence = "";
    private boolean shown_answer = false;
    private MyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView langSentence = (TextView) findViewById(R.id.langSentence);
        final TextView pinyin = (TextView) findViewById(R.id.pinyin);
        final TextView enSentence = (TextView) findViewById(R.id.enSentence);

        final int level;
        final boolean reverse;
        final boolean speak;

        SharedPreferences prefs = getSharedPreferences("Config",0);
        lang = prefs.getString("language", "Chinese");
        level = prefs.getInt("level",5);
        reverse = prefs.getBoolean("reverse", false);
        speak = prefs.getBoolean("speak",false);

        tts = new TextToSpeech(getApplicationContext(), this);

        final Random rand = new Random();
        //final DatabaseReader database = new DatabaseReader(this);
        final MyDatabase database = new MyDatabase(this, lang.toLowerCase()+".db");

        final int limit = database.getRowLimit();
        final Sentence[] sentence = {database.getSentence(rand.nextInt(limit))};

        if(!reverse) {
            langSentence.setText(sentence[0].getSentence());
            if(speak)
                say(lang,sentence[0].getSentence());
        } else {
            langSentence.setText(sentence[0].getEnglish());
            if(speak)
                say("English",sentence[0].getEnglish());
        }

        pinyin.setText("");
        enSentence.setText("Click to show answer");

        final Button button = (Button) findViewById(R.id.button);
        button.setText("Show Answer");

        final boolean showAnswer = prefs.getBoolean("showAnswer",false);
        final boolean nextQuestion = prefs.getBoolean("nextQuestion",false);
        timer = new MyTimer(showAnswer, nextQuestion, button);
        timer.setTimerWaitForTts(shown_answer, tts);
        //if(!speak) timer.setTimer(shown_answer);

        //final boolean[] preclick = {false};
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!shown_answer) {
                    shown_answer = true;
                    timer.showAnswerClicked = true;

                    button.setText("New Sentence");
                    pinyin.setText(sentence[0].getPinyin());
                    if(!reverse) {
                        enSentence.setText(sentence[0].getEnglish());
                        if(speak) {
                            say("English",sentence[0].getEnglish());

                        }
                    } else {
                        enSentence.setText(sentence[0].getSentence());
                        if(speak) {
                            say(lang,sentence[0].getSentence());
                        }
                    }
                } else {
                    shown_answer = false;
                    timer.nextQuestionClicked = true;

                    button.setText("Show Answer");
                    sentence[0] = database.getSentence(rand.nextInt(limit));

                    if(!reverse) {
                        langSentence.setText(sentence[0].getSentence());
                        if(speak)
                            say(lang,sentence[0].getSentence());
                    } else {
                        langSentence.setText(sentence[0].getEnglish());
                        if(speak)
                            say("English",sentence[0].getEnglish());
                    }

                    pinyin.setText("");
                    enSentence.setText("Click to show answer");
                }
                timer.setTimerWaitForTts(shown_answer, tts);
                //if(!speak) timer.setTimer(shown_answer);
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsInit = true;
            say(queueLang,queueSentence);
            queueSentence = "";
        } else {
            Log.e("MainActivity", "Initialization Failed!");
        }
    }

    public void say(String language, String sentence){
        if(!ttsInit){
            queueLang = language;
            queueSentence += sentence;
            return;
        }
        switch (language){
            case "Chinese":
                tts.setLanguage(Locale.CHINA);
                break;
            case "Japanese":
                tts.setLanguage(Locale.JAPAN);
                break;
            default:
                tts.setLanguage(Locale.US);
        }
        tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
        tts.setOnUtteranceProgressListener(mProgressListener);
    }

    private UtteranceProgressListener mProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        } // Do nothing

        @Override
        public void onError(String utteranceId) {
        } // Do nothing.

        @Override
        public void onDone(String utteranceId) {
            Log.d("TTS","Trying to set Timer");
            timer.setTimer(shown_answer);
        }
    };
}
