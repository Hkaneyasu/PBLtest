package com.example.test11_01_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Rireki_for_Itirann extends AppCompatActivity {

    static int Thentime_Rireki[] = new int[3];
    static String strKoma_name = "科目名未登録";
    public final String AppFileName = "PBLservice";
    int koma = -1;
    String KomaNames[] = new String[25];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rireki_for__itirann);

        Thentime_Rireki = Itirann.Thentime;
        koma = Itirann.koma;
        KomaNames = MainActivity.KomaNames;
        strKoma_name = KomaNames[convKoma(koma)];

        String shussekiRireki = Thentime_Rireki[0]+addRireki(0) +"("+ Thentime_Rireki[0] +")";
        String tikokuRireki = Thentime_Rireki[1]+addRireki(1) + "("+ Thentime_Rireki[1] +")";
        String kessekiRireki = Thentime_Rireki[2]+addRireki(2) + "("+ Thentime_Rireki[2] +")";

        //科目の履歴
        String KamokuRireki = "科目名：　" + strKoma_name + " \n\n\n出席数：　 "
                + shussekiRireki + " \n\n\n遅刻数：　 "
                + tikokuRireki + " \n\n\n欠席数：　 "
                + kessekiRireki;

        TextView syussekikaisuu = (TextView) findViewById(R.id.syussekikaisuu);
        syussekikaisuu.setText(KamokuRireki);

        final EditText kamoku = findViewById(R.id.kamoku);
        final Button B_kamoku = findViewById(R.id.kamokuButton);
        B_kamoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String skamoku = null;
                skamoku = kamoku.getText().toString();
                setKamoku(skamoku);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.KomaNames = KomaNames;
        saveArray(MainActivity.KomaNames,"KomaNames");  //アクティビティを出る前に保存
    }

    public void setKamoku(String kamoku){
        switch (koma){
            case 11: MainActivity.KomaNames[0] = kamoku;break;
            case 12: MainActivity.KomaNames[1] = kamoku;break;
            case 13: MainActivity.KomaNames[2] = kamoku;break;
            case 14: MainActivity.KomaNames[3] = kamoku;break;
            case 15: MainActivity.KomaNames[4] = kamoku;break;

            case 21: MainActivity.KomaNames[5] = kamoku;break;
            case 22: MainActivity.KomaNames[6] = kamoku;break;
            case 23: MainActivity.KomaNames[7] = kamoku;break;
            case 24: MainActivity.KomaNames[8] = kamoku;break;
            case 25: MainActivity.KomaNames[9] = kamoku;break;

            case 31: MainActivity.KomaNames[10] = kamoku;break;
            case 32: MainActivity.KomaNames[11] = kamoku;break;
            case 33: MainActivity.KomaNames[12] = kamoku;break;
            case 34: MainActivity.KomaNames[13] = kamoku;break;
            case 35: MainActivity.KomaNames[14] = kamoku;break;

            case 41: MainActivity.KomaNames[15] = kamoku;break;
            case 42: MainActivity.KomaNames[16] = kamoku;break;
            case 43: MainActivity.KomaNames[17] = kamoku;break;
            case 44: MainActivity.KomaNames[18] = kamoku;break;
            case 45: MainActivity.KomaNames[19] = kamoku;break;

            case 51: MainActivity.KomaNames[20] = kamoku;break;
            case 52: MainActivity.KomaNames[21] = kamoku;break;
            case 53: MainActivity.KomaNames[22] = kamoku;break;
            case 54: MainActivity.KomaNames[23] = kamoku;break;
            case 55: MainActivity.KomaNames[24] = kamoku;break;
            default:break;
        }
    }

    public int convKoma(int koma){
        switch (koma){
            case 11: return 0;
            case 12: return 1;
            case 13: return 2;
            case 14: return 3;
            case 15: return 4;


            case 21: return 5;
            case 22: return 6;
            case 23: return 7;
            case 24: return 8;
            case 25: return 9;


            case 31: return 10;
            case 32: return 11;
            case 33: return 12;
            case 34: return 13;
            case 35: return 14;

            case 41: return 15;
            case 42: return 16;
            case 43: return 17;
            case 44: return 18;
            case 45: return 19;

            case 51: return 20;
            case 52: return 21;
            case 53: return 22;
            case 54: return 23;
            case 55: return 24;
            default:break;
        }
        return 25;
    }

    //jikan 0なら出席　１なら遅刻　２なら欠席の合計を返す
    public int addRireki(int jikann){
        int i;
        int sum = 0;
        for(i=0;i<25;i++){
            if(i == convKoma(koma)) continue;
            if(KomaNames[convKoma(koma)].equals(KomaNames[i])){
                switch (i){
                    case 0:  sum+=MainActivity.Monday1[jikann];  break;
                    case 1:  sum+=MainActivity.Monday2[jikann];  break;
                    case 2:  sum+=MainActivity.Monday3[jikann];  break;
                    case 3:  sum+=MainActivity.Monday4[jikann];  break;
                    case 4:  sum+=MainActivity.Monday5[jikann];  break;

                    case 5:  sum+=MainActivity.Tuesday1[jikann];  break;
                    case 6:  sum+=MainActivity.Tuesday2[jikann];  break;
                    case 7:  sum+=MainActivity.Tuesday3[jikann];  break;
                    case 8:  sum+=MainActivity.Tuesday4[jikann];  break;
                    case 9:  sum+=MainActivity.Tuesday5[jikann];  break;

                    case 10: sum+=MainActivity.Wednesday1[jikann];  break;
                    case 11: sum+=MainActivity.Wednesday2[jikann];  break;
                    case 12: sum+=MainActivity.Wednesday3[jikann];  break;
                    case 13: sum+= MainActivity.Wednesday4[jikann];  break;
                    case 14: sum+= MainActivity.Wednesday5[jikann];  break;

                    case 15:  sum+=MainActivity.Thursday1[jikann];  break;
                    case 16:  sum+=MainActivity.Thursday2[jikann];  break;
                    case 17:  sum+=MainActivity.Thursday3[jikann];  break;
                    case 18:  sum+=MainActivity.Thursday4[jikann];  break;
                    case 19:  sum+=MainActivity.Thursday5[jikann];  break;

                    case 20:  sum+=MainActivity.Friday1[jikann];  break;
                    case 21:  sum+=MainActivity.Friday2[jikann];  break;
                    case 22:  sum+=MainActivity.Friday3[jikann];  break;
                    case 23:  sum+=MainActivity.Friday4[jikann];  break;
                    case 24:  sum+=MainActivity.Friday5[jikann];  break;

                }
            }
        }
        return sum;
    }

    //Stringの配列をPreferenceで保存しましょう　kamoku1,kamoku2,kamoku3,...をそのまま文字列にして保存
    public void saveArray(String[] array,String PrefKey){
        StringBuffer buffer = new StringBuffer();
        String stringItem = null;
        for(String item : array){
            buffer.append(item+",");
        };
        if(buffer != null){
            String buf = buffer.toString();
            stringItem = buf.substring(0, buf.length() - 1);

            //SharedPreferences prefs1 = getSharedPreferences("Array", Context.MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PrefKey, stringItem).commit();
        }
    }

    public String[] getArray(String PrefKey){
        SharedPreferences prefs2 = getSharedPreferences("Array", Context.MODE_PRIVATE);
        String stringItem = prefs2.getString(PrefKey,"");
        if(stringItem != null && stringItem.length() != 0){
            return stringItem.split(",");
        }else{
            return null;
        }
    }

}
