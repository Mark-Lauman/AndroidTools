package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.marklauman.tools.ArrayCheckAdapter;
import ca.marklauman.tools.QueryDialogBuilder;
import ca.marklauman.tools.QueryDialogBuilder.QueryListener;
import ca.marklauman.tools.R;
import ca.marklauman.tools.Utils;

/** An alternative to MultiSelectPreference that can be placed in any view structure.
 *  @author Mark Lauman */
public class MultiSelectPreference extends LinearLayout {

    /** The entries on display. */
    private CharSequence[] entries;
    /** The values matched to the entries. */
    private int[] entryValues;
    /** The key used to save the preference */
    private String key;
    /** The visible name of this preference */
    private String name;
    /** The visible summary of this preference. */
    private String summary;
    /** True if the selections are inverted in this preference */
    private boolean inverted;

    /** The state of the saved preference. */
    private Integer[] savedSel;

    /** The text view used for the preference summary. */
    private TextView vSummary;
    /** The adapter used to display the entries */
    private ArrayCheckAdapter<CharSequence> adapter;

    public MultiSelectPreference(Context context) {
        super(context);
        setup(context, null, 0, 0);
    }

    public MultiSelectPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MultiSelectPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiSelectPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    /** My additions to the basic constructors. */
    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        setOnClickListener(new DialogLauncher());
        View v = View.inflate(c, R.layout.multi_select_preference, null);
        this.addView(v);
        TextView vName = (TextView) v.findViewById(android.R.id.text1);
        vSummary = (TextView) v.findViewById(android.R.id.text2);
        ImageView vImage1 = (ImageView) v.findViewById(android.R.id.icon1);
        ImageView vImage2 = (ImageView) v.findViewById(android.R.id.icon2);

        // Get the attribute set
        if(rawAttrs == null)
            throw new UnsupportedOperationException("MultiSelectPreference "
                                                    + "must be created with a attribute set.");
        TypedArray ta = c.getTheme().obtainStyledAttributes(rawAttrs,
                                                            R.styleable.MultiSelectPreference,
                                                            defStyleAttr, defStyleRes);
        if(ta == null)
            throw new UnsupportedOperationException("MultiSelectPreference "
                                                    + "must be created with a attribute set.");

        try {
            // Check for basic, required attributes
            entries = ta.getTextArray(R.styleable.MultiSelectPreference_entries);
            if(entries == null)
                throw new IllegalArgumentException("MultiSelectPreference "
                                                   + "requires attribute \"entries\".");
            key = ta.getString(R.styleable.MultiSelectPreference_key);
            if(key == null) throw new IllegalArgumentException("MultiSelectPreference "
                                                               + "requires attribute \"key\".");

            // TextView attributes
            name = ta.getString(R.styleable.MultiSelectPreference_name);
            vName.setText(name);
            vName.setTextColor(ta.getColor(R.styleable.MultiSelectPreference_nameColor,
                                           vName.getCurrentTextColor()));
            summary = ta.getString(R.styleable.MultiSelectPreference_summary);
            if(summary != null && 0 < summary.length()) vSummary.setText(summary);
            else vSummary.setVisibility(GONE);
            vSummary.setTextColor(ta.getColor(R.styleable.MultiSelectPreference_summaryColor,
                                              vSummary.getCurrentTextColor()));

            // Icon attributes
            int imgRes = ta.getResourceId(R.styleable.MultiSelectPreference_image, 0);
            if(imgRes != 0) vImage1.setImageResource(imgRes);
            else vImage2.setVisibility(GONE);

            // Setup beyond this point is not needed for preview mode.
            if(isInEditMode()) return;

            // Determine the values to save when items are selected.
            // Default to 0,1,2,3,4...
            entryValues = getIntArray(ta, R.styleable.MultiSelectPreference_entryValues);
            if(entryValues == null) {
                entryValues = new int[entries.length];
                for(int i=0; i<entryValues.length; i++)
                    entryValues[i] = i;
            }

            // Basic adapter setup
            adapter = new ArrayCheckAdapter<>(c, R.layout.list_item_check, entries);
            inverted = ta.getBoolean(R.styleable.MultiSelectPreference_inverted, false);
            int[] entryIcons = getResourceArray(ta, R.styleable.MultiSelectPreference_entryIcons);
            if(entryIcons != null) adapter.setIcons(entryIcons);

            // Create a map to link the entry values to positions
            SparseIntArray map = new SparseIntArray(entryValues.length);
            for(int i=0; i<entryValues.length; i++)
                map.put(entryValues[i], i);

            // Load the selections
            Integer[] rawSel = parseValues(PreferenceManager.getDefaultSharedPreferences(c)
                                                            .getString(key, ""));
            savedSel = new Integer[rawSel.length];
            for(int i = 0; i < savedSel.length; i++)
                savedSel[i] = map.get(rawSel[i]);
            adapter.setSelections(savedSel);
            if(inverted) adapter.invertSelections();
            savedSel = adapter.getSelections();
            updateSummary();

        } finally {
            ta.recycle();
        }
    }

    /** Update the summary based off of the selected values */
    private void updateSummary() {
        if(savedSel.length == 0) {
            // Display the summary if nothing is selected.
            if(summary != null && 0 < summary.length()) {
                vSummary.setVisibility(VISIBLE);
                vSummary.setText(summary);
            } else vSummary.setVisibility(GONE);
        } else {
            CharSequence[] display = new String[savedSel.length];
            for(int i=0; i<display.length; i++)
                display[i] = entries[savedSel[i]];
            vSummary.setVisibility(VISIBLE);
            vSummary.setText(Utils.join(", ", display));
        }
    }


    /** Get the integer array mapped to the given index.
     * @param ta The typed array to take the value from.
     * @param index The index of the integer array in that typed array.
     * @return The resulting array, or null if no array is found. */
    private static int[] getIntArray(TypedArray ta, int index) {
        int resId = ta.getResourceId(index, 0);
        if(resId == 0) return null;
        return ta.getResources().getIntArray(resId);
    }

    /** Get the resource array mapped to the given index.
     * @param ta The typed array to take the value from.
     * @param index The index of the resource array in that typed array.
     * @return The resulting array, or null if no array is found. */
    private static int[] getResourceArray(TypedArray ta, int index) {
        int resId = ta.getResourceId(index, 0);
        if(resId == 0) return null;
        TypedArray ta2 = ta.getResources().obtainTypedArray(resId);
        if(ta2 == null) return null;
        int[] res = new int[ta2.length()];
        for(int i=0; i<res.length; i++)
            res[i] = ta2.getResourceId(i, 0);
        ta2.recycle();
        return res;
    }


    /** Interpret the saved values in a MultiSelectPreference string.
     *  @param pref The string stored by the MultiSelectPreference.
     *  @return The values inside that string, or null if the string
     *  doesn't follow MultiSelectPreference's storage format.     */
    public static Integer[] parseValues(String pref) {
        if(pref == null || "".equals(pref))
            return new Integer[0];

        // Split it up and parse each segment into an int
        String[] pref_split = pref.split(",");
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


    private class DialogLauncher implements OnClickListener, QueryListener {
        @Override
        public void onClick(View v) {
            QueryDialogBuilder builder = new QueryDialogBuilder(getContext());
            builder.setPositiveButton(android.R.string.ok);
            builder.setNegativeButton(android.R.string.cancel);

            // The list view from inside the dialog.
            ListView list = new ListView(getContext());
            int background = getResources().getColor(R.color.background_grey);
            list.setBackgroundColor(background);
            adapter.setSelections(savedSel);
            list.setAdapter(adapter);
            AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                    adapter.toggleItem(position);
                }
            };
            list.setOnItemClickListener(listener);
            builder.setView(list);

            builder.setQueryListener(this);
            AlertDialog d = builder.create();
            d.setTitle(name);
            d.show();
        }

        @Override
        public void onDialogClose(boolean ok) {
            if(!ok) return;
            savedSel = adapter.getSelections();
            updateSummary();
            if(inverted) adapter.invertSelections();
            Integer[] saveValues = adapter.getSelections();

            ArrayList<Integer> save = new ArrayList<>(saveValues.length);
            for(int i : saveValues)
                save.add(entryValues[i]);
            PreferenceManager.getDefaultSharedPreferences(getContext())
                             .edit()
                             .putString(key, Utils.join(",", save))
                             .commit();
        }
    }
}