package com.blogspot.fossipol.duendecat;

import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by patarapolw on 1/21/18.
 */

public class MyTimer {
    private boolean showAnswer, nextQuestion;
    private Button button;
    private final int showDelay = 3000, nextDelay = 1000; //milliseconds
    private boolean threadFinished = true;

    public boolean showAnswerClicked = false, nextQuestionClicked = false;

    public MyTimer (boolean showAnswer, boolean nextQuestion, Button button){
        this.showAnswer = showAnswer;
        this.nextQuestion = nextQuestion;
        this.button = button;
    }

    public void setTimer(boolean shown_answer) {
        threadFinished = false;
        if(showAnswer && !shown_answer){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!showAnswerClicked) {
                        button.performClick();
                    } else
                        showAnswerClicked = false;
                    threadFinished = true;
                }
            }, showDelay);
        }

        if(nextQuestion && shown_answer){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!nextQuestionClicked) {
                        button.performClick();
                    } else
                        nextQuestionClicked = false;
                    threadFinished = true;
                }
            }, nextDelay);
        }
    }

    public void setTimerWaitForTts(final boolean shown_answer, final TextToSpeech tts){
        final Handler h =new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (!tts.isSpeaking() && threadFinished) {
                    setTimer(shown_answer);
                }
                h.postDelayed(this, 1000);
            }
        };
        h.postDelayed(r, 1000);
    }
}
