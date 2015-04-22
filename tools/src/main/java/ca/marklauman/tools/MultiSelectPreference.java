/* Copyright (c) 2015 Mark Christopher Lauman
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

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

/** Deprecated in favor of
 *  {@link ca.marklauman.tools.preferences.MultiSelectPreference}. */
@Deprecated
public class MultiSelectPreference extends ListPreference {

    /** If this preference is inverted (as per the class description). */
    public boolean inverted;
    /** If nothing is selected, this displays as the summary. */
    public CharSequence defaultSummary;

    /** The separator between saved entries */
    private static final String SEPARATOR = ",";
    /** The view id used for items in the popup list. */
    private final int item_view_id = R.layout.list_item_check;
    /** Adapter used to track selections */
    private ArrayCheckAdapter<CharSequence> adapter;



    /** Interpret the saved values in a MultiSelectPreference string.
     *  @param pref The string stored by the MultiSelectPreference.
     *  @return The indexes inside that string, or null if the string
     *  doesn't follow MultiSelectPreference's storage format.     */
    public static Integer[] parseValues(String pref) {
        if(pref == null || "".equals(pref))
            return new Integer[0];

        // Split it up and parse each segment into an int
        String[] pref_split = pref.split(SEPARATOR);
        Integer[] res = new Integer[pref_split.length];
        for(int i=0; i<pref_split.length; i++) {
            try {
                res[i] = Integer.parseInt(pref_split[i]);
            } catch (Exception e) {
                return null;
            }
        }
        return res;
    }


    /** Map a loaded MultiSelectPreference's saved string to a set of strings.
     * @param pref The value saved by the MultiSelectPreference.
     * @param mapValues The Strings to be match with the preference.
     * @return The entries of mapValues that had their indexes saved by the preference.
     *         If the preference is not stored in the MultiSelectPreference style,
     *         returns null instead.                                           */
    public static CharSequence[]  mapValues(String pref, String[] mapValues) {
        Integer[] indexes = parseValues(pref);
        return mapValues(indexes, mapValues, false);
    }


    /** Map the parsed values to a set of strings.
     *  @param pref The parsed index values saved by the preference
     *  @param strValues The strings that should be mapped to these values
     *  @param invert If true, returns the strValues that do NOT appear in pref.
     *  @return The strValues with indexes matching those found in pref,
     *          or those that don't match if invert is true.          */
    private static CharSequence[] mapValues(Integer[] pref, CharSequence[] strValues,
                                            boolean invert) {
        // quick handler for empty params
        if(strValues == null || strValues.length == 0)
            return new String[0];
        if(pref == null || pref.length == 0) {
            if(invert) return strValues;
            else return new String[0];
        }

        // Flip the selections if needed
        if(invert) {
            // The new values
            Integer[] newPref = new Integer[strValues.length - pref.length];
            // the index for pref & newPref
            int prefId=0, newId=0;
            // go through all string values
            for(int strId=0; strId<strValues.length; strId++) {
                if(strId == pref[prefId]) {
                    // this id is present, skip it and move on to next id to match
                    if (prefId < (pref.length - 1)) prefId++;
                } else {
                    // this id is not present, it must now be selected
                    newPref[newId] = strId;
                    newId++;
                }
            }
            pref = newPref;
        }

        // map values to the strings
        CharSequence[] res = new CharSequence[pref.length];
        for(int i=0; i<pref.length; i++)
            res[i] = strValues[pref[i]];
        return res;
    }



    public MultiSelectPreference(Context context) {
        super(context);
        setup(null);
    }

    public MultiSelectPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup(attributeSet);
    }

    /** Does constructor work that applies across both constructors */
    private void setup(AttributeSet attrs) {
        inverted = attrs != null &&
                   attrs.getAttributeBooleanValue(null, "inverted", false);
        defaultSummary = getSummary();

        CharSequence[] entries = getEntries();
        adapter = new ArrayCheckAdapter<>(getContext(), item_view_id, entries);
        adapter.setChoiceMode(ArrayCheckAdapter.CHOICE_MODE_MULTIPLE);

        // load the icons
        int[] icons = null;
        if(attrs != null) {
            // Get the "icons" parameter (0 is not a valid resource id)
            int list_id = attrs.getAttributeResourceValue(null, "icons", 0);
            if(list_id != 0) {
                // Get the array connected to the "icons" parameter
                TypedArray ta = getContext().getResources()
                                            .obtainTypedArray(list_id);
                if(ta != null) {
                    // Get the icon drawable ids from the array
                    icons = new int[ta.length()];
                    for(int i=0; i<ta.length(); i++)
                        icons[i] = ta.getResourceId(i, 0);
                    ta.recycle();
                }
            }
        }
        adapter.setIcons(icons);

        // Update the summary on display
        updateSummary(parseValues(getValue()));
    }


    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);
        // used to change the summary style
        TextView summary = (TextView) view.findViewById(android.R.id.summary);
        if(summary != null) {
            summary.setEllipsize(TextUtils.TruncateAt.END);
            summary.setLines(2);
        }
    }


    @Override
    protected void onPrepareDialogBuilder(@NonNull Builder builder) {
        Integer[] saved = parseValues(getValue());

        // Determine the selected list items
        if(saved == null || saved.length == 0) {
            // no selections? no problem!
            if(inverted) adapter.selectAll();
            else adapter.deselectAll();
        } else {
            // there are selections, apply them as needed
            adapter.setSelections(Arrays.asList(saved));
            if(inverted) adapter.invertSelections();
        }

        // Build the dialog and link the adapter to it
        ListView list = new ListView(getContext());
        list.setBackgroundColor(Color.WHITE);
        list.setAdapter(adapter);
        OnItemClickListener listener = new OnItemClickListener() {
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                adapter.toggleItem(position);
            }
        };
        list.setOnItemClickListener(listener);
        builder.setView(list);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // Nothing need be done on a cancel
        if(!positiveResult) return;
        if(inverted) saveAndRefresh(adapter.getUnselected());
        else         saveAndRefresh(adapter.getSelections());
    }

    @Override
    protected Object onGetDefaultValue(@NonNull TypedArray typedArray, int index) {
        return typedArray.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object rawDefaultValue) {
        // value is the saved value unless restoreValue is false.
        String value = "" + rawDefaultValue;
        if(restoreValue) value = getPersistedString(value);

        Integer[] parsed = parseValues(value);
        if(parsed == null) parsed = new Integer[0];
        saveAndRefresh(parsed);
    }


    /** Save the value of this preference and tell anyone listening
     *  that it has changed.
     *  @param newValues The new entry values. Will be passed to any
     *  listening classes.                                        */
    private void saveAndRefresh(Integer[] newValues) {
        // Check with any listeners to make sure the save is ok.
        if(! callChangeListener(newValues)) return;
        setValue(Utils.join(SEPARATOR, newValues));
        updateSummary(newValues);
    }


    /** Set the icons to display in the popup list (see class description).
     *  @param drawables Drawable resource ids to use                    */
    public void setIcons(int[] drawables) {
        adapter.setIcons(drawables);
    }


    /** Get the drawable resource ids for the icons of this preference. */
    public int[] getIcons() {
        return adapter.getIcons();
    }


    private void updateSummary(Integer[] selections) {
        // TODO: remove hardcoded string
        String summary = Utils.join(", ", mapValues(selections, getEntries(), inverted))
                              .trim();
        if(summary.length() == 0)
            setSummary(defaultSummary);
        else setSummary(summary);
    }


    /** Entry values are ignored in this class, and thus have no value at all */
    @Override
    @Deprecated
    public CharSequence[] getEntryValues() {
        return super.getEntryValues();
    }

    /** Entry values are ignored in this class, and thus have no value at all. */
    @Override
    @Deprecated
    public void setEntryValues(CharSequence[] entryValues) {
        super.setEntryValues(entryValues);
    }
}