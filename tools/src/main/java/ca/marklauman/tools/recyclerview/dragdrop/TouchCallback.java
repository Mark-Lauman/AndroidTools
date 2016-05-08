package ca.marklauman.tools.recyclerview.dragdrop;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/** Callback object that defines how drag, drop and swipe work
 * Created by Mark on 2016-05-05.
 */
public class TouchCallback extends BasicTouchAdapter.Callback {
    /** Possible drag or swipe flag for the constructor. Allows dragging/swiping up. */
    public static final int UP = ItemTouchHelper.UP;
    /** Possible drag or swipe flag for the constructor. Allows dragging/swiping down. */
    public static final int DOWN = ItemTouchHelper.DOWN;
    /** Possible drag or swipe flag for the constructor. Allows dragging/swiping to the start. */
    public static final int START = ItemTouchHelper.START;
    /** Possible drag or swipe flag for the constructor. Allows dragging/swiping to the end. */
    public static final int END = ItemTouchHelper.END;

    /** Has swipe to dismiss been enabled. */
    private final boolean hasDismiss;
    /** Valid movement directions for drag and swipe. */
    private final int moveFlags;
    /** The adapter this callback oversees */
    private BasicTouchAdapter mAdapter;

    /** Create a new TouchCallback with the provided setup.
     *  Both dragFlags and swipeFlags can be any combination of
     *  {@link #UP}, {@link #DOWN}, {@link #START} or {@link #END}
     *  joined together with a bitwise or ( | ).
     *  @param dragFlags The valid directions that an item can be dragged.
     *                   If 0, items cannot be dragged.
     *  @param swipeFlags The valid directions that an item can be swiped offscreen.
     *                    If 0, items cannot be swiped to dismiss them. */
    public TouchCallback(int dragFlags, int swipeFlags) {
        moveFlags = makeMovementFlags(dragFlags, swipeFlags);
        hasDismiss = 0 != swipeFlags;
        mAdapter = null;
    }

    /** Attach this TouchCallback to its {@link BasicTouchAdapter}.
     *  Automatically called by BasicTouchAdapter's constructor. */
    @Override
    public void attachAdapter(BasicTouchAdapter adapter) {
        mAdapter = adapter;
    }


    /** Get a TouchCallback built for a vertical list with drag and drop, but no dismiss. */
    public static TouchCallback forDragList() {
        return new TouchCallback(UP|DOWN, 0);
    }

    /** Get a TouchCallback built for a vertical list with swipe to dismiss. */
    public static TouchCallback forDismissList() {
        return new TouchCallback(0, START|END);
    }


    @Override
    public boolean isLongPressDragEnabled() {
        // If people want to use long press to start a drag, they must use a OnLongClickListener.
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return hasDismiss;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return moveFlags;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if(mAdapter != null)
            mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(mAdapter != null) mAdapter.onDismiss(viewHolder.getAdapterPosition());
    }
}