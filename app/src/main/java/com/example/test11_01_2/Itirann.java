package com.example.test11_01_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Itirann extends AppCompatActivity {

    //出席(=0)・欠席(=1)・遅刻(=2)カウント   サーバーからデータ取得？
    static int Monday1[] = new int[3];
    static int Monday2[] = new int[3];
    static int Monday3[] = new int[3];
    static int Monday4[] = new int[3];
    static int Monday5[] = new int[3];

    static int Tuesday1[] = new int[3];
    static int Tuesday2[] = new int[3];
    static int Tuesday3[] = new int[3];
    static int Tuesday4[] = new int[3];
    static int Tuesday5[] = new int[3];

    static int Wednesday1[] = new int[3];
    static int Wednesday2[] = new int[3];
    static int Wednesday3[] = new int[3];
    static int Wednesday4[] = new int[3];
    static int Wednesday5[] = new int[3];

    static int Thursday1[] = new int[3];
    static int Thursday2[] = new int[3];
    static int Thursday3[] = new int[3];
    static int Thursday4[] = new int[3];
    static int Thursday5[] = new int[3];

    static int Friday1[] = new int[3];
    static int Friday2[] = new int[3];
    static int Friday3[] = new int[3];
    static int Friday4[] = new int[3];
    static int Friday5[] = new int[3];

    private Button Btn_mo1;
    private Button Btn_mo2;
    private Button Btn_mo3;
    private Button Btn_mo4;
    private Button Btn_mo5;

    private Button Btn_tu1;
    private Button Btn_tu2;
    private Button Btn_tu3;
    private Button Btn_tu4;
    private Button Btn_tu5;

    private Button Btn_we1;
    private Button Btn_we2;
    private Button Btn_we3;
    private Button Btn_we4;
    private Button Btn_we5;

    private Button Btn_th1;
    private Button Btn_th2;
    private Button Btn_th3;
    private Button Btn_th4;
    private Button Btn_th5;

    private Button Btn_fr1;
    private Button Btn_fr2;
    private Button Btn_fr3;
    private Button Btn_fr4;
    private Button Btn_fr5;

    private TextView Tmo1;
    private TextView Tmo2;
    private TextView Tmo3;
    private TextView Tmo4;
    private TextView Tmo5;

    private TextView Ttu1;
    private TextView Ttu2;
    private TextView Ttu3;
    private TextView Ttu4;
    private TextView Ttu5;

    private TextView Twe1;
    private TextView Twe2;
    private TextView Twe3;
    private TextView Twe4;
    private TextView Twe5;

    private TextView Tth1;
    private TextView Tth2;
    private TextView Tth3;
    private TextView Tth4;
    private TextView Tth5;

    private TextView Tfr1;
    private TextView Tfr2;
    private TextView Tfr3;
    private TextView Tfr4;
    private TextView Tfr5;


    static int Thentime[] = new int[3];   //選択された科目の過去記録を代入するため。 staticでActivity間のデータ移動
    static String strKoma_Name = null; //科目名表示用
    static int koma = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itirann);
        FontFitTextView fft = new FontFitTextView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.addView(fft);


        Monday1 = MainActivity.Monday1;
        Monday2 = MainActivity.Monday2;
        Monday3 = MainActivity.Monday3;
        Monday4 = MainActivity.Monday4;
        Monday5 = MainActivity.Monday5;

        Tuesday1 = MainActivity.Tuesday1;
        Tuesday2 = MainActivity.Tuesday2;
        Tuesday3 = MainActivity.Tuesday3;
        Tuesday4 = MainActivity.Tuesday4;
        Tuesday5 = MainActivity.Tuesday5;

        Wednesday1 = MainActivity.Wednesday1;
        Wednesday2 = MainActivity.Wednesday2;
        Wednesday3 = MainActivity.Wednesday3;
        Wednesday4 = MainActivity.Wednesday4;
        Wednesday5 = MainActivity.Wednesday5;

        Thursday1 = MainActivity.Thursday1;
        Thursday2 = MainActivity.Thursday2;
        Thursday3 = MainActivity.Thursday3;
        Thursday4 = MainActivity.Thursday4;
        Thursday5 = MainActivity.Thursday5;

        Friday1 = MainActivity.Friday1;
        Friday2 = MainActivity.Friday2;
        Friday3 = MainActivity.Friday3;
        Friday4 = MainActivity.Friday4;
        Friday5 = MainActivity.Friday5;

        Tmo1 = findViewById(R.id.text_mo1);
        Tmo2 = findViewById(R.id.text_mo2);
        Tmo3 = findViewById(R.id.text_mo3);
        Tmo4 = findViewById(R.id.text_mo4);
        Tmo5 = findViewById(R.id.text_mo5);

        Ttu1 = findViewById(R.id.text_tu1);
        Ttu2 = findViewById(R.id.text_tu2);
        Ttu3 = findViewById(R.id.text_tu3);
        Ttu4 = findViewById(R.id.text_tu4);
        Ttu5 = findViewById(R.id.text_tu5);

        Twe1 = findViewById(R.id.text_we1);
        Twe2 = findViewById(R.id.text_we2);
        Twe3 = findViewById(R.id.text_we3);
        Twe4 = findViewById(R.id.text_we4);
        Twe5 = findViewById(R.id.text_we5);

        Tth1 = findViewById(R.id.text_th1);
        Tth2 = findViewById(R.id.text_th2);
        Tth3 = findViewById(R.id.text_th3);
        Tth4 = findViewById(R.id.text_th4);
        Tth5 = findViewById(R.id.text_th5);

        Tfr1 = findViewById(R.id.text_fr1);
        Tfr2 = findViewById(R.id.text_fr2);
        Tfr3 = findViewById(R.id.text_fr3);
        Tfr4 = findViewById(R.id.text_fr4);
        Tfr5 = findViewById(R.id.text_fr5);

        setKomaName();

        Btn_mo1 = (Button) findViewById(R.id.btn_mo1);
        Btn_mo1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Monday1;
                koma = 11;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_mo2 = (Button) findViewById(R.id.btn_mo2);
        Btn_mo2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Monday2;
                koma = 12;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_mo3 = (Button) findViewById(R.id.btn_mo3);
        Btn_mo3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Monday3;
                koma = 13;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_mo4 = (Button) findViewById(R.id.btn_mo4);
        Btn_mo4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Monday4;
                koma = 14;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_mo5 = (Button) findViewById(R.id.btn_mo5);
        Btn_mo5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Monday5;
                koma = 15;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });




        Btn_tu1 = (Button) findViewById(R.id.btn_tu1);
        Btn_tu1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Tuesday1;
                koma = 21;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_tu2 = (Button) findViewById(R.id.btn_tu2);
        Btn_tu2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Tuesday2;
                koma = 22;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_tu3 = (Button) findViewById(R.id.btn_tu3);
        Btn_tu3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Tuesday3;
                koma = 23;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_tu4 = (Button) findViewById(R.id.btn_tu4);
        Btn_tu4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Tuesday4;
                koma = 24;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_tu5 = (Button) findViewById(R.id.btn_tu5);
        Btn_tu5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Tuesday5;
                koma = 25;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });





        Btn_we1 = (Button) findViewById(R.id.btn_we1);
        Btn_we1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Wednesday1;
                koma = 31;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_we2 = (Button) findViewById(R.id.btn_we2);
        Btn_we2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Wednesday2;
                koma = 32;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_we3 = (Button) findViewById(R.id.btn_we3);
        Btn_we3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Wednesday3;
                koma = 33;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_we4 = (Button) findViewById(R.id.btn_we4);
        Btn_we4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Wednesday4;
                koma = 34;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_we5 = (Button) findViewById(R.id.btn_we5);
        Btn_we5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Wednesday5;
                koma = 35;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });





        Btn_th1 = (Button) findViewById(R.id.btn_th1);
        Btn_th1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Thursday1;
                koma = 41;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_th2 = (Button) findViewById(R.id.btn_th2);
        Btn_th2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Thursday2;
                koma = 42;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_th3 = (Button) findViewById(R.id.btn_th3);
        Btn_th3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Thursday3;
                koma = 43;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_th4 = (Button) findViewById(R.id.btn_th4);
        Btn_th4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Thursday4;
                koma = 44;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_th5 = (Button) findViewById(R.id.btn_th5);
        Btn_th5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Thursday5;
                koma = 45;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });





        Btn_fr1 = (Button) findViewById(R.id.btn_fr1);
        Btn_fr1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Friday1;
                koma = 51;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_fr2 = (Button) findViewById(R.id.btn_fr2);
        Btn_fr2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Friday2;
                koma = 52;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_fr3 = (Button) findViewById(R.id.btn_fr3);
        Btn_fr3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Friday3;
                koma = 53;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_fr4 = (Button) findViewById(R.id.btn_fr4);
        Btn_fr4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Friday4;
                koma = 54;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

        Btn_fr5 = (Button) findViewById(R.id.btn_fr5);
        Btn_fr5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thentime = Friday5;
                koma = 55;
                Intent intent = new Intent(getApplication(), Rireki_for_Itirann.class);
                startActivity(intent);
            }
        });

    }

    public void setKomaName(){
        Tmo1.setText(MainActivity.KomaNames[0]);
        Tmo2.setText(MainActivity.KomaNames[1]);
        Tmo3.setText(MainActivity.KomaNames[2]);
        Tmo4.setText(MainActivity.KomaNames[3]);
        Tmo5.setText(MainActivity.KomaNames[4]);

        Ttu1.setText(MainActivity.KomaNames[5]);
        Ttu2.setText(MainActivity.KomaNames[6]);
        Ttu3.setText(MainActivity.KomaNames[7]);
        Ttu4.setText(MainActivity.KomaNames[8]);
        Ttu5.setText(MainActivity.KomaNames[9]);

        Twe1.setText(MainActivity.KomaNames[10]);
        Twe2.setText(MainActivity.KomaNames[11]);
        Twe3.setText(MainActivity.KomaNames[12]);
        Twe4.setText(MainActivity.KomaNames[13]);
        Twe5.setText(MainActivity.KomaNames[14]);

        Tth1.setText(MainActivity.KomaNames[15]);
        Tth2.setText(MainActivity.KomaNames[16]);
        Tth3.setText(MainActivity.KomaNames[17]);
        Tth4.setText(MainActivity.KomaNames[18]);
        Tth5.setText(MainActivity.KomaNames[19]);

        Tfr1.setText(MainActivity.KomaNames[20]);
        Tfr2.setText(MainActivity.KomaNames[21]);
        Tfr3.setText(MainActivity.KomaNames[22]);
        Tfr4.setText(MainActivity.KomaNames[23]);
        Tfr5.setText(MainActivity.KomaNames[24]);

    }

}
