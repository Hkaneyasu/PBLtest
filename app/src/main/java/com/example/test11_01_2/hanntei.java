package com.example.test11_01_2;

import java.util.Calendar;

public class hanntei {

    String strKoma_Name = "授業時間外"; //科目名表示用
    String strKoma = "授業時間外";

    //<科目名登録


    public int set() {

        //時刻表示するコードを追加
        Calendar cal = Calendar.getInstance();       //カレンダーを取得

        int iYear = cal.get(Calendar.YEAR);         //年を取得
        int iMonth = cal.get(Calendar.MONTH) + 1;       //月を取得  0~11に+1して1~12
        int iDate = cal.get(Calendar.DATE);         //日を取得
        int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
        int iMinute = cal.get(Calendar.MINUTE);    //分を取得
        int iSecond = cal.get(Calendar.SECOND);    //分を取得
        int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
        int iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得
        int iSum_Time = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)

        int iKoma = 0;//今が何曜日の何コマ目か   月曜１－＞１１　金曜３－＞５３


        iKoma = -1;
        //メインアクティビティの時間変数更新
        MainActivity.iYear = iYear;
        MainActivity.iMonth = iMonth;
        MainActivity.iDate = iDate;
        MainActivity.iHour = iHour;
        MainActivity.iMinute = iMinute;
        MainActivity.iSecond = iSecond;
        MainActivity.iDayOfWeek = iDayOfWeek;
        MainActivity.iAM_PM = iAM_PM;
        MainActivity.iSum_Time = iSum_Time;

        if (iDayOfWeek == 2) {   //月曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 11;
                strKoma = "(月曜日1限)";
                strKoma_Name = MainActivity.KomaNames[0];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 12;
                strKoma = "(月曜日2限)";
                strKoma_Name = MainActivity.KomaNames[1];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 13;
                strKoma = "(月曜日3限)";
                strKoma_Name = MainActivity.KomaNames[2];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 14;
                strKoma = "(月曜日4限)";
                strKoma_Name = MainActivity.KomaNames[3];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 15;
                strKoma = "(月曜日5限)";
                strKoma_Name = MainActivity.KomaNames[4];

            }

        } else if (iDayOfWeek == 3) {   //火曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 21;
                strKoma = "(火曜日1限)";
                strKoma_Name = MainActivity.KomaNames[5];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 22;
                strKoma = "(火曜日2限)";
                strKoma_Name = MainActivity.KomaNames[6];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 23;
                strKoma = "(火曜日3限)";
                strKoma_Name = MainActivity.KomaNames[7];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 24;
                strKoma = "(火曜日4限)";
                strKoma_Name = MainActivity.KomaNames[8];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 25;
                strKoma = "(火曜日5限)";
                strKoma_Name = MainActivity.KomaNames[9];
            }
        } else if (iDayOfWeek == 4) {   //水曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 31;
                strKoma = "(水曜日1限)";
                strKoma_Name = MainActivity.KomaNames[10];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 32;
                strKoma = "(水曜日2限)";
                strKoma_Name = MainActivity.KomaNames[11];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 33;
                strKoma = "(水曜日3限)";
                strKoma_Name = MainActivity.KomaNames[12];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 34;
                strKoma = "(水曜日4限)";
                strKoma_Name = MainActivity.KomaNames[13];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 35;
                strKoma = "(水曜日5限)";
                strKoma_Name = MainActivity.KomaNames[14];
            }


            } else if (iDayOfWeek == 5) {   //木曜

                if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                    iKoma = 41;
                    strKoma = "(木曜日1限)";
                    strKoma_Name = MainActivity.KomaNames[15];
                } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                    iKoma = 42;
                    strKoma = "(木曜日2限)";
                    strKoma_Name = MainActivity.KomaNames[16];
                } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                    iKoma = 43;
                    strKoma = "(木曜日3限)";
                    strKoma_Name = MainActivity.KomaNames[17];
                } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                    iKoma = 44;
                    strKoma = "(木曜日4限)";
                    strKoma_Name = MainActivity.KomaNames[18];
                } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                    iKoma = 45;
                    strKoma = "(木曜日5限)";
                    strKoma_Name = MainActivity.KomaNames[19];
                }
            } else if (iDayOfWeek == 6) {   //金曜

                if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                    iKoma = 51;
                    strKoma = "(金曜日1限)";
                    strKoma_Name = MainActivity.KomaNames[20];
                } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                    iKoma = 52;
                    strKoma = "(金曜日2限)";
                    strKoma_Name = MainActivity.KomaNames[21];
                } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                    iKoma = 53;
                    strKoma = "(金曜日3限)";
                    strKoma_Name = MainActivity.KomaNames[22];
                } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                    iKoma = 54;
                    strKoma = "(金曜日4限)";
                    strKoma_Name = MainActivity.KomaNames[23];
                } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                    iKoma = 55;
                    strKoma = "(金曜日5限)";
                    strKoma_Name = MainActivity.KomaNames[24];
                }
            }

            MainActivity.strKoma_Name = strKoma_Name;
            MainActivity.strKoma = strKoma;
            return iKoma;
        }

    public int tikoku() {
        //時刻表示するコードを追加
        Calendar cal = Calendar.getInstance();       //カレンダーを取得

        int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
        int iMinute = cal.get(Calendar.MINUTE);    //分を取得
        int iSecond = cal.get(Calendar.SECOND);    //分を取得
        int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
        int iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得
        int nowTime = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)


        if (iDayOfWeek == 2) {   //月曜

            if (iAM_PM == 0 && 37200  >= nowTime && nowTime >= 31800 + 900) {//8時50分~10時20分
                MainActivity.Monday1[1]++;
                return 31800;
            } else if (iAM_PM == 0 && 43200  >= nowTime && nowTime >= 37800 + 900) {//10時30分~12時
                MainActivity.Monday1[1]++;
                return 37800;
            } else if (iAM_PM == 1 && 9000 >= nowTime && nowTime >= 3600 + 900) {//13時~14時30分
                MainActivity.Monday1[1]++;
                return 3600;
            } else if (iAM_PM == 1 && 15000  >= nowTime && nowTime >= 9600 + 900) {//14時40分~16時10分
                MainActivity.Monday1[1]++;
                return 9600;
            } else if (iAM_PM == 1 && 21000  >= nowTime && nowTime >= 15600 + 900) {//16時20分~17時50分
                MainActivity.Monday1[1]++;
                return 15600;
            }

        } else if (iDayOfWeek == 3) {   //火曜

            if (iAM_PM == 0 && 37200  >= nowTime && nowTime >= 31800 + 900) {//8時50分~10時20分
                MainActivity.Tuesday1[1]++;
                return 31800;
            } else if (iAM_PM == 0 && 43200 >= nowTime && nowTime >= 37800 + 900) {//10時30分~12時
                MainActivity.Tuesday2[1]++;
                return 37800;
            } else if (iAM_PM == 1 && 9000 >= nowTime && nowTime >= 3600 + 900) {//13時~14時30分
                MainActivity.Tuesday3[1]++;
                return 3600;
            } else if (iAM_PM == 1 && 15000  >= nowTime && nowTime >= 9600 + 900) {//14時40分~16時10分
                MainActivity.Tuesday4[1]++;
                return 9600;
            } else if (iAM_PM == 1 && 21000  >= nowTime && nowTime >= 15600 + 900) {//16時20分~17時50分
                MainActivity.Tuesday5[1]++;
                return 15600;
            }

        } else if (iDayOfWeek == 4) {   //水曜

            if (iAM_PM == 0 && 37200  >= nowTime && nowTime >= 31800 + 900) {//8時50分~10時20分
                MainActivity.Wednesday1[1]++;
                return 31800;
            } else if (iAM_PM == 0 && 43200  >= nowTime && nowTime >= 37800 + 900) {//10時30分~12時
                MainActivity.Wednesday2[1]++;
                return 37800;
            } else if (iAM_PM == 1 && 9000  >= nowTime && nowTime >= 3600 + 900) {//13時~14時30分
                MainActivity.Wednesday3[1]++;
                return 3600;
            } else if (iAM_PM == 1 && 15000  >= nowTime && nowTime >= 9600 + 900) {//14時40分~16時10分
                MainActivity.Wednesday4[1]++;
                return 9600;
            } else if (iAM_PM == 1 && 21000  >= nowTime && nowTime >= 15600 + 900) {//16時20分~17時50分
                MainActivity.Wednesday5[1]++;
                return 15600;
            }

        } else if (iDayOfWeek == 5) {   //木曜

            if (iAM_PM == 0 && 37200  >= nowTime && nowTime >= 31800 + 900) {//8時50分~10時20分
                MainActivity.Thursday1[1]++;
                return 31800;
            } else if (iAM_PM == 0 && 43200  > nowTime && nowTime >= 37800 + 900) {//10時30分~12時
                MainActivity.Thursday2[1]++;
                return 37800;
            } else if (iAM_PM == 1 && 9000  >= nowTime && nowTime >= 3600 + 900) {//13時~14時30分
                MainActivity.Thursday3[1]++;
                return 3600;
            } else if (iAM_PM == 1 && 15000  >= nowTime && nowTime >= 9600 + 900) {//14時40分~16時10分
                MainActivity.Thursday4[1]++;
                return 9600;
            } else if (iAM_PM == 1 && 21000 >= nowTime && nowTime >= 15600 + 900) {//16時20分~17時50分
                MainActivity.Thursday5[1]++;
                return 15600;
            }

        } else if (iDayOfWeek == 6) {   //金曜

            if (iAM_PM == 0 && 37200  >= nowTime && nowTime >= 31800 + 900) {//8時50分~10時20分
                MainActivity.Friday1[1]++;
                return 31800;
            } else if (iAM_PM == 0 && 43200 >= nowTime && nowTime >= 37800 + 900) {//10時30分~12時
                MainActivity.Friday2[1]++;
                return 37800;
            } else if (iAM_PM == 1 && 9000 >= nowTime && nowTime >= 3600 + 900) {//13時~14時30分
                MainActivity.Friday3[1]++;
                return 3600;
            } else if (iAM_PM == 1 && 15000  >= nowTime && nowTime >= 9600 + 900) {//14時40分~16時10分
                MainActivity.Friday4[1]++;
                return 9600;
            } else if (iAM_PM == 1 && 21000  >= nowTime && nowTime >= 15600 + 900) {//16時20分~17時50分
                MainActivity.Friday5[1]++;
                return 15600;
            }
        }

        return -1;  //遅刻じゃなかった
    }

    public int TimeToKoma(int iSum_Time) {

        //時刻表示するコードを追加
        Calendar cal = Calendar.getInstance();       //カレンダーを取得

        int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
        int iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得

        int iKoma = 0;//今が何曜日の何コマ目か   月曜１－＞１１　金曜３－＞５３


        iKoma = -1;

        if (iDayOfWeek == 2) {   //月曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 11;
                strKoma = "(月曜日1限)";
                strKoma_Name = MainActivity.KomaNames[0];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 12;
                strKoma = "(月曜日2限)";
                strKoma_Name = MainActivity.KomaNames[1];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 13;
                strKoma = "(月曜日3限)";
                strKoma_Name = MainActivity.KomaNames[2];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 14;
                strKoma = "(月曜日4限)";
                strKoma_Name = MainActivity.KomaNames[3];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 15;
                strKoma = "(月曜日5限)";
                strKoma_Name = MainActivity.KomaNames[4];

            }

        } else if (iDayOfWeek == 3) {   //火曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 21;
                strKoma = "(火曜日1限)";
                strKoma_Name = MainActivity.KomaNames[5];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 22;
                strKoma = "(火曜日2限)";
                strKoma_Name = MainActivity.KomaNames[6];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 23;
                strKoma = "(火曜日3限)";
                strKoma_Name = MainActivity.KomaNames[7];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 24;
                strKoma = "(火曜日4限)";
                strKoma_Name = MainActivity.KomaNames[8];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 25;
                strKoma = "(火曜日5限)";
                strKoma_Name = MainActivity.KomaNames[9];
            }
        } else if (iDayOfWeek == 4) {   //水曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 31;
                strKoma = "(水曜日1限)";
                strKoma_Name = MainActivity.KomaNames[10];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 32;
                strKoma = "(水曜日2限)";
                strKoma_Name = MainActivity.KomaNames[11];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 33;
                strKoma = "(水曜日3限)";
                strKoma_Name = MainActivity.KomaNames[12];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 34;
                strKoma = "(水曜日4限)";
                strKoma_Name = MainActivity.KomaNames[13];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 35;
                strKoma = "(水曜日5限)";
                strKoma_Name = MainActivity.KomaNames[14];
            }


        } else if (iDayOfWeek == 5) {   //木曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 41;
                strKoma = "(木曜日1限)";
                strKoma_Name = MainActivity.KomaNames[15];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 42;
                strKoma = "(木曜日2限)";
                strKoma_Name = MainActivity.KomaNames[16];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 43;
                strKoma = "(木曜日3限)";
                strKoma_Name = MainActivity.KomaNames[17];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 44;
                strKoma = "(木曜日4限)";
                strKoma_Name = MainActivity.KomaNames[18];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 45;
                strKoma = "(木曜日5限)";
                strKoma_Name = MainActivity.KomaNames[19];
            }
        } else if (iDayOfWeek == 6) {   //金曜

            if (iAM_PM == 0 && 37200 >= iSum_Time && iSum_Time >= 31800) {//8時50分~10時20分
                iKoma = 51;
                strKoma = "(金曜日1限)";
                strKoma_Name = MainActivity.KomaNames[20];
            } else if (iAM_PM == 0 && 43200 >= iSum_Time && iSum_Time >= 37800) {//10時30分~12時
                iKoma = 52;
                strKoma = "(金曜日2限)";
                strKoma_Name = MainActivity.KomaNames[21];
            } else if (iAM_PM == 1 && 9000 >= iSum_Time && iSum_Time >= 3600) {//13時~14時30分
                iKoma = 53;
                strKoma = "(金曜日3限)";
                strKoma_Name = MainActivity.KomaNames[22];
            } else if (iAM_PM == 1 && 15000 >= iSum_Time && iSum_Time >= 9600) {//14時40分~16時10分
                iKoma = 54;
                strKoma = "(金曜日4限)";
                strKoma_Name = MainActivity.KomaNames[23];
            } else if (iAM_PM == 1 && 21000 >= iSum_Time && iSum_Time >= 15600) {//16時20分~17時50分
                iKoma = 55;
                strKoma = "(金曜日5限)";
                strKoma_Name = MainActivity.KomaNames[24];
            }
        }
        return iKoma;
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


}
