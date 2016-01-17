package com.matsu.quizgamecsv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;


public class TitleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//タイトルバーの非表示
        setContentView(R.layout.activity_title);
    }

    public void onClick(View v){
        //Quiz画面へ繊維
        Intent intent = new Intent(TitleActivity.this,QuizActivity.class);
        startActivity(intent);
    }
}
