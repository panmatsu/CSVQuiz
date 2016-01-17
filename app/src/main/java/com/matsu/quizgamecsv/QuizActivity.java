package com.matsu.quizgamecsv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class QuizActivity extends Activity {

    String Answer;    //正解
    int score;        //点数
    int cntStage = 0; //通しでの並び番号
    //Questionの並び順
    String[] Stage= {"0","1","2","3","4","5","6"};
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

        //選択肢Buttonの取得
        btChoice[0] = (Button)findViewById(R.id.bt1);
        btChoice[1] = (Button)findViewById(R.id.bt2);
        btChoice[2] = (Button)findViewById(R.id.bt3);
        btChoice[3] = (Button)findViewById(R.id.bt4);

    }

    @Override
    protected void onResume(){
        super.onResume();
        //CSV読み込み
        Context context = getApplicationContext();
        parse(context);

        //初期値30000ミリ秒(=30秒)
        cdt = new MyCountDownTimer(30000, 1000);
        //問題文のシャッフル
        setStage();
        //選択肢のセット
        setQuestion();
        //カウントダウンスタート
        cdt.start();
    }

    private void setStage() {
        /**
         *  Stage配列のシャッフル
         */
        //配列からListへ変換
        List<String> list = Arrays.asList(Stage);
        //リストの並びをシャッフル
        Collections.shuffle(list);
        //Listから配列へ変換
        Stage = (String[])list.toArray(new String[list.size()]);
    }

    private void setQuestion() {

        // 画面↑にあるテキストを「クイズNo. + 問題No で表示
        TextView quizBar = (TextView) findViewById(R.id.textNo);
        quizBar.setText("クイズNo." + Integer.toString(cntStage + 1));

        //問題選択肢の配列
        String[] Choice= new String[4];
        //Databaseから取得したデータを変数にセット
        String questionTitle =QuizText[Integer.parseInt(Stage[cntStage])][0];//問題文
        Choice[0] =QuizText[Integer.parseInt(Stage[cntStage])][1];      //選択肢
        Choice[1] =QuizText[Integer.parseInt(Stage[cntStage])][2];
        Choice[2] =QuizText[Integer.parseInt(Stage[cntStage])][3];
        Choice[3] =QuizText[Integer.parseInt(Stage[cntStage])][4];
        Answer = Choice[0];       //答え

        /**
         *  Choise配列のシャッフル
         */
        //配列からListへ変換
        List<String> list = Arrays.asList(Choice);
        //リストの並びをシャッフル
        Collections.shuffle(list);
        //Listから配列へ変換
        Choice = (String[])list.toArray(new String[list.size()]);

        //テキストに問題文と質問を配置
        TextView TextQuestion = (TextView) findViewById(R.id.textQuestion);
        TextQuestion.setText(questionTitle);
        btChoice[0].setText(Choice[0]);
        btChoice[1].setText(Choice[1]);
        btChoice[2].setText(Choice[2]);
        btChoice[3].setText(Choice[3]);

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
            //String TAG = getLocalClassName();
            //Log.d(TAG, "押された");
            return false;
        }
    }

    public void parse(Context context){
        //AssetManagerの呼び出し
        AssetManager assetManager = context.getResources().getAssets();
        try{
            //CSVファイルの読み込み
            InputStream is = assetManager.open("quiz.csv");
            //文字コード"SJIS"形式で読み込む(文字化け対策)
            InputStreamReader inputStreamReader = new InputStreamReader(is,"SJIS");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            int i=0;
            while ((line = bufferedReader.readLine())!=null){
                //各行が","で区切られていて5つの項目
                StringTokenizer st = new StringTokenizer(line, ",");
                QuizText[i][0] = st.nextToken();
                QuizText[i][1] = st.nextToken();
                QuizText[i][2] = st.nextToken();
                QuizText[i][3] = st.nextToken();
                QuizText[i][4] = st.nextToken();
                i++;
            }
            bufferedReader.close();
        }catch (IOException e){
            //例外処理
            e.printStackTrace();
        }
    }

}