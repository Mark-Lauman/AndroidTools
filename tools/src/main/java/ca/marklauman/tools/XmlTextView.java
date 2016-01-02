package ca.marklauman.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/** Basic view that takes xml strings and parses them to display info.
 *  Must be extended to provide functionality.
 *  @author Mark Lauman */
@SuppressWarnings("unused")
public abstract class XmlTextView extends LinearLayout {
    /** Context used to construct this view */
    protected final Context mContext;
    /** The AttributeSet passed to this view */
    private final AttributeSet attrSet;

    /** Tags currently active in the parser */
    private final ArrayList<String> activeTags = new ArrayList<>(5);
    /** Starting points of each active tag */
    private final ArrayList<Integer> activeStart = new ArrayList<>(5);

    /** Current value of the display string. */
    private String rawText = "";
    /** True if we should split the text on "<hr/>" */
    private boolean hrSplit = true;

    /** Resource used for TextView elements */
    private int textRes = 0;
    /** Resource used for &lt;hr/&gt; elements */
    private int hrRes = R.layout.xmltextview_default_hr;

    public XmlTextView(Context context) {
        super(context);
        mContext = context;
        attrSet = null;
    }

    public XmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        attrSet = attrs;
        parseAttributes();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public XmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        attrSet = attrs;
        parseAttributes();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XmlTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        attrSet = attrs;
        parseAttributes();
    }

    /** Parse the AttributeSet passed to this preference, and apply it. */
    private void parseAttributes() {
        // TODO
    }

    /** Set the text on display */
    public void setText(int resourceId) {
        setText(mContext.getString(resourceId));
    }


    /** Set the text on display */
    public void setText(String text) {
        if(text == null) text = "";
        if(rawText.equals(text)) return;
        rebuildView(text);
    }

    /** If set to true, the textview will split and add a horizontal rule when it encounters
     *  the xml entity "&lt;hr/&gt;" or "&lt;hr /&gt;".<br/>
     *  Defaults to {@code true} */
    public void setHrSplit(boolean shouldSplit) {
        if(hrSplit == shouldSplit) return;
        hrSplit = shouldSplit;
        rebuildView(rawText);
    }


    /** Change the TextView used by this layout to the one specified.
     *  @param textLayout The resource id of the view you wish to use.
     *                    This layout can have a TextView as its root element, or contain
     *                    a TextView with an id of {@link android.R.id#text1}. */
    public void setTextViewRes(int textLayout) {
        textRes = textLayout;
    }


    /** Change the default resource used to draw the hr lines. Allows for custom hr lines */
    public void setHrRes(int hrLayout) {
        hrRes = hrLayout;
    }


    /** Rebuild the TextView using the provided text as the basis */
    private void rebuildView(@NonNull String text) {
        rawText = text;
        removeAllViews();

        // If we aren't splitting on the <hr/> tag, this is just one textView
        if(!hrSplit)
            addView(newSection(text));
        else {
            // We are splitting on the <hr/> tag
            String[] split = text.split("<hr\\s*/>");
            addView(newSection(split[0]));
            for(int i=1; i<split.length; i++) {
                addView(View.inflate(mContext, hrRes, null));
                addView(newSection(split[i]));
            }
        }
        invalidate();
    }


    /** Called when a new TextView is created due to section breaks,
     *  Makes a new text section for the view. */
    private View newSection(String rawText) {
        sectionStarted();

        // Create the TextView from the provided resource
        View view;
        TextView textView;
        if(textRes != 0) {
            view = View.inflate(mContext, textRes, null);
            if(view instanceof TextView)
                textView = (TextView)view;
            else textView = (TextView)view.findViewById(android.R.id.text1);
        } else {
            textView = new TextView(mContext, attrSet);
            textView.setGravity(Gravity.CENTER);
            view = textView;
        }

        // Handle raw text that has no tags
        int start = rawText.indexOf("<");
        if(start < 0) {
            textView.setText(rawText);
            return view;
        }

        // Handle text that has tags
        SpannableStringBuilder txt = new SpannableStringBuilder(rawText);
        int offset = 0;
        do {
            // determine the size and type of the tag
            boolean close = rawText.charAt(start+1) == '/';
            int end = rawText.indexOf(">", start+1);
            if(end < 0) end = rawText.length()-1;
            boolean selfClose = rawText.charAt(end-1) == '/';

            // Determine the name of the tag
            String tag = rawText.substring(start+1, end);
            if(close) tag = tag.substring(1);
            if(selfClose) tag = tag.substring(0, tag.length()-1);
            tag = tag.trim().toLowerCase();

            // If it is self closing remove and handle it
            if(selfClose) {
                int oldSize = txt.length();
                txt.delete(start - offset, end + 1 - offset);
                tagCompleted(txt, tag, start - offset, start - offset);
                offset += oldSize-txt.length();
            } else {
                // If this is an opening tag, remember it
                if(!close) {
                    activeTags.add(tag);
                    activeStart.add(start-offset);
                }
                else {
                    // If this was a closing tag, search for the first one that matches.
                    for(int i=activeTags.size()-1; 0<=i; i--) {
                        if(activeTags.get(i).equals(tag)) {
                            // We have a match. Close all tags inside this tag.
                            for(int i2=activeTags.size()-1; i<=i2; i2--) {
                                int oldSize = txt.length();
                                tagCompleted(txt, activeTags.get(i2),
                                        activeStart.get(i2), start-offset);
                                offset += oldSize-txt.length();
                                activeTags.remove(i2);
                                activeStart.remove(i2);
                            }
                            break;
                        }
                    }
                }
                // remove this tag
                int oldSize = txt.length();
                txt.delete(start-offset, end-offset +1);
                offset += oldSize-txt.length();
            }

            // Iterate
            start = rawText.indexOf("<", start+1);
        } while(0 <= start);

        textView.setText(txt, TextView.BufferType.SPANNABLE);
        return view;
    }


    /** Checks to see if a given tag is active. Useful in
     *  {@link #tagCompleted(SpannableStringBuilder, String, int, int)}. */
    protected boolean tagActive(String tagName) {
        return activeTags.contains(tagName);
    }


    /** Called when a new TextView is created for another section (separated by &lt;hr/&gt; tags) */
    protected abstract void sectionStarted();

    /** Called when an xml tag closes.
     *  @param txt The spannable string that will be displayed in the TextView.
     *             Can be modified to have the desired effects. Your xml tags will have been
     *             removed from this string before this point.
     *  @param tag The tag that has just closed
     * @param start The start position of the tag.
     * @param end The end position of the tag. If this is the same as start, it is because the
     *            tag is self-closing. */
    protected abstract void tagCompleted(SpannableStringBuilder txt, String tag, int start, int end);
}