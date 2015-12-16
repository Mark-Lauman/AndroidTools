package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** Simple preference that alerts its listener when clicked.
 *  @author Mark Lauman */
public class ClickPreference extends Preference<Void> {

    public ClickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ClickPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClickPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    /** My additions to the basic constructors. */
    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        addView(View.inflate(c, R.layout.preference_basic, null));
        if(rawAttrs == null) return;

        TypedArray ta = c.getTheme()
                         .obtainStyledAttributes(rawAttrs, R.styleable.ClickPreference,
                                                 defStyleAttr, defStyleRes);
        if(ta == null) return;

        try {
            TextView vText = (TextView) findViewById(android.R.id.text1);
            vText.setText(ta.getString(R.styleable.ClickPreference_name));
            vText.setTextColor(ta.getColor(R.styleable.ClickPreference_nameColor,
                    vText.getCurrentTextColor()));

            vText = (TextView) findViewById(android.R.id.text2);
            vText.setText(ta.getString(R.styleable.ClickPreference_summary));
            vText.setTextColor(ta.getColor(R.styleable.ClickPreference_summaryColor,
                                           vText.getCurrentTextColor()));

            ImageView vImg = (ImageView) findViewById(android.R.id.icon2);
            View vImgWrap = findViewById(android.R.id.icon1);
            int imgRes = ta.getResourceId(R.styleable.IntentPreference_image, 0);
            if(imgRes != 0) vImg.setImageResource(imgRes);
            else vImgWrap.setVisibility(GONE);
        } finally {
            ta.recycle();
        }

    }

    @Override
    public void reload() {}

    @Override
    public void setValue(Void value, boolean notify) {}

    @Override
    public Void getValue() {
        return null;
    }

    private class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            alertListener();
        }
    }
}