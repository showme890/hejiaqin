package com.chinamobile.hejiaqin.business.ui.basic.view.sidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chinamobile.hejiaqin.R;

/**
 * Created by Administrator on 2016/1/8.
 */
public class SideBarView extends View {
    private String[] data = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                              "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
    private int selectPos = -1;

    private static final int DEFAULT_NORMAL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_PRESS_COLOR = Color.parseColor("#1F000000");
    private static final int DEFAULT_TEXT_SIZE = 30;
    private static final int DEFAULT_NOR_TEXT_COLOR = Color.parseColor("#404040");
    private static final int DEFAULT_PRESS_TEXT_COLOR = Color.parseColor("#ff000000");

    private int sideBarBgNorColor;
    private int sideBarBgPressColor;
    private int sideBarTextSize;
    private int sideBarNorTextColor;
    private int sideBarPressTextColor;

    public SideBarView(Context context) {
        this(context, null);
    }

    public SideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public SideBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SideBarView,
                defStyleAttr, 0);
        sideBarBgNorColor = typedArray.getColor(R.styleable.SideBarView_sidebar_nor_background,
                DEFAULT_NORMAL_COLOR);
        sideBarBgPressColor = typedArray.getColor(R.styleable.SideBarView_sidebar_press_background,
                DEFAULT_PRESS_COLOR);
        sideBarTextSize = typedArray.getInt(R.styleable.SideBarView_sidebar_text_size,
                DEFAULT_TEXT_SIZE);
        sideBarNorTextColor = typedArray.getColor(R.styleable.SideBarView_sidebar_text_color_nor,
                DEFAULT_NOR_TEXT_COLOR);
        sideBarPressTextColor = typedArray.getColor(
                R.styleable.SideBarView_sidebar_text_color_press, DEFAULT_PRESS_TEXT_COLOR);

        typedArray.recycle();

        init();
    }

    Paint paint;
    Paint paintSelect;

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(sideBarNorTextColor);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(sideBarTextSize);
        paintSelect = new Paint();
        paintSelect.setAntiAlias(true);
        paintSelect.setTypeface(Typeface.DEFAULT_BOLD);
        paintSelect.setTextSize(sideBarTextSize);
        paintSelect.setColor(sideBarPressTextColor);

    }

    int height;
    int width;
    int perHeight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getY();
        int position = (int) (x / perHeight);

        if (!isPositionValid(position)) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(sideBarBgPressColor);
                selectPos = position;
                if (listener != null) {
                    listener.onLetterSelected(data[selectPos]);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (position != selectPos) {
                    //切换到其他字母
                    selectPos = position;
                    if (listener != null) {
                        listener.onLetterChanged(data[selectPos]);
                    }
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(sideBarBgNorColor);
                if (listener != null) {
                    listener.onLetterReleased(data[selectPos]);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private boolean isPositionValid(int position) {
        return position >= 0 && position < data.length;
    }

    public void setData(String[] data) {
        if (data != null) {
            this.data = data;
            this.invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        height = getHeight();
        width = getWidth();
        perHeight = height / data.length;
        for (int i = 0; i < data.length; i++) {
            canvas.drawText(data[i], width / 2 - paint.measureText(data[i]) / 2, perHeight * i
                    + perHeight, paint);
            if (selectPos == i) {
                canvas.drawText(data[i], width / 2 - paint.measureText(data[i]) / 2, perHeight * i
                        + perHeight, paintSelect);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveMeasure(widthMeasureSpec, true);
        int height = resolveMeasure(heightMeasureSpec, false);
        setMeasuredDimension(width, height);
    }

    private int resolveMeasure(int measureSpec, boolean isWidth) {

        int result = 0;
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop()
                + getPaddingBottom();

        // 获取宽度测量规格中的mode
        int mode = MeasureSpec.getMode(measureSpec);

        // 获取宽度测量规格中的size
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                float textWidth = paint.measureText(data[0]);
                if (isWidth) {
                    result = getSuggestedMinimumWidth() > textWidth ? getSuggestedMinimumWidth()
                            : (int) textWidth;
                    result += padding;
                    result = Math.min(result, size);
                } else {
                    result = size;
                    result = Math.max(result, size);
                }

                break;
            default:
                break;
        }

        return result;
    }

    /***/
    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) dp2px(25);
    }

    /***/
    public interface LetterSelectListener {
        /***/
        void onLetterSelected(String letter);

        /***/
        void onLetterChanged(String letter);

        /***/
        void onLetterReleased(String letter);
    }

    private LetterSelectListener listener;

    public void setOnLetterSelectListen(LetterSelectListener listen) {
        this.listener = listen;
    }

}
