package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** An alternative to CheckBoxPreference that can be placed in any view structure.
 *  @author Mark Lauman */
public class CheckBoxPreference extends LinearLayout {

    /** Key used to save this preference to memory */
    private String key;
    /** The checkbox used to display the current state of this preference */
    private CheckBox vCheckBox;

    public CheckBoxPreference(Context context) {
        super(context);
        setup(context, null, 0, 0);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }


    /** My additions to the basic constructors. */
    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        setOnClickListener(new Toggler());
        View v = View.inflate(c, R.layout.multi_select_preference, null);
        vCheckBox = (CheckBox) v.findViewById(android.R.id.checkbox);
        TextView vName = (TextView) v.findViewById(android.R.id.text1);
        TextView vSummary = (TextView) v.findViewById(android.R.id.text2);
        View vImage1 = v.findViewById(android.R.id.icon1);
        ImageView vImage2 = (ImageView) v.findViewById(android.R.id.icon2);
        addView(v);

        // Get the attribute set
        if(rawAttrs == null) throw new UnsupportedOperationException("CheckBoxPreference "
                + "must be created with a attribute set.");
        TypedArray ta = c.getTheme().obtainStyledAttributes(rawAttrs,
                                                            R.styleable.CheckBoxPreference,
                                                            defStyleAttr, defStyleRes);
        if(ta == null) throw new UnsupportedOperationException("CheckBoxPreference "
                + "must be created with a attribute set.");

        // Read the attributes
        try {
            // Get the key (required)
            key = ta.getString(R.styleable.CheckBoxPreference_key);
            if(key == null) throw new IllegalArgumentException("MultiSelectPreference "
                    + "requires attribute \"key\".");

            // TextView attributes
            String name = ta.getString(R.styleable.CheckBoxPreference_name);
            vName.setText(name);
            vName.setTextColor(ta.getColor(R.styleable.CheckBoxPreference_nameColor,
                                           vName.getCurrentTextColor()));
            String summary = ta.getString(R.styleable.CheckBoxPreference_summary);
            if(summary != null && 0 < summary.length()) vSummary.setText(summary);
            else vSummary.setVisibility(GONE);
            vSummary.setTextColor(ta.getColor(R.styleable.CheckBoxPreference_summaryColor,
                                              vSummary.getCurrentTextColor()));

            // Icon attributes
            int imgRes = ta.getResourceId(R.styleable.CheckBoxPreference_image, 0);
            if(imgRes != 0) vImage2.setImageResource(imgRes);
            else vImage1.setVisibility(GONE);

        } finally {
            ta.recycle();
        }

        // Android Studio can't load preferences.
        if(isInEditMode()) return;

        // Set the checkbox to the value of the key
        vCheckBox.setChecked(PreferenceManager.getDefaultSharedPreferences(c).getBoolean(key, false));
    }

    private class Toggler implements OnClickListener {
        @Override
        public void onClick(View v) {
            vCheckBox.toggle();
            PreferenceManager.getDefaultSharedPreferences(getContext())
                             .edit()
                             .putBoolean(key, vCheckBox.isChecked())
                             .commit();
        }
    }
}
