package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** Preference used to select a small number (0-99). */
public class SmallNumberPreference extends Preference<Integer> {

    /** The number on display right now */
    private int num = 0;

    /** The view used for the description */
    private TextView viewText;
    /** The view used to display the number. */
    private TextView viewNum;


    public SmallNumberPreference(Context context) {
        super(context);
        setup(context, null, 0,0);
    }
    public SmallNumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SmallNumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SmallNumberPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }


    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        // Basic view setup
        View v = View.inflate(c, R.layout.preference_small_number, null);
        viewText  =    (TextView) v.findViewById(android.R.id.text1);
        viewNum   =    (TextView) v.findViewById(R.id.number);
        ImageButton button = (ImageButton) v.findViewById(R.id.minus);
        button.setOnClickListener(new MinusClick());
        button = (ImageButton) v.findViewById(R.id.plus);
        button.setOnClickListener(new PlusClick());
        addView(v);

        // Load attributes
        if(rawAttrs == null) return;
        TypedArray ta = c.getTheme()
                         .obtainStyledAttributes(rawAttrs, R.styleable.SmallNumberPreference,
                                                 defStyleAttr, defStyleRes);
        if (ta == null) return;
        try {
            String txt = ta.getString(R.styleable.SmallNumberPreference_text);
            if (txt != null) viewText.setText(txt);
            txt = ta.getString(R.styleable.SmallNumberPreference_key);
            if (txt != null) setKey(txt);
        } finally {
            ta.recycle();
        }
    }


    /** Set the text label for this preference */
    public void setText(CharSequence text) {
        viewText.setText(text);
    }


    /** Set the text label for this preference */
    public void setText(int stringRes) {
        viewText.setText(stringRes);
    }


    /** Get the text label for this preference */
    public CharSequence getText() {
        return viewText.getText();
    }


    /** Reload the preference from its key. */
    @Override
    public void reload() {
        if(key == null || isInEditMode()) return;
        setValue(getSharedPreferences().getInt(key, 0),
                 true);
    }


    /** Set the value displayed by this preference.
     *  @param newValue The new value of the preference.
     *  @param notify If the listeners should be notified of the change */
    @Override
    public void setValue(@NonNull Integer newValue, boolean notify) {
        if(newValue < 0) newValue = 0;
        else if(99 < newValue) newValue = 99;

        if(num == newValue) return;
        num = newValue;
        viewNum.setText(String.valueOf(newValue));
        if(key != null && !isInEditMode())
            getSharedPreferences().edit()
                                  .putInt(key, num)
                                  .commit();
        if(notify) alertListener();
    }


    /** Get the value displayed by this preference */
    @Override
    public Integer getValue() {
        return num;
    }

    private class PlusClick implements OnClickListener {
        @Override
        public void onClick(View ignored) {
            setValue(num+1, true);
        }
    }

    private class MinusClick implements OnClickListener {
        @Override
        public void onClick(View ignored) {
            setValue(num-1, true);
        }
    }
}