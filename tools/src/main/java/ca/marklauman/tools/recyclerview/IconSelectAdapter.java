package ca.marklauman.tools.recyclerview;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/** A simple adapter that displays an array of items (and optionally icons for those items).
 *  An item can be selected with {@link #select(int)} to change its background to selectRes.
 *  A listener can be added with {@link #setListener(ClickListener)} to learn when items are clicked.
 *  Items are not selected when clicked - you need to call {@link #select(int)} yourself.
 *  @author Mark Lauman */
public class IconSelectAdapter extends RecyclerView.Adapter<IconSelectAdapter.ViewHolder> {

    /** The view resource inflated for each list item */
    private final int mViewRes;
    /** Drawable resource id used for the selected item's background. */
    private final int mSelectBack;
    /** The values on display in this adapter. */
    private final String[] mValues;
    /** The icons paired to the values */
    private final int[] mIcons;
    /** Listener to notify if items are clicked. */
    private ClickListener mListen;
    /** Currently selected position. */
    private int mSelect = -1;


    /** Construct a new adapter.
     *  @param viewRes The view resource used for each list entry. This view must contain
     *                 one ImageView with the {@link android.R.id#icon} id and one
     *                 TextView with the {@link android.R.id#text1} id.
     * @param selectRes Drawable resource id used as the background of the selected item.
     * @param values The values to display in the adapter.
     * @param icons The icons to pair with those values. */
    public IconSelectAdapter(int viewRes, int selectRes, String[] values, int[] icons) {
        mViewRes = viewRes;
        mSelectBack = selectRes;
        mValues = values;
        mIcons = icons;
    }

    /** Construct a new adapter.
     *  @param viewRes The view resource used for each list entry. This view must contain
     *                 one ImageView with the {@link android.R.id#icon} id and one
     *                 TextView with the {@link android.R.id#text1} id.
     * @param selectRes Drawable resource id used as the background of the selected item.
     * @param values The values to display in the adapter. */
    public IconSelectAdapter(int viewRes, int selectRes, String[] values) {
        this(viewRes, selectRes, values, null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(mViewRes, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Toggle the background if this item is selected
        if(mSelect == position) holder.itemView.setBackgroundResource(mSelectBack);
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            //noinspection deprecation
            holder.itemView.setBackgroundDrawable(holder.defBack);
        else holder.itemView.setBackground(holder.defBack);

        // Place the icon and text in this item
        holder.text.setText(mValues[position]);
        if(mIcons != null && position < mIcons.length && mIcons[position] != 0) {
            holder.icon.setImageResource(mIcons[position]);
            holder.icon.setVisibility(View.VISIBLE);
        } else holder.icon.setVisibility(View.GONE);
    }

    /** Select the item at this position. */
    public void select(int position) {
        if(mSelect == position) return;
        int oldSel = mSelect;
        mSelect = position;
        notifyItemChanged(oldSel);
        notifyItemChanged(mSelect);
    }

    /** Set a listener that will be notified when items are clicked. */
    public void setListener(ClickListener listener) {
        mListen = listener;
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    /** ViewHolder used by this adapter */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /** Holds the icon for this item */
        public final ImageView icon;
        /** Holds the text for this item */
        public final TextView text;
        /** The drawable that the view originally used as its background */
        public final Drawable defBack;

        public ViewHolder(View view) {
            super(view);
            defBack = view.getBackground();
            icon = (ImageView) view.findViewById(android.R.id.icon);
            text  = (TextView)  view.findViewById(android.R.id.text1);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListen != null) mListen.onItemClick(getAdapterPosition());
        }
    }

    public interface ClickListener {
        /** Called when an item in an IconSelectAdapter is clicked.
         *  @param position The position of that item in the adapter. */
        void onItemClick(int position);
    }
}