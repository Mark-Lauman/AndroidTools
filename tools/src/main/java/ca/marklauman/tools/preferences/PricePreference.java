package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** Simple preference that displays a price.
 *  An OnClickListener should be used for this preference to respond to clicks.
 *  @author Mark Lauman */
public class PricePreference extends LinearLayout {

    private TextView vPrice;

    public PricePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PricePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PricePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    /** My additions to the basic constructors. */
    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        addView(View.inflate(c, R.layout.preference_price, null));
        if(rawAttrs == null) return;
        TypedArray ta = c.getTheme().obtainStyledAttributes(rawAttrs,
                                                            R.styleable.PricePreference,
                                                            defStyleAttr, defStyleRes);
        if(ta == null) return;

        // Apply the attributes
        try {
            // Apply the price to its field
            vPrice = (TextView)findViewById(R.id.price);
            String price = ta.getString(R.styleable.PricePreference_price);
            if(price != null && 0 < price.length())
                vPrice.setText(price);

            // Apply the attributes to the name text field
            TextView vText = (TextView) findViewById(android.R.id.text1);
            String name = ta.getString(R.styleable.PricePreference_name);
            vText.setText(name);
            vText.setTextColor(ta.getColor(R.styleable.PricePreference_nameColor,
                    vText.getCurrentTextColor()));

            // Apply the attributes to the summary text field
            vText = (TextView) findViewById(android.R.id.text2);
            String summary = ta.getString(R.styleable.PricePreference_summary);
            if(summary != null && 0 < summary.length())
                vText.setText(summary);
            else vText.setVisibility(GONE);
            vText.setTextColor(ta.getColor(R.styleable.PricePreference_summaryColor,
                    vText.getCurrentTextColor()));

            // Apply the attributes to the icon views
            ImageView vImg = (ImageView) findViewById(android.R.id.icon2);
            View vImgWrap = findViewById(android.R.id.icon1);
            int imgRes = ta.getResourceId(R.styleable.PricePreference_image, 0);
            if(imgRes != 0) vImg.setImageResource(imgRes);
            else vImgWrap.setVisibility(GONE);
        } finally {
            ta.recycle();
        }
    }


    public void setPrice(CharSequence price) {
        vPrice.setText(price);
    }
}