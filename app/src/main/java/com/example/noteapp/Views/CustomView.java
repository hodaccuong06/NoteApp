package com.example.noteapp.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

public class CustomView extends View {
    private Paint petalPaint;
    private Paint centerPaint;
    private Path petalPath;
    private RectF petalOval;
    private Paint textPaint,textPaint1;
    private Handler handler = new Handler();
    private Random random = new Random();

    private Runnable colorChangeRunnable = new Runnable() {
        @Override
        public void run() {
            textPaint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            petalPaint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            centerPaint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            textPaint1.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            invalidate();
            handler.postDelayed(this, 500);
        }
    };


    public CustomView(Context context) {
        super(context);
        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        petalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        petalPaint.setColor(Color.parseColor("#FFA500")); // Orange color for the petals
        petalPaint.setStyle(Paint.Style.FILL);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.BLUE);
        centerPaint.setStyle(Paint.Style.FILL);

        petalPath = new Path();

        petalOval = new RectF();


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(100);

        textPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textPaint1.setColor(Color.BLACK);
        textPaint1.setTextSize(75);

        handler.post(colorChangeRunnable);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        float width = getWidth();
        float height = getHeight();

        String text = "HUTECH";
        String text1 ="Đại học Công nghệ Tp.HCM";
        float textWidth = textPaint.measureText(text);
        float x = (getWidth() - textWidth) / 2;
        float y = (getHeight() / 5) - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(text, x, y, textPaint);
        float textWidth1 = textPaint1.measureText(text1);
        float x1 = (getWidth() - textWidth1) / 2;
        float y1 = (getHeight() / 4) - ((textPaint1.descent() + textPaint1.ascent()) / 2);
        canvas.drawText(text1, x1, y1, textPaint1);

        float centerRadius = 70;

        float petalWidth = 50;
        float petalLength = 200;



        float cornerOffsetX = petalWidth + centerRadius;
        float cornerOffsetY = petalLength + centerRadius;


        float[][] corners = {
                {cornerOffsetX + 100, cornerOffsetY -50},
                {getWidth() - cornerOffsetX - 100, cornerOffsetY -50},
                {cornerOffsetX + 100, getHeight() - cornerOffsetY +50},
                {getWidth() - cornerOffsetX - 100, getHeight() - cornerOffsetY+50}
        };

        for (float[] corner : corners) {
            for (int i = 0; i < 8; i++) {
                petalPaint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

                canvas.save();
                canvas.rotate(45 * i, corner[0], corner[1]);
                petalOval.set(corner[0] - petalWidth, corner[1] - petalLength , (float) (corner[0] + petalWidth /1.5), corner[1]-50);
                petalPath.reset();
                petalPath.addOval(petalOval, Path.Direction.CW);
                canvas.drawPath(petalPath, petalPaint);
                canvas.restore();
            }

            canvas.drawCircle(corner[0], corner[1], centerRadius, centerPaint);
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(colorChangeRunnable);
    }


}
