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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/** <p>Deprecated in favor of {@link MultiSelectPreference}</p>
 *  <p></p>An implementation of MultiSelectImagePreference
 *  for Android 2 with icons included beside
 *  the list items.</p>
 *  @author Mark Lauman & Krzysztof Suszynski.
 *  This is a heavily modified version of
 *  Krzysztof Suszynski's MultiSelectListPreference:
 *  https://gist.github.com/cardil/4754571&nbsp;. */
@Deprecated
public class MultiSelectImagePreference extends ListPreference {
	
	/** The view used by this preference for items in
	 *  the popup list.                            */
	public static final int LIST_ITEM_VIEW = R.layout.list_item_check;
	
	/** Separator between list entries. */
    private static final String SEPARATOR = "\u0001\u0007\u001D\u0007\u0001";
    /** Adapter used to track selections */
    private ArrayCheckAdapter<CharSequence> adapt;
    /** Icon resources used by the adapter */
    private int[] icons = null;
    /** If this preference is inverted, it saves the
     *  items that are NOT selected */
    private boolean inverted = false;
    
    
    public MultiSelectImagePreference(Context context) {
        this(context, null);
    }
    
    public MultiSelectImagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        
        // load inversion settings
        isInverted(attributeSet);
        
        // setup the adapter
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null
                || entries.length != entryValues.length) {
            throw new IllegalStateException(
                    "MultiSelectImagePreference requires an entries array and an entryValues "
                            + "array which are both the same length");
        }
        adapt = new ArrayCheckAdapter<>(getContext(),
        								LIST_ITEM_VIEW,
        								entries);
        adapt.setChoiceMode(ArrayCheckAdapter.CHOICE_MODE_MULTIPLE);
        adapt.setIcons(getEntryIcons(attributeSet));
        
        // setup the summary
        CharSequence[] values = getValues(getValue());
        ArrayList<String> listValues = new ArrayList<>(values.length);
        for(CharSequence val : values)
        	listValues.add("" + val);
        setSummary(prepareSummary(listValues));
    }
    
    
    /** Load the icons used for the list entries
     *  into {@link #icons}. After this is called,
     *  {@link #getEntryIcons()} will return the same
     *  as this function - and run faster. Before it
     *  is called, {@link #getEntryIcons()} always
     *  returns {@code null}.
     *  @param attrs The attributes provided
     *  to this {@code MultiSelectImagePreference}.
     *  The icons are retrieved from this parameter.
     *  @return The resource ids of each icon in the
     *  list. These icons appear in the order of
     *  the entries themselves.                   */
    private int[] getEntryIcons(AttributeSet attrs) {
    	icons = null;
    	if(attrs == null) return null;
    	int list_id = attrs.getAttributeResourceValue(null, "icons", 0);
    	if(list_id == 0) return null;
    	
    	TypedArray ta = getContext().getResources()
    								.obtainTypedArray(list_id);
    	if(ta == null) return icons;
    	icons = new int[ta.length()];
    	for(int i=0; i<ta.length(); i++) {
    		icons[i] = ta.getResourceId(i, -1);
    	}
    	ta.recycle();
    	return icons;
    }
    
    
    /** Get the icons used for the list entries.
     *  @return The resource ids of each icon in the
     *  list. These icons appear in the order of
     *  the entries themselves.                   */
    public int[] getEntryIcons() {
    	return icons;
    }
    
    /** <p>Check if this preference is inverted in its
     *  xml description. Inverted preferences save
     *  items that are NOT selected.</p>
     *  <p>After this is called once
     *  {@link #isInverted()} returns the same thing
     *  and is faster. Before this is called,
     *  {@link #isInverted()} returns {@code null}.</p>
     *  @param attrs The attributes provided
     *  to this {@code MultiSelectImagePreference}.
     *  The setting is retrieved from this parameter.
     *  @return {@code true} if this is inverted */
    private boolean isInverted(AttributeSet attrs) {
    	inverted = attrs.getAttributeBooleanValue(null, "inverted", false);
    	return inverted;
    }
    
    /** Check if this preference is inverted.
     *  Inverted preferences save items that are
     *  NOT selected.
     *  @return {@code true} if this is inverted */
    public boolean isInverted() {
    	return inverted;
    }
    
    
    @Override
    protected void onPrepareDialogBuilder(@NonNull Builder builder) {
        restoreCheckedEntries();
        
        ListView list = new ListView(getContext());
        list.setBackgroundColor(Color.WHITE);
        list.setAdapter(adapt);
        OnItemClickListener listener = new OnItemClickListener() {
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				adapt.toggleItem(position);
			}
        };
        list.setOnItemClickListener(listener);
        builder.setView(list);
    }
    

    private void restoreCheckedEntries() {
        // get preference state
        CharSequence[] saved = getValues(getValue());
        
        if (saved == null || saved.length == 0) {
        	if(inverted) adapt.selectAll();
        	else adapt.deselectAll();
        	return;
        }
        
        List<CharSequence> savedList = Arrays.asList(saved);
        CharSequence[] values = getEntryValues();
        ArrayList<Integer> selections = new ArrayList<>(savedList.size());
        if(inverted) {
        	for (int i = 0; i < values.length; i++) {
            	if(!savedList.contains(values[i]))
                	selections.add(i);
            }
        } else {
        	for (int i = 0; i < values.length; i++) {
            	if(savedList.contains(values[i]))
                	selections.add(i);
            }
        }
        adapt.setSelections(selections);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
    	if(!positiveResult) return;
    	
    	CharSequence[] entryValues = getEntryValues();
    	Integer[] select;
    	if(inverted) select = adapt.getUnselected();
    	else select = adapt.getSelections();
        List<String> values = new ArrayList<>();
        if (select != null) {
        	for(int id : select)
        		values.add("" + entryValues[id]);
            String value = join(values, SEPARATOR);
            setSummary(prepareSummary(values));
            setValueAndEvent(value);
        }
    }
    
    
    private void setValueAndEvent(String value) {
        if (callChangeListener(getValues(value))) {
            setValue(value);
        }
    }
    
    private CharSequence prepareSummary(List<String> joined) {
        List<String> titles = new ArrayList<>();
        CharSequence[] entryTitle = getEntries();
        CharSequence[] entryValues = getEntryValues();
        int ix = 0;
        if(inverted) {
        	for (CharSequence value : entryValues) {
                if (!joined.contains(value + "")) {
                    titles.add((String) entryTitle[ix]);
                }
                ix += 1;
            }
        } else {
        	for (CharSequence value : entryValues) {
                if (joined.contains(value + "")) {
                    titles.add((String) entryTitle[ix]);
                }
                ix += 1;
            }
        }
        return join(titles, ", ");
    }

    @Override
    protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
        return typedArray.getTextArray(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue,
            Object rawDefaultValue) {
        String value;
        CharSequence[] defaultValue;
        if (rawDefaultValue == null) {
            defaultValue = new CharSequence[0];
        } else {
            defaultValue = (CharSequence[]) rawDefaultValue;
        }
        List<CharSequence> joined = Arrays.asList(defaultValue);
        String joinedDefaultValue = join(joined, SEPARATOR);
        if (restoreValue) {
            value = getPersistedString(joinedDefaultValue);
        } else {
            value = joinedDefaultValue;
        }

        setSummary(prepareSummary(Arrays.asList(getValues(value))));
        setValueAndEvent(value);
    }

    /** Joins array of object to single string by separator
     *  Credits to kurellajunior on this post
     *  http://snippets.dzone.com/posts/show/91
     *   @param iterable any kind of iterable
     *   ex.: <code>["a", "b", "c"]</code>
     *  @param separator separates entries
     *  ex.: <code>","</code>
     *  @return joined string
     *  ex.: <code>"a,b,c"</code>                  */
    private static String join(Iterable<?> iterable, String separator) {
        Iterator<?> oIter;
        if (iterable == null || (!(oIter = iterable.iterator()).hasNext()))
            return "";
        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
        while (oIter.hasNext())
            oBuilder.append(separator).append(oIter.next());
        return oBuilder.toString();
    }
    
    @Override
    protected void onBindView(@NonNull View view) {
    	super.onBindView(view);
    	TextView summary = (TextView) view.findViewById(android.R.id.summary);
    	if(summary != null) {
    		summary.setEllipsize(TruncateAt.END);
    		summary.setLines(2);
    	}
    }
    
    
    /** Save a value to memory using the
     *  {@code MultiSelectImagePreference} format.
     *  @param prefs The preferences to save the values into.
     *  @param key The key associated with this preference value.
     *  @param values The values you wish placed there.        */
    public static void saveValue(SharedPreferences prefs, String key, Collection<? extends String> values) {
    	prefs.edit()
    		 .putString(key, join(values, SEPARATOR))
    		 .commit();
    }
    
    /** Extract the values stored by a {@code MultiSelectImagePreference}.
     *  (Retrieve the values as a String and pass them to this)
     *  @param val The value stored in the preferences
     *  @return The values stored inside that.      */
    public static String[] getValues(CharSequence val) {
        if (val == null || "".equals(val)) {
            return new String[0];
        } else {
            return ((String) val).split(SEPARATOR);
        }
    }
}