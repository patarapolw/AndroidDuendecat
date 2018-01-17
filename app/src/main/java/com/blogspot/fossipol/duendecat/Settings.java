package com.blogspot.fossipol.duendecat;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button button = (Button) findViewById(R.id.goBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void sendMessage() {
        Intent intent = new Intent(this, MainActivity.class);

        EditText levelBox = (EditText) findViewById(R.id.level);
        int level = Integer.parseInt(levelBox.getText().toString());

        ToggleButton reverseBox = (ToggleButton) findViewById(R.id.reverse);
        boolean reverse = reverseBox.isChecked();

        ToggleButton speakBox = (ToggleButton) findViewById(R.id.speak);
        boolean speak = speakBox.isChecked();

        Bundle extras = new Bundle();
        extras.putInt("level", level);
        extras.putBoolean("reverse", reverse);
        extras.putBoolean("speak", speak);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
