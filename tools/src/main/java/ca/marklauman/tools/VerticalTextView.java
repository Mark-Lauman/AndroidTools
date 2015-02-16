package ca.marklauman.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/** <p></p>A {@link TextView} that has been rotated by 90 degrees.
 *  Since it extends TextView, it can have TextView styles applied to it.
 *  By default, text is from top to bottom, but if you set
 *  {@code android:gravity="bottom"} then it is drawn bottom to top.
 *  It does loose some TextView properties, like marquee.</p>
 *
 * <p>Shamelessly stolen from
 *  <a href="http://stackoverflow.com/questions/1258275/vertical-rotated-label-in-android">
 *      here.</a></p>
 * @author Pointer Null        */
public class VerticalTextView extends TextView {
    private final boolean topDown;

    public VerticalTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        final int gravity = getGravity();
        if(Gravity.isVertical(gravity) && (gravity&Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((gravity&Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
            topDown = false;
        }else
            topDown = true;
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        // the swap is intentional - we're rotating 90 degrees
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas){
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();

        if(topDown){
            canvas.translate(getWidth(), 0);
            canvas.rotate(90);
        }else {
            canvas.translate(0, getHeight());
            canvas.rotate(-90);
        }

        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

        getLayout().draw(canvas);
        canvas.restore();
    }
}