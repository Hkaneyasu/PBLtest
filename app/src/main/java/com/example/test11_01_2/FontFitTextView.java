package com.example.test11_01_2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

public class FontFitTextView extends AppCompatTextView {
    public FontFitTextView(Context context){
        super(context);
    }

    public FontFitTextView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        resize();
    }

    private void resize()
    {

        /** 最小のテキストサイズ */
        final float MIN_TEXT_SIZE = 10f;
        /** 最大のテキストサイズ（sp単位） */
        final float MAX_TEXT_SIZE = 48f;

        int viewHeight = this.getHeight();    // Viewの縦幅
        int viewWidth = this.getWidth();    // Viewの横幅

        // テキストサイズ
        //float textSize = getTextSize();
        float textSize = MAX_TEXT_SIZE * Resources.getSystem().getDisplayMetrics().scaledDensity; // spをpixelに変換

        // Paintにテキストサイズ設定
        Paint paint = new Paint();
        paint.setTextSize(textSize);

        // テキストの縦幅取得
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

        // テキストの横幅取得
        float textWidth = paint.measureText(this.getText().toString());

        // 縦幅と、横幅が収まるまでループ
        while (viewHeight < textHeight | viewWidth < textWidth)
        {
            // 調整しているテキストサイズが、定義している最小サイズ以下か。
            if (MIN_TEXT_SIZE >= textSize)
            {
                // 最小サイズ以下になる場合は最小サイズ
                textSize = MIN_TEXT_SIZE;
                break;
            }

            // テキストサイズをデクリメント
            textSize--;

            // Paintにテキストサイズ設定
            paint.setTextSize(textSize);

            // テキストの縦幅を再取得
            fm = paint.getFontMetrics();
            textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));

            // テキストの横幅を再取得
            textWidth = paint.measureText(this.getText().toString());
        }

        // テキストサイズ設定
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

    }
}
