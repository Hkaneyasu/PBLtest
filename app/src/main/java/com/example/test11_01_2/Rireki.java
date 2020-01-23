package com.example.test11_01_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import static com.example.test11_01_2.MainActivity.KomaNames;

public class Rireki extends AppCompatActivity {

    int Nowtime_Rireki[] = new int[3];

    static String strKoma_name = "授業時間外";

    int Monday1[] = new int[3];
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


    static int Thentime_Rireki[] = new int[3];
    //static String strKoma_name = "科目名未登録";
    public final String AppFileName = "PBLservice";
    int koma = -1;
    String KomaNames[] = new String[25];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rireki);

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

        Nowtime_Rireki = MainActivity.Nowtime;
        strKoma_name = MainActivity.strKoma_Name;

        hanntei now = new hanntei();
        int nTime = now.set();

        if(nTime == 11) {
            Nowtime_Rireki = Monday1;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 12){
            Nowtime_Rireki = Monday2;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 13){
            Nowtime_Rireki = Monday3;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 14){
            Nowtime_Rireki = Monday4;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 15){
            Nowtime_Rireki = Monday5;
            //strKoma_name = hanntei.strKoma_Name;


        }else if(nTime == 21){
            Nowtime_Rireki = Tuesday1;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 22){
            Nowtime_Rireki = Tuesday2;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 23){
            Nowtime_Rireki = Tuesday3;
           // strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 24){
            Nowtime_Rireki = Tuesday4;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 25){
            Nowtime_Rireki = Tuesday5;
            //strKoma_name = hanntei.strKoma_Name;


        }else if(nTime == 31){
            Nowtime_Rireki = Wednesday1;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 32){
            Nowtime_Rireki = Wednesday2;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 33){
            Nowtime_Rireki = Wednesday3;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 34){
            Nowtime_Rireki = Wednesday4;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 35){
            Nowtime_Rireki = Wednesday5;
            //strKoma_name = hanntei.strKoma_Name;


        }else if(nTime == 41){
            Nowtime_Rireki = Thursday1;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 42){
            Nowtime_Rireki = Thursday2;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 43){
            Nowtime_Rireki = Thursday3;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 44){
            Nowtime_Rireki = Thursday4;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 45){
            Nowtime_Rireki = Thursday5;
            //strKoma_name = hanntei.strKoma_Name;


        }else if(nTime == 51){
            Nowtime_Rireki = Friday1;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 52){
            Nowtime_Rireki = Friday2;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 53){
            Nowtime_Rireki = Friday3;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 54){
            Nowtime_Rireki = Friday4;
            //strKoma_name = hanntei.strKoma_Name;
        }else if(nTime == 55){
            Nowtime_Rireki = Friday5;
            //strKoma_name = hanntei.strKoma_Name;
        }

        String KamokuRireki;
        KomaNames = MainActivity.KomaNames;
        int cKoma = convKoma(nTime);
        if(cKoma == 25){
            KamokuRireki = "授業時間外";
        }else {
            strKoma_name = KomaNames[cKoma];
            koma = nTime;

            String shussekiRireki = Nowtime_Rireki[0] + addRireki(0) + "(" + Nowtime_Rireki[0] + ")";
            String tikokuRireki = Nowtime_Rireki[1] + addRireki(1) + "(" + Nowtime_Rireki[1] + ")";
            String kessekiRireki = Nowtime_Rireki[2] + addRireki(2) + "(" + Nowtime_Rireki[2] + ")";

            //科目の履歴
            KamokuRireki = "科目名：　" + strKoma_name + " \n\n\n出席数：　 "
                    + shussekiRireki + " \n\n\n遅刻数：　 "
                    + tikokuRireki + " \n\n\n欠席数：　 "
                    + kessekiRireki;

        }
        TextView syussekikaisuu = (TextView) findViewById(R.id.syussekikaisuu);
        syussekikaisuu.setText(KamokuRireki);
    }

    //return25は授業時間外
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

}
