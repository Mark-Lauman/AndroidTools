package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** Preference used to select a small number (0-99). */
public class SmallNumberPreference extends LinearLayout
                                   implements Preference {

    /** The number on display right now */
    private int num = 0;

    /** The listener attached to this preference */
    private PreferenceListener listener = null;
    /** The unique id of this preference given when the listener was attached */
    private int id = 0;

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
        View v = View.inflate(c, R.layout.small_number_preference, null);
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
        if(ta == null) return;
        String txt = ta.getString(R.styleable.SmallNumberPreference_text);
        if(txt != null) viewText.setText(txt);
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

    /** Set the value displayed by this preference. Listeners will be notified. */
    public void setValue(int value) {
        if(value < 0) value = 0;
        else if(99 < value) value = 99;
        num = value;
        viewNum.setText(String.valueOf(num));
        if(listener != null)
            listener.preferenceChanged(id);
    }

    /** Get the value displayed by this preference */
    public int getValue() {
        return num;
    }

    @Override
    public void setListener(PreferenceListener listener, int id) {
        this.listener = listener;
        this.id = id;
    }

    private class PlusClick implements OnClickListener {
        @Override
        public void onClick(View ignored) {
            if(num == 99) return;
            num++;
            viewNum.setText(String.valueOf(num));
            if(listener != null)
                listener.preferenceChanged(id);
        }
    }

    private class MinusClick implements OnClickListener {
        @Override
        public void onClick(View ignored) {
            if(num == 0) return;
            num--;
            viewNum.setText(String.valueOf(num));
            if(listener != null)
                listener.preferenceChanged(id);
        }
    }
}