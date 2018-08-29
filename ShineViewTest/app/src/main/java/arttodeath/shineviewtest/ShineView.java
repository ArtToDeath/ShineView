package arttodeath.shineviewtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;


/**
 * Created by ThinkPad on 2018/8/28.
 */
public class ShineView extends View {
    private static final String TAG = "ShineView";

    //实际大小
    private int mWidth = 200;
    private int mHeight = 360;

    //绘制大小
    private int mDrawWidth = mWidth - 20;
    private int mDrawHeight = mHeight - 20;

    private int mStartX = 10;
    private int mStartY = 10;

    private Paint mDstPaint;
    private Paint mSrcPaint;
    private Paint mLightPaint;

    private RectF mDstRect;
    private RectF mSrcRect;

    private int backgroundColor;
    private int foregroundColor;
    private int roundRadius;
    private float lightRadius;

    private int value = 50;//0~100
    private int valueHeight;//值对应高度

    private PorterDuffXfermode xfermode;

    private BlurMaskFilter blurMaskFilter;

    private OnValueChangeListener mOnValueChangeListener;

    public ShineView(Context context) {
        this(context, null);
    }

    public ShineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initPaint();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ShineView);
        try {
            //
            backgroundColor = typedArray.getColor(R.styleable.ShineView_backgroundColor, 0x5900ffdd);
            foregroundColor = typedArray.getColor(R.styleable.ShineView_foregroundColor, 0x8d0b0000);
            roundRadius = typedArray.getDimensionPixelOffset(R.styleable.ShineView_roundRadius, 10);
        }finally {
            typedArray.recycle();
        }

    }

    private void initPaint() {

        if(mDstPaint == null){
            mDstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mDstPaint.setStyle(Paint.Style.FILL);
        mDstPaint.setColor(backgroundColor);

        if(mSrcPaint == null){
            mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mSrcPaint.setStyle(Paint.Style.FILL);
        mSrcPaint.setColor(foregroundColor);

        //光亮
        if (mLightPaint == null){
            mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mLightPaint.setStyle(Paint.Style.FILL);
        mLightPaint.setColor(Color.WHITE);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        blurMaskFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //为了在API14以上版本获得模糊效果
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        if (isTouch){//按下-放大
            mDstRect = new RectF(0, 0, mWidth, mHeight);
            valueHeight = mHeight - value*mHeight/100;
            mSrcRect = new RectF(0, 0, mWidth, valueHeight);

            lightRadius = mWidth/8 + 10;
        }else{//缩小
            mDstRect = new RectF(mStartX, mStartY, mDrawWidth+mStartX, mDrawHeight+mStartY);
            valueHeight = mDrawHeight+mStartY - value*mDrawHeight/100;
            mSrcRect = new RectF(mStartX, mStartY, mDrawWidth+mStartX, valueHeight);
            lightRadius = mWidth/8;
        }

        int layer = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(),
                null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(mDstRect, roundRadius, roundRadius, mDstPaint);

        mSrcPaint.setXfermode(xfermode);
        canvas.drawRect(mSrcRect, mSrcPaint);
        mSrcPaint.setXfermode(null);

        int light = 155 + value;//155~255
        mLightPaint.setColor(Color.argb(255, light, light, light));
        mLightPaint.setMaskFilter(blurMaskFilter);
        canvas.drawCircle(mWidth/2, mHeight*3/4, lightRadius, mLightPaint);

        canvas.restoreToCount(layer);
    }

    //h:0~100
    private void refreshProgress(int h){
        value = h;
        if (mOnValueChangeListener != null){
            mOnValueChangeListener.onValueChanged(value);
        }
        invalidate();
    }

    private float mDownY;
    private boolean isTouch;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                isTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float instance = event.getY() - mDownY;
                int variate = value - (int)(instance / 100);
                if(variate > 100){
                    variate = 100;
                }else if (variate < 0){
                    variate = 0;
                }
                Log.d(TAG, "variate:"+variate);
                refreshProgress(variate);
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener){
        this.mOnValueChangeListener = listener;
    }

    interface OnValueChangeListener{
        void onValueChanged(int value);
    }
}
