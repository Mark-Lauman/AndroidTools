/* Copyright (c) 2014 Mark Christopher Lauman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.                                        */
package ca.marklauman.tools;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/** Selection Utility class I made because I hate android's
 *  selection implementation. This should work for all
 *  versions of Android down to API v4.
 *  @author Mark Lauman                                  */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CursorSelAdapter extends SimpleCursorAdapter {
	
	/** Default background color id for selected items */
	public static final int SEL_COLOR = R.color.list_activated_holo;
	
	
	/** Normal adapter that does not indicate choices. */
    public static final int CHOICE_MODE_NONE = ListView.CHOICE_MODE_NONE;
	/** The adapter allows up to one choice. */ 
	public static final int CHOICE_MODE_SINGLE = ListView.CHOICE_MODE_SINGLE;
	/** The adapter allows multiple choices.  */
	public static final int CHOICE_MODE_MULTIPLE = ListView.CHOICE_MODE_MULTIPLE;

    /** Default background color of a selected list item. */
    private static int backDefSelect = -1;
	
	/** Background of an unselected list item. */
	final private Drawable backNorm;
	/** Background color of a selected list item. */
	private int backSelect = -1;
	
	
	/** Current choice mode.   */
	private int mChoiceMode = CHOICE_MODE_NONE;
	/** Current selections.    */
	final protected HashSet<Long> mSelected = new HashSet<>();
    /** The column id for the _id column */
    protected int _id;
	
	
	/** Standard constructor.
	 *  @param context The application context
	 *  @param layout resource identifier of a layout file that defines
     *  the views for this list item. The layout file should include at
     *  least those named views defined in "to"
	 *  @param from A list of column names representing the data to bind
     *  to the UI. Can be null if the cursor is not available yet.
	 *  @param to The views that should display column in the "from" parameter.
     *  These should all be TextViews. The first N views in this list are given
	 *  the values of the first N columns in the from parameter. Can be null
     *  if the cursor is not available yet. */
	public CursorSelAdapter(Context context, int layout,
			                String[] from, int[] to) {
		super(context, layout, null, from, to, 0);
        backNorm = View.inflate(context, layout, null).getBackground();
		if(backDefSelect == -1)
            backDefSelect = ContextCompat.getColor(context, SEL_COLOR);
        backSelect = backDefSelect;
	}


    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        if(cursor != null) _id = cursor.getColumnIndex(BaseColumns._ID);
    }
	
	
	/** Gets the view for a specified position in the list. In
     *  {@link SimpleCursorAdapter}s, this method is not responsible for the
     *  inflation of the view, just its retrieval and refreshing.
     *  Inflation is done in {@link #newView(Context, Cursor, ViewGroup)}.
	 *  @param position The position of the item within the
	 *  adapter's data set of the item whose view we want.
	 *  @param convertView The old view to reuse, if possible.
	 *  @param parent The parent that this view will eventually be attached to
	 *  @return A View corresponding to the data at the specified position. */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
        mCursor.moveToPosition(position);
        return showSelection(convertView, mCursor,
                             mSelected.contains(mCursor.getLong(_id)));
	}

    /** Changes a view to reflect its selection status.
     *  The default implementation changes the background color.
     *  @param view The view to update.
     *  @param cursor The cursor positioned at the current item.
     *                If you change the cursor's position it won't matter.
     *  @param selected True if this item is selected right now.
     *  @return The original view, modified to indicate the selection. */
    @SuppressWarnings("UnusedParameters")
	protected View showSelection(View view, Cursor cursor, boolean selected) {
        if(selected) view.setBackgroundColor(backSelect);
        else if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            //noinspection deprecation
            view.setBackgroundDrawable(backNorm);
        else view.setBackground(backNorm);
        return view;
    }


	/** Get the item position paired with this id.
	 *  @param id The id of the item according to the cursor.
	 *  @return The position of that item in the list,
	 *  or -1 if no item exists with that id. */
	public int getPosition(long id) {
		if(mCursor == null || !mCursor.moveToFirst())
			return -1;
		do {
			if(id == mCursor.getLong(mRowIDColumn))
				return mCursor.getPosition();
		} while(mCursor.moveToNext());
		return -1;
	}
	
	
	/** <p>Defines the choice behavior for the Adapter. By default,
     *  Adapters do not have any choice behavior ({@link #CHOICE_MODE_NONE}).
	 *  By setting the choiceMode to {@link #CHOICE_MODE_SINGLE},
     *  the Adapter allows up to one item to be selected. By setting the
	 *  choiceMode to {@link #CHOICE_MODE_MULTIPLE}, the list allows any
     *  number of items to be chosen.</p>
	 *  <p>Calling this method will deselect all items. Be sure to call
     *  {@link #getSelected()} before this if you want to preserve
     *  your selections.</p>
	 *  @param choiceMode  One of {@link #CHOICE_MODE_NONE},
	 *  {@link #CHOICE_MODE_SINGLE}, or {@link #CHOICE_MODE_MULTIPLE}. */
	public void setChoiceMode(int choiceMode) {
		switch(choiceMode) {
		case CHOICE_MODE_NONE:		case CHOICE_MODE_SINGLE:
		case CHOICE_MODE_MULTIPLE:	break;
		default:	throw new InvalidParameterException("Invalid choice mode");
		}
		mChoiceMode = choiceMode;
		mSelected.clear();
		notifyDataSetChanged();
	}
	
	
	/** Gets the current choice mode of this CursorSelAdapter.
	 *  @return One of {@link #CHOICE_MODE_NONE},
	 *  {@link #CHOICE_MODE_SINGLE}, or
	 *  {@link #CHOICE_MODE_MULTIPLE}.                      */
	public int getChoiceMode() {
		return mChoiceMode;
	}
	
	
	/** Select this item. If it is already selected it remains so.
	 *  @param id The id of the item in the cursor. */
	public void selectItem(long id) {
		switch(mChoiceMode) {
		case CHOICE_MODE_NONE:
			return;
		case CHOICE_MODE_SINGLE:
			mSelected.clear();
		case CHOICE_MODE_MULTIPLE: break;
		default: 	throw new IllegalStateException("Choice Mode is an invalid value: " + mChoiceMode);
		}
		mSelected.add(id);
		notifyDataSetChanged();
	}
	
	
	/** Deselects this item. If it is already deselected then it remains so.
     *  @param id The id of the item in the cursor. */
	public void deselectItem(long id) {
		mSelected.remove(id);
		notifyDataSetChanged();
	}
	
	
	/** If the list item at the given location is selected, deselects it.
     *  If it is not selected, selects it.
	 *  @param id The id of the item in the cursor.
	 *  @return {@code true} if the item is selected, {@code false} otherwise. */
	public boolean toggleItem(long id) {
		switch(mChoiceMode) {
		case CHOICE_MODE_NONE:
			return false;
		case CHOICE_MODE_SINGLE:
			mSelected.clear();
		case CHOICE_MODE_MULTIPLE: break;
		default: 	throw new IllegalStateException("Choice Mode is an invalid value: " + mChoiceMode);
		}

        boolean selected = mSelected.contains(id);
		if(selected) mSelected.remove(id);
		else mSelected.add(id);
		notifyDataSetChanged();
		return !selected;
	}


    /** Set the background used on selected items */
    public void setSelectionColor(int color) {
        backSelect = color;
    }
	
	
	/** All items are selected. Choice mode changes to
     *  {@link #CHOICE_MODE_MULTIPLE}. */
	public void selectAll() {
		mChoiceMode = CHOICE_MODE_MULTIPLE;
		mSelected.clear();
		if(mCursor == null) return;

        mCursor.moveToPosition(-1);
        while(mCursor.moveToNext())
            mSelected.add(mCursor.getLong(_id));
		notifyDataSetChanged();
	}
	
	
	/** All items are deselected. Choice mode is unchanged */
	public void deselectAll() {
		mSelected.clear();
		notifyDataSetChanged();
	}
	
	
	/** All items in the list are selected. If they are already all selected,
     *  then everything is deselected instead.
	 *  Choice mode changes to {@link #CHOICE_MODE_MULTIPLE} regardless.
	 *  @return {@code true} if all items are selected.   */
	public boolean toggleAll() {
		mChoiceMode = CHOICE_MODE_MULTIPLE;
		
		// check all items are selected
		boolean selected = true;
        mCursor.moveToPosition(-1);
        while(mCursor.moveToNext())
            selected = mSelected.contains(mCursor.getLong(_id));
		
		// apply the change
		if(selected) deselectAll();
		else selectAll();
		return !selected;
	}
	
	
	/** Get the selected item positions.
	 *  Returns a little bit slower than {@link #getSelected()}.
	 *  @return The positions of each selected item in
	 *  the list. (not necessarily the sql ids of the items)                               */
	public int[] getSelectedPos() {
		int[] res = new int[mSelected.size()];
        mCursor.moveToPosition(-1);
        int i = 0;
        while(mCursor.moveToNext()) {
            if(mSelected.contains(mCursor.getLong(_id))) {
                res[i] = mCursor.getPosition();
                i++;
            }
        }
		return res;
	}

    /** Get the selected item positions as Integers (non-primitive).
     *  Returns at the same speed as {@link #getSelectedPos()}
     *  as it is computationally identical.
     *  @return The positions of each selected item in
     *  the list. (not necessarily the sql ids
     *  of the items)                               */
    public Integer[] getSelectedPosInt() {
        Integer[] res = new Integer[mSelected.size()];
        mCursor.moveToPosition(-1);
        int i = 0;
        while(mCursor.moveToNext()) {
            if(mSelected.contains(mCursor.getLong(_id))) {
                res[i] = mCursor.getPosition();
                i++;
            }
        }
        return res;
    }
	
	
	/** Gets all selected item ids.
	 *  @return The ids of each selected item. There is
	 *  no guaranteed order to this list, users must sort
	 *  it themselves if necessary.                    */
	public long[] getSelected() {
		long[] res = new long[mSelected.size()];
        int i = 0;
        for(long id : mSelected) {
            res[i] = id;
            i++;
		}
		return res;
	}


    /** Gets all selected item ids.
     *  Before it returns, it verifies that the selections
     *  are in the cursor. This slows it down compared to
     *  {@link #getSelected()}
     *  @return The ids of each selected item. There is
     *  no guaranteed order to this list, users must sort
     *  it themselves if necessary.                    */
    public long[] getSelectedVerify() {
        if(mCursor == null || ! mCursor.moveToFirst())
            return new long[0];

        ArrayList<Long> res1 = new ArrayList<>(mSelected.size());
        do {
            long id = mCursor.getLong(_id);
            if(mSelected.contains(id)) res1.add(id);
        } while (mCursor.moveToNext());

        long[] res2 = new long[res1.size()];
        for(int i=0; i<res2.length; i++)
            res2[i] = res1.get(i);
        return res2;
    }

    /** Gets all selected item ids as Longs (not longs).
     *  Computationally identical to {@link #getSelected()}.
     *  @return The ids of each selected item. There is
     *  no guaranteed order to this list, users must sort
     *  it themselves if necessary.                    */
    public Long[] getSelectedLong() {
        return (Long[]) mSelected.toArray();
    }
	
	
	/** <p>Sets the selected items. Be sure to set the choice mode
     *  (using {@link #setChoiceMode(int)}) before calling this!</p>
	 *  <p>{@link #CHOICE_MODE_NONE}: nothing is selected.<br>
	 *  {@link #CHOICE_MODE_SINGLE}: only the last item is selected.<br>
	 *  {@link #CHOICE_MODE_MULTIPLE}: all specified items are selected.</p>
	 *  @param ids The ids of the new selections as per the cursor. */
	public void setSelected(long... ids) {
		deselectAll();
		if(ids == null || ids.length == 0 || mChoiceMode == CHOICE_MODE_NONE)
			return;
		for(long id : ids) selectItem(id);
	}

    /** <p>Sets the selected items. Be sure to set the choice mode
     *  (using {@link #setChoiceMode(int)}) before calling this!</p>
     *  <p>{@link #CHOICE_MODE_NONE}: nothing is selected.<br>
     *  {@link #CHOICE_MODE_SINGLE}: only the last item is selected.<br>
     *  {@link #CHOICE_MODE_MULTIPLE}: all specified items are selected.</p>
     *  @param ids The ids of the new selections as per the cursor. */
    public void setSelected(Long... ids) {
        deselectAll();
        if(ids == null || ids.length == 0 || mChoiceMode == CHOICE_MODE_NONE)
            return;
        for(long id : ids) selectItem(id);
    }
}