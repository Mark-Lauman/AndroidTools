package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** Simple preference that launches an intent when clicked.
 *  Does not save data, or do anything else, just looks like a preference,
 *  and launches an activity when clicked.
 *  @author Mark Lauman */
public class IntentPreference extends LinearLayout {
    /** Extra passed to the called activity */
    public String EXTRA = "extra";

    /** The class name of the intent to launch. */
    private String toLaunch;
    /** The extra to pass with the intent */
    private int extra = 0;

    public IntentPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public IntentPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IntentPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    /** My additions to the basic constructors. */
    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        // Setup the OnClickListener and View.
        this.addView(View.inflate(c, R.layout.preference_basic, null));
        setOnClickListener(new IntentLauncher());

        // Load the attributes
        if(rawAttrs == null)
            throw new UnsupportedOperationException("IntentPreference must be created"
                                                    +" with an attribute set.");
        TypedArray ta = c.getTheme().obtainStyledAttributes(rawAttrs,
                                                            R.styleable.IntentPreference,
                                                            defStyleAttr, defStyleRes);
        if(ta == null) throw new UnsupportedOperationException("IntentPreference must be created "
                                                               +" with an attribute set.");

        // Apply the attributes
        try {
            // Load the name of the intent we will launch
            toLaunch = ta.getString(R.styleable.IntentPreference_intent);
            if(toLaunch == null)
                throw new UnsupportedOperationException("IntentPreference must be created "
                                                        +" with an intent attribute.");

            // Load the extra to send to the intent
            extra = ta.getResourceId(R.styleable.IntentPreference_extra, 0);

            // Apply the attributes to the name text field
            TextView vText = (TextView) findViewById(android.R.id.text1);
            String name = ta.getString(R.styleable.IntentPreference_name);
            vText.setText(name);
            vText.setTextColor(ta.getColor(R.styleable.IntentPreference_nameColor,
                                           vText.getCurrentTextColor()));

            // Apply the attributes to the summary text field
            vText = (TextView) findViewById(android.R.id.text2);
            String summary = ta.getString(R.styleable.IntentPreference_summary);
            if(summary != null && 0 < summary.length())
                vText.setText(summary);
            else vText.setVisibility(GONE);
            vText.setTextColor(ta.getColor(R.styleable.IntentPreference_summaryColor,
                                           vText.getCurrentTextColor()));

            // Apply the attributes to the icon views
            ImageView vImg = (ImageView) findViewById(android.R.id.icon2);
            View vImgWrap = findViewById(android.R.id.icon1);
            int imgRes = ta.getResourceId(R.styleable.IntentPreference_image, 0);
            if(imgRes != 0) vImg.setImageResource(imgRes);
            else vImgWrap.setVisibility(GONE);
        } finally {
            ta.recycle();
        }
    }

    /** Used to launch the intent passed to this preference. */
    protected void launchIntent(Intent i) {
        getContext().startActivity(i);
    }


    private class IntentLauncher implements OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                Intent i = new Intent(getContext(), Class.forName(toLaunch));
                i.putExtra(EXTRA, extra);
                launchIntent(i);
            } catch (ClassNotFoundException e) {
                throw new UnsupportedOperationException("IntentPreference has invalid intent "
                        +"attribute \""+toLaunch+"\"");
            }
        }
    }
}