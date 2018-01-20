package com.blogspot.fossipol.duendecat;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;

/**
 * Created by patarapolw on 1/21/18.
 */

public class MyTimer {
    private boolean showAnswer, nextQuestion;
    private Button button;
    private final int showDelay = 5000, nextDelay = 2000; //milliseconds

    public MyTimer (boolean showAnswer, boolean nextQuestion, Button button){
        this.showAnswer = showAnswer;
        this.nextQuestion = nextQuestion;
        this.button = button;
    }

    public void onTtsFinished(boolean shown_answer) {
        if(showAnswer && !shown_answer){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.performClick();
                }
            }, showDelay);

        }

        if(nextQuestion && shown_answer){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.performClick();
                }
            }, nextDelay);
        }
    }

    private static boolean ttsSpeaking(TextToSpeech[] array)
    {
        for(int i=0; i<array.length; i++) {
            boolean b = array[i].isSpeaking();
            Log.d("Speak",i+" is speaking.");
            if(b) return true;
        }
        return false;
    }

    public void setTimer(final boolean shown_answer, final TextToSpeech[] array){

        final Handler h =new Handler();
        Runnable r = new Runnable() {
            public void run() {
                if (!ttsSpeaking(array)) {
                    onTtsFinished(shown_answer);
                }

                h.postDelayed(this, 1000);
            }
        };
        h.postDelayed(r, 1000);
    }
}
