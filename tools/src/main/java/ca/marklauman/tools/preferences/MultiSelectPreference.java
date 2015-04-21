package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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
    /** The android namespace used for some of our attributes. */
    private final static String ANDROID = "http://schemas.android.com/apk/res/android";
    /** The res-auto namespace used for our custom attributes */
    private final static String RES_AUTO = "http://schemas.android.com/apk/res-auto";

    /** The size of 1dp in px */
    private final float dp = getResources().getDisplayMetrics().density;

    /** The values matched to the entries. */
    private int[] entryValues;
    /** The key used to save the preference */
    private String key;

    /** The state of the saved preference. */
    private Integer[] savedSel;

    /** The text view used for the preference name. */
    private TextView vName;
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
        setOrientation(VERTICAL);
        setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        int pad = dp(8);
        setPadding(pad, pad, pad, pad);

        // The TextView for displaying the preference name
        vName = new TextView(c);
        vName.setWidth(getWidth());
        vName.setSingleLine();
        vName.setEllipsize(TruncateAt.END);
        vName.setTextSize(18);
        this.addView(vName);

        // The TextView for displaying the summary
        vSummary = new TextView(c);
        vSummary.setWidth(getWidth());
        vSummary.setMaxLines(2);
        vSummary.setEllipsize(TruncateAt.END);
        vSummary.setTextSize(14);
        this.addView(vSummary);

        // Get the attribute set
        if(rawAttrs == null) throw new UnsupportedOperationException("MultiSelectPreference must be created with a attribute set.");
        TypedArray ta = c.getTheme().obtainStyledAttributes(rawAttrs,
                                                            R.styleable.MultiSelectPreference,
                                                            defStyleAttr, defStyleRes);
        if(ta == null) throw new UnsupportedOperationException("MultiSelectPreference must be created with a attribute set.");

        try {
            // Check for basic, required attributes
            CharSequence[] entries = ta.getTextArray(R.styleable.MultiSelectPreference_entries);
            if(entries == null) throw new IllegalArgumentException("MultiSelectPreference requires attribute \"entries\".");
            key = ta.getString(R.styleable.MultiSelectPreference_key);
            if(key == null) throw new IllegalArgumentException("MultiSelectPreference requires attribute \"key\".");

            // TextView attributes
            vName.setText(ta.getString(R.styleable.MultiSelectPreference_name));
            vName.setTextColor(ta.getColor(R.styleable.MultiSelectPreference_nameColor, Color.BLACK));
            vSummary.setText(ta.getString(R.styleable.MultiSelectPreference_summary));
            vSummary.setTextColor(ta.getColor(R.styleable.MultiSelectPreference_summaryColor, Color.BLACK));

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
            int[] entryIcons = getResourceArray(ta, R.styleable.MultiSelectPreference_entryIcons);
            if(entryIcons != null) adapter.setIcons(entryIcons);

            boolean inverted = ta.getBoolean(R.styleable.MultiSelectPreference_inverted, false);

            // Basic selections for AndroidStudio
            if(isInEditMode()) {
                if(inverted) adapter.selectAll();
                return;
            }

            // load selections
            SparseIntArray map = new SparseIntArray(entryValues.length);
            for(int i=0; i<entryValues.length; i++)
                map.put(entryValues[i], i);
            String[] rawSel = PreferenceManager.getDefaultSharedPreferences(c)
                                               .getString(key, "").split(",");
            savedSel = new Integer[rawSel.length];
            for(int i=0; i<savedSel.length; i++)
                savedSel[i] = map.get(Integer.parseInt(rawSel[i]));
            adapter.setSelections(savedSel);
            if(inverted)
                adapter.invertSelections();
            savedSel = adapter.getSelections();
        } finally {
            ta.recycle();
        }
    }


    /** Convert from dp into px.
     *  @param dp The measurement in dp.
     *  @return That measurement in px. */
    private int dp(int dp) {
        return (int)(dp * this.dp + 0.5);
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


    private class DialogLauncher implements OnClickListener, QueryListener {
        @Override
        public void onClick(View v) {
            QueryDialogBuilder builder = new QueryDialogBuilder(getContext());
            builder.setPositiveButton(android.R.string.ok);
            builder.setNegativeButton(android.R.string.cancel);

            // The list view from inside the dialog.
            ListView list = new ListView(getContext());
            list.setBackgroundColor(Color.WHITE);
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
            builder.create().show();
        }

        @Override
        public void onDialogClose(boolean ok) {
            if(!ok) return;
            savedSel = adapter.getSelections();

            ArrayList<Integer> save = new ArrayList<>(savedSel.length);
            for(int i : savedSel)
                save.add(entryValues[i]);

            Log.d("save", Utils.join(",", save));
        }
    }
}