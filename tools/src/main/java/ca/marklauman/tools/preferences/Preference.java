package ca.marklauman.tools.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/** Generic structure for a preference. */
public abstract class Preference<T> extends LinearLayout {

    /** The listener to be notified if this preference changes */
    private PreferenceListener listener;
    /** The id to pass to the listener */
    private int listenId;

    /** The key used to save the preference */
    protected String key;


    // ======= DEFINED METHODS ======= //
    public Preference(Context context) {
        super(context);
    }
    public Preference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Preference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Preference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** The same as setListener(listener, 0)
     * @param listener The new listener for this Preference */
    public void setListener(PreferenceListener listener) {
        setListener(listener, 0);
    }

    /** Assign a listener to this preference. The listener will be alerted when
     *  the preference changes value.
     *  @param listener The new listener.
     *  @param id An identifier, which will be passed to the listener when the value changes. */
    public void setListener(PreferenceListener listener, int id) {
        this.listener = listener;
        listenId = id;
    }

    /** Call this when you wish to notify the listeners of a change. */
    public void alertListener() {
        if(listener != null)
            listener.preferenceChanged(listenId);
    }

    /** Set the key used to save values.
     *  If a key is specified, then the preference will save its value to the
     *  default sharedPreference of the activity.
     *  Automatically triggers a reload if the key has changed. */
    void setKey(String key) {
        // Check if the keys are the same
        if (key == null) {
            if (this.key == null) return;
        } else {
            if(key.equals(this.key)) return;
        }
        // The keys are not the same
        this.key = key;
        reload();
    }

    /** Get the key used to save the preference */
    String getKey() {
        return key;
    }

    /** The same as calling {@link #setValue(Object, boolean)} with notify set to {@code false}. */
    public void setValue(T value) {
        setValue(value, false);
    }

    /** Get the default SharedPreferences for this context.
     *  (useful utility method) */
    protected SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }


    // ======= ABSTRACT METHODS ======= //
    /** Load this preference's value from the default SharedPreferences of this activity.
     *  If this changes the value of the preference, notify listeners. */
    abstract public void reload();

    /** Set the value of this preference.
     *  @param value The new value of the preference.
     *  @param notify If the listeners should be notified of the change */
    abstract public void setValue(T value, boolean notify);
    /** Get the value of this preference. */
    abstract public T getValue();

    /** Interface used for those who wish to listen to a Preference */
    interface PreferenceListener {
        /** Called when a preference is changed.
         * @param id The id passed to the preference when the listener was set. */
        void preferenceChanged(int id);
    }
}