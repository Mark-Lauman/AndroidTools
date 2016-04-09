package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** Simple preference that sends an email when clicked.
 *  @author Mark Lauman */
public class EmailPreference extends LinearLayout {
    private String name;
    private String email;
    private String subject;
    private String noEmail;

    private final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(email == null) {
                Log.e("EmailPreference", "No email specified");
                return;
            }
            // Prepare the email intent
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("message/rfc822");
            String data = "mailto:"+ Uri.encode(email);
            if(subject!=null)
                data += "?subject=" + Uri.encode(subject);
            intent.setData(Uri.parse(data));

            // Launch the email dialog
            try { getContext().startActivity(Intent.createChooser(intent, name));
            } catch(ActivityNotFoundException ex) {
                // Display a dialog if no email programs are available
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(name)
                       .setMessage(noEmail)
                       .create()
                       .show();
            }
        }
    };


    public EmailPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public EmailPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmailPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    /** My additions to the basic constructors. */
    private void setup(Context c, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(c)
                      .inflate(R.layout.preference_basic, this, true);
        setOnClickListener(clickListener);

        if(rawAttrs == null) return;
        TypedArray ta = c.getTheme()
                         .obtainStyledAttributes(rawAttrs, R.styleable.EmailPreference,
                                                 defStyleAttr, defStyleRes);
        if(ta != null) try {
            // Load the string values passed as attributes
            name = ta.getString(R.styleable.EmailPreference_name);
            email = ta.getString(R.styleable.EmailPreference_email);
            subject = ta.getString(R.styleable.EmailPreference_subject);
            noEmail = ta.getString(R.styleable.EmailPreference_no_email);

            // Apply attributes to their views.
            TextView vText = (TextView) findViewById(android.R.id.text1);
            vText.setText(name);
            vText.setTextColor(ta.getColor(R.styleable.EmailPreference_nameColor,
                                           vText.getCurrentTextColor()));
            vText = (TextView) findViewById(android.R.id.text2);
            vText.setText(ta.getString(R.styleable.EmailPreference_summary));
            vText.setTextColor(ta.getColor(R.styleable.EmailPreference_summaryColor,
                                           vText.getCurrentTextColor()));

            // Apply the icon
            ImageView vImg = ((ImageView)findViewById(android.R.id.icon2));
            int imgRes = ta.getResourceId(R.styleable.EmailPreference_image, 0);
            if(imgRes != 0) vImg.setImageResource(imgRes);
            else findViewById(android.R.id.icon1).setVisibility(GONE);
        } finally {
            ta.recycle();
        }
    }
}