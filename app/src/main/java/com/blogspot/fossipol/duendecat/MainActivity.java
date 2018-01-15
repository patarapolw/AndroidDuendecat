package com.blogspot.fossipol.duendecat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView langSentence = (TextView) findViewById(R.id.langSentence);
        final TextView pinyin = (TextView) findViewById(R.id.pinyin);
        final TextView enSentence = (TextView) findViewById(R.id.enSentence);

        final Random rand = new Random();
        final DatabaseReader database = new DatabaseReader(this);

        final int level = 3;
        final int limit = (int) Math.ceil(database.getNumberOfRows()*level/60f);
        final Sentence[] sentence = {database.getSentence(rand.nextInt(limit))};

        langSentence.setText(sentence[0].getSentence());
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
                    enSentence.setText(sentence[0].getEnglish());
                } else {
                    shown_answer[0] = false;
                    button.setText("Show Answer");
                    sentence[0] = database.getSentence(rand.nextInt(limit));
                    langSentence.setText(sentence[0].getSentence());
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
