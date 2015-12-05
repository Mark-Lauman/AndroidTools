package ca.marklauman.tools.preferences;

/** Generic structure for a preference. */
public interface Preference {

    /** Assign a listener to this preference. It will call
     *  {@link PreferenceListener#preferenceChanged(int)}
     *  when the value of the preference changes.
     *  @param listener The new listener.
     *  @param id An identifier, which will be passed to the listener when the value changes. */
    void setListener(PreferenceListener listener, int id);

    interface PreferenceListener {
        void preferenceChanged(int id);
    }
}