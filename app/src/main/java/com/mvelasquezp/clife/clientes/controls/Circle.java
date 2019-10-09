package com.mvelasquezp.clife.clientes.controls;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.mvelasquezp.clife.clientes.R;

public class Circle extends View {

    private static final int START_ANGLE_POINT = 90;

    private Paint paint;
    private RectF rect;

    private float angle;
    private float circleStrokeWidth;
    private int circleBackgroundColor;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributesAndSetupField(context, attrs);
        setUpCirclePaint();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //final int strokeWidth = 12;
        final int strokeWidth = Math.round(circleStrokeWidth);
        final int circleWidth = getMeasuredWidth() - 2 * strokeWidth;
        final int circleHeigth = getMeasuredHeight() - 2 * strokeWidth;
        /*paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(getResources().getColor(R.color.colorCicleActive));*/
        rect = new RectF(strokeWidth, strokeWidth, circleWidth + strokeWidth, circleHeigth + strokeWidth);
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    private void readAttributesAndSetupField(Context context, AttributeSet attrs){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.Circle,0,0);
        try {
            applyAttirbutes(context, typedArray);
        }finally {
            typedArray.recycle();
        }
    }

    private void applyAttirbutes(Context context, TypedArray typedArray){
        readColorFromAttributes(context, typedArray);
        circleStrokeWidth = typedArray.getDimension(R.styleable.Circle_circleStrokeWidth, getDefaultStrokeWidth(context));

    }

    private void readColorFromAttributes(Context context, TypedArray typedArray){
        ColorStateList colorStateList = typedArray.getColorStateList(R.styleable.Circle_circleBackgroundColor);
        if(colorStateList != null){
            circleBackgroundColor = colorStateList.getDefaultColor();
        }
    }

    private int getDefaultStrokeWidth(Context context){
        return (int) context.getResources().getDisplayMetrics().density * 10;
    }

    private void setUpCirclePaint() {
        paint = new Paint();
        paint.setColor(circleBackgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleStrokeWidth);
        paint.setAntiAlias(true);
    }

    public void ModifyLineColor(int lineColor) {
        circleBackgroundColor = getResources().getColor(lineColor);
        setUpCirclePaint();
    }
}
