package ca.marklauman.tools.popups;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.marklauman.tools.R;

/** A basic popup bar for undo messages.
 *  Heavily based off code provided by Roman Nurik
 *  <a href="http://code.google.com/p/romannurik-code/source/browse/misc/undobar">here.</a>
 *  @author Mark Lauman */
@SuppressWarnings("WeakerAccess")
public class PopupUndo extends LinearLayout {

    /** Default amount of time before the popup is hidden (in ms) */
    public static final int DEFAULT_HIDE_DELAY = 5000;

    /** Handles hiding the undo bar after x seconds. */
    final private Handler hideHandler = new Handler();
    /** Actually does the hiding for the above handler. */
    final private Runnable hideRunnable = new PopupHider();
    /** Provides fancy fade transitions for newer versions of android. */
    private ViewPropertyAnimator hideAnimator = null;

    /** The {@link TextView} for the message. */
    private TextView mText;
    /** The listener to call when the button is clicked */
    private PopupListener mListener = null;
    /** The delay to use before the popup is hidden */
    private int hideDelay;

    public PopupUndo(Context context) {
        super(context);
        setup(context, null, 0, 0);
    }

    public PopupUndo(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PopupUndo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PopupUndo(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    /** My extensions to the constructors */
    private void setup(Context context, AttributeSet rawAttrs, int defStyleAttr, int defStyleRes) {
        // Basic View setup
        View v = View.inflate(context, R.layout.popup_undo, null);
        mText = (TextView) v.findViewById(android.R.id.message);
        View mButton = v.findViewById(android.R.id.button1);
        mButton.setOnClickListener(new PopupClickListener());
        hideDelay = DEFAULT_HIDE_DELAY;
        setOrientation(VERTICAL);
        addView(v);

        // Setup the animator
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            hideAnimator = animate();

        // Load parameters passed via an xml file
        if(rawAttrs == null) return;
        TypedArray ta = context.getTheme()
                               .obtainStyledAttributes(rawAttrs, R.styleable.PopupUndo,
                                                       defStyleAttr, defStyleRes);
        if(ta == null) return;
        try {
            setText(ta.getString(R.styleable.PopupUndo_message));
            mButton.setBackgroundResource(ta.getResourceId(R.styleable.PopupUndo_btnBack,
                                                           R.drawable.popup_btn_back));
            hideDelay = ta.getInt(R.styleable.PopupUndo_hideDelay, DEFAULT_HIDE_DELAY);
        } finally {
            ta.recycle();
        }

        // The bar starts hidden, unless you are previewing in AndroidStudio
        if(! isInEditMode()) hide(true);
    }


    /** Set the {@link PopupListener} for this bar.
     *  The listener will be called when this bar's button is pressed.
     *  @param listener The new listener. */
    public void setListener(PopupListener listener) {
        this.mListener = listener;
    }


    /** Set the amount of time the popup waits until it hides itself.
     *  @param delay The delay time in milliseconds. */
    public void setHideDelay(int delay) {
        hideDelay = delay;
    }


    /** Set the display message shown in this popup.
     *  @param text The text to display. */
    public void setText(CharSequence text) {
        mText.setText(text);
    }


    /** Set the display message shown in this popup.
     *  @param resId The resource id of the text to display. */
    public void setText(int resId) {
        mText.setText(resId);
    }


    /** Show this popup.
     *  @param immediate In Android versions > 4.0 (Ice Cream Sandwich) this indicates
     *                   whether the transition should be immediate or should fade in slowly.
     *                   In versions earlier than 4.0, this parameter will be ignored. */
    public void show(boolean immediate) {
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, hideDelay);
        setVisibility(View.VISIBLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            fancyShow(immediate);
    }


    /** Does fancy fade in effects for versions that support it.
     *  @param immediate This indicates whether the transition
     *  should be immediate or should fade in slowly.         */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void fancyShow(boolean immediate) {
        if (! immediate) {
            hideAnimator.cancel();
            hideAnimator.alpha(1)
                        .setDuration(getResources()
                                .getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(null);
        } else setAlpha(1);
    }


    /** Hide this popup.
     *  @param immediate In Android versions > 4.0 (Ice Cream Sandwich) this indicates
     *                   whether the transition should be immediate or should fade out slowly.
     *                   In versions earlier than 4.0, this parameter will be ignored. */
    public void hide(boolean immediate) {
        hideHandler.removeCallbacks(hideRunnable);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            fancyHide(immediate);
        else setVisibility(View.GONE);
        invalidate();
    }


    /** Does fancy fade out effects for versions that support it.
     *  @param immediate This indicates whether the transition
     *  should be immediate or should fade out slowly. */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void fancyHide(boolean immediate) {
        if(! immediate) {
            hideAnimator.cancel();
            hideAnimator.alpha(0)
                        .setDuration(getResources()
                                .getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                setVisibility(View.GONE);
                            }
                        });
        } else {
            setVisibility(View.GONE);
            setAlpha(0);
        }
    }


    /** Listens to the button and informs the listener of this popup when clicked. */
    private class PopupClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            hide(false);
            if(mListener == null) return;
            mListener.onPopupClicked();
        }
    }


    /** Hides this popup when run.    */
    private class PopupHider implements Runnable {
        @Override
        public void run() {
            hide(false);
        }
    }
}