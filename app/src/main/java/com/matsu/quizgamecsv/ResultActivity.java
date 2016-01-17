package com.matsu.quizgamecsv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);

        //前ActivityからScoreを受け取る
        int score = getIntent().getIntExtra("SCORE",0);

        //点数の表示
        TextView textScore = (TextView) findViewById(R.id.textView);
        textScore.setText(score + "／７点！！");
        textScore.setTextColor(Color.WHITE);

        //結果発表のText表示
        TextView textResult = (TextView)findViewById(R.id.textView1);
        if(score>=7){
            textResult.setText("すごい！！！！");      //満点
        }
        else if(score<7&&score>=3){
            textResult.setText("ふつうじゃな");        //3~6
        }
        else {
            textResult.setText("まだまだじゃな");      //0~2
        }
        textResult.setTextColor(Color.WHITE);
    }

    public void onClick(View v){
        //HOMEへ戻る
        Intent intent = new Intent(ResultActivity.this,TitleActivity.class);
        startActivity(intent);
    }

    //BackKeyの無効化
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }
        else{
            return false;
        }
    }
}