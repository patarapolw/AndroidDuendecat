package com.blogspot.fossipol.duendecat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Settings extends AppCompatActivity {
    private SharedPreferences prefs;

    private EditText levelBox;
    private ToggleButton reverseBox;
    private ToggleButton speakBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        levelBox = (EditText) findViewById(R.id.level);
        reverseBox = (ToggleButton) findViewById(R.id.reverse);
        speakBox = (ToggleButton) findViewById(R.id.speak);

        Button button = (Button) findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        prefs = getSharedPreferences("Config",0);
        levelBox.setText(Integer.toString(prefs.getInt("level",5)));
        reverseBox.setChecked(prefs.getBoolean("reverse",false));
        speakBox.setChecked(prefs.getBoolean("speak",false));
    }

    public void sendMessage() {
        Intent intent = new Intent(this, MainActivity.class);

        int level = Integer.parseInt(levelBox.getText().toString());
        boolean reverse = reverseBox.isChecked();
        boolean speak = speakBox.isChecked();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("level",level);
        editor.putBoolean("reverse", reverse);
        editor.putBoolean("speak", speak);
        editor.commit();

        startActivity(intent);
    }
}
