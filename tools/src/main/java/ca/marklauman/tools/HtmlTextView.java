package ca.marklauman.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/** Simple class used to make adding html to TextViews easier.
 *  Any text assigned to this view at construction will be parsed as html.
 *  @author Mark Lauman */
public class HtmlTextView extends TextView {
    public HtmlTextView(Context context) {
        super(context);
        setHtml(getText());
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHtml(getText());
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHtml(getText());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setHtml(getText());
    }

    /** Set the text of this TextView to an html string */
    public void setHtml(CharSequence text) {
        setText(Html.fromHtml(""+text));
    }

    /** Set the text of this TextView to an html string */
    public void setHtml(int textResource) {
        setText(Html.fromHtml(getContext().getString(textResource)));
    }
}