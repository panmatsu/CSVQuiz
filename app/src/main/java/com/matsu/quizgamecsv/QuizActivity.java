package com.matsu.quizgamecsv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends Activity {

    String Answer;    //正解
    int score;        //点数
    int cntStage = 0; //通しでの並び番号
    //Questionの並び順
    List<Integer> Stage = new ArrayList<Integer>();
    //選択肢Buttonの取得
    Button[] btChoice = new Button[4];
    //CountDownの宣言
    MyCountDownTimer cdt;
    TextView timer;
    //CSVから読み取ったQuizデータ収納:[Stage][0~4]: [0]問題、[1]正解、[2~4]不正解*3の順
    String[][]QuizText = new String[7][5];
    Intent intent;     //インテント

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //CountDownTextの取得
        timer = (TextView)findViewById(R.id.textTimer);

        //ListにQuestionの並び順を代入
        for(int i = 0; i < 7; i++){
            Stage.add(i);
        }

        //選択肢Buttonの取得
        btChoice[0] = (Button)findViewById(R.id.bt1);
        btChoice[1] = (Button)findViewById(R.id.bt2);
        btChoice[2] = (Button)findViewById(R.id.bt3);
        btChoice[3] = (Button)findViewById(R.id.bt4);

    }

    @Override
    protected void onResume(){
        super.onResume();

        CSVReader reader;   //CSVReaderの宣言
        AssetManager as = getResources().getAssets();  //AssetManagerの取得
        InputStream is = null;

        try{   //ファイルを開く
            is = as.open("quiz.csv");
        }catch (IOException e){  //例外処理
            e.printStackTrace();
        }
        InputStreamReader ireader = null;
        try{ //文字コード"SJIS"で渡す
            ireader = new InputStreamReader(is,"SJIS");
        }catch (UnsupportedEncodingException e){  //例外処理
            e.printStackTrace();
        }
        reader = new CSVReader(ireader,',','"',0);
        try {  //QuizTextにCSVからのデータを入れる
            for(int i = 0; i < 7; i++){
                QuizText[i] = reader.readNext();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初期値32000ミリ秒(=32秒)(微妙に遅れて始まるので30+2)
        cdt = new MyCountDownTimer(32000, 1000);
        //問題文のシャッフル
        setStage();
        //選択肢のセット
        setQuestion();
        //カウントダウンスタート
        cdt.start();
    }

    private void setStage() {

        //Questionの並び順をシャッフル
        Collections.shuffle(Stage);
    }

    private void setQuestion() {

        // 画面↑にあるテキストを「クイズNo. + 問題No で表示
        TextView quizBar = (TextView) findViewById(R.id.textNo);
        quizBar.setText("クイズNo." + Integer.toString(cntStage + 1));

        //問題選択肢の配列
        List<String> Choice = new ArrayList<String>();
        //Databaseから取得したデータを変数にセット
        String questionTitle =QuizText[Stage.get(cntStage)][0];//問題文
        for(int i = 0; i < 4; i++) {
            Choice.add(QuizText[Stage.get(cntStage)][i+1]);      //選択肢
        }
        Answer = Choice.get(0);       //答え

        //選択肢の並びをシャッフル
        Collections.shuffle(Choice);

        //テキストに問題文と質問を配置
        TextView TextQuestion = (TextView) findViewById(R.id.textQuestion);
        TextQuestion.setText(questionTitle);
        for(int i = 0; i < 4; i++) {
            btChoice[i].setText(Choice.get(i));
        }

        //ボタンの有効化
        for (int i = 0;i < 4;i++){
            btChoice[i].setEnabled(true);
        }
    }

    public void onClick(View v) {

        //ダイアログ表示中はカウントダウン停止
        cdt.cancel();

        //ステージ数をカウント
        cntStage++;

        //ダイアログ
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //Button連打を防ぐために一旦無効化
        for(int i = 0;i < 4;i++){
            btChoice[i].setEnabled(false);
        }
        //Dialogの中でのBackKeyの無効化
        alert.setCancelable(false);
        //ダイアログのTitleの表示
        alert.setTitle("Question:" + cntStage);

        //ダイアログのボタンを押した場合の処理
        //正解の場合
        if (((Button) v).getText().equals(Answer)) {
            score++;  //点数Plus
            alert.setMessage("正解だよ＾＾\n点数：＋１\nただいまの合計：" + score);
        }
        //不正解の場合
        else {
            alert.setMessage("残念不正解＾＾；\n点数：＋０\nただいまの合計：" + score);
        }

        //「次へ進む」ボタンの表示
        alert.setPositiveButton("次へ進む", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * 次へ進むボタンが押された時の処理
                 */
                //7以下の場合
                if (cntStage < 7) {
                    //問題画面を更新させる
                    setQuestion();
                    //中止したtimeを取得
                    String time = timer.getText().toString();
                    //timeを分と秒に分ける
                    String[] time2 = time.split(":", 0);
                    //止めたところからリスタートする
                    cdt = new MyCountDownTimer((Integer.parseInt(time2[0])*60+Integer.parseInt(time2[1]))*1000, 1000);
                    cdt.start();
                }
                //7以上の場合
                else {
                    //リザルト画面に移動
                    intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("SCORE", score);
                    startActivity(intent);
                }
            }
        });
        alert.show();
    }

    //CountDownについて
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // カウントダウン完了後に呼ばれる
            timer.setText("0:00");
            Toast.makeText(getApplicationContext(), "時間切れだよ(´・ω・｀)\nもう一回挑戦してみよう", Toast.LENGTH_LONG).show();
            //その時点でのScoreを引き継いでリザルト画面へ
            intent = new Intent(QuizActivity.this,ResultActivity.class);
            intent.putExtra("SCORE",score);
            startActivity(intent);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // インターバル(countDownInterval)毎に呼ばれる
            long m = millisUntilFinished/1000/60;  //分
            long s = millisUntilFinished/1000%60;  //秒
            timer.setText(String.format("%02d",m) + ":" + String.format("%02d",s)); //桁あわせ
            if(s<=10.0){
                //残り10秒になったら赤文字にしサイズを大きく
                timer.setTextColor(Color.RED);
                timer.setTextSize(30.0f);
            }
        }
    }

    //BackKeyの無効化
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }
        else{
            Toast.makeText(this, "Quiz中は戻るボタン禁止です！！！！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}