package ca.marklauman.tools.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

/** A class designed to add ListView style dividers between RecyclerView items. */
@SuppressWarnings("unused")
public class ListDivider extends RecyclerView.ItemDecoration {
    /** The divider that will be drawn. */
    private final Drawable mDivider;

    /** Construct the divider using the default ListView separator. */
    public ListDivider(Context context) {
        // Get the default ListView divider.
        final TypedArray arr = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDivider = arr.getDrawable(0);
        arr.recycle();
    }


    /** Construct the divider using a specified drawable as the divider. */
    public ListDivider(Context context, int resId) {
        mDivider = ContextCompat.getDrawable(context, resId);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
