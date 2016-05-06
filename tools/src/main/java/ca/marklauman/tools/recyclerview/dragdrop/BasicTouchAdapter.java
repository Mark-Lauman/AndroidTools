package ca.marklauman.tools.recyclerview.dragdrop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/** A simple adapter that supports drag, drop and dismiss.
 *  @author Mark Lauman */
public abstract class BasicTouchAdapter<T extends RecyclerView.ViewHolder>
                extends RecyclerView.Adapter<T> {

    /** Handles the drag and dismiss events for us. */
    private final ItemTouchHelper mItemTouchHelper;


    /** Basic Constructor.
     *  @param recyclerView The RecyclerView this adapter will be attached to.
     *  @param callback Callback that is used to define swipe and drag behaviour.
     *                  The {@link TouchCallback} class offers automatic construction of these
     *                  callback objects. */
    public BasicTouchAdapter(@NonNull RecyclerView recyclerView, @NonNull Callback callback) {
        callback.attachAdapter(this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }


    /** Called when an item has been dragged far enough to trigger a move.
     *  This is called every time an item is shifted, and not at the end of a "drop" event.
     *  @param fromPosition The start position of the moved item.
     *  @param toPosition   Then end position of the moved item. */
    public abstract void onItemMove(int fromPosition, int toPosition);


    /** Called when an item is dismissed via swipe.
     *  @param position The position of the item being dismissed. */
    public abstract void onDismiss(int position);


    /** Start dragging the passed ViewHolder. */
    public void startDrag(T viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    /** Used to define the mouse callbacks used to trigger drag events */
    public static abstract class Callback extends ItemTouchHelper.Callback {
        public abstract void attachAdapter(BasicTouchAdapter adapter);
    }
}