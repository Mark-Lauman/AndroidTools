/* Copyright (c) 2014 Mark Christopher Lauman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.                                        */
package ca.marklauman.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;

/** A basic popup bar for messages.<br/>
 *  For this class to be error free, you must
 *  implement and specify a layout for
 *  {@link #LAYOUT_ID}. Be sure to follow the
 *  recommendations provided in this parameter.<br/>
 *  Heavily based off code provided by Roman Nurik
 *  <a href="http://code.google.com/p/romannurik-code/source/browse/misc/undobar">here.</a>
 *  @author Mark Lauman                               */
@SuppressWarnings("WeakerAccess")
public class FragmentPopup extends Fragment {
	
	/** The resource id used for the layout of this fragment.
     *  This layout may contain:
	 *  <ul><li>A {@link TextView} with {@code @android:id/message}
     *  as its id (required).</li>
	 *  <li>A {@link Button} with {@code @android:id/button1}
     *  as its id (optional)</li>
	 *  <li>A {@link View} used to separate {@code @android:id/button1}
     *   from {@code @android:id/message} with the id
	 *  {@code @android:id/cut} (optional) </li></ul> */
	public static final int LAYOUT_ID = R.layout.fragment_popup;
	
	
	/** Amount of time before the popup is hidden
	 *  (in ms)                                */
	public static final int HIDE_DELAY = 5000;
	/** savedInstanceState key for the message at startup */
	private static final String KEY_MSG = "Current message";
	/** savedInstanceState key for if the button is displayed */
	private static final String KEY_BTN = "Button displayed";
	
	
	/** The view for this {@link FragmentPopup}. */
	private View mView;
	/** The {@link TextView} for the message. */
	private TextView mText;
	/** The {@link View} used to separate the button
	 *  from the message.                         */
	private View mSeparator;
	/** The {@link Button} that triggers
	 *  {@link #mListener}.               */
	private Button mButton;
	
	
	/** The message on display. */
	private CharSequence message = null;
	/** If the button is visible right now */
	private boolean button_vis;
	/** The listener to call when the button is clicked */
	private PopupListener mListener = null;
	
	
	/** Handles hiding the undo bar after x seconds. */
	final private Handler hideHandler = new Handler();
	/** Actually does the hiding for the above handler. */
	final private Runnable hideRunnable = new PopupHider();
    /** Provides fancy fade transitions for newer
     *  versions of android.                   */
	private ViewPropertyAnimator hideAnimator = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(LAYOUT_ID, container, false);
		mText = (TextView) mView.findViewById(android.R.id.message);
		mButton = (Button) mView.findViewById(android.R.id.button1);
		mSeparator = mView.findViewById(android.R.id.cut);
		if(mButton != null)
			mButton.setOnClickListener(new PopupClickListener());
		hide(true);
		return mView;
	}
	
	
	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			setupAnimator();
		if(savedInstanceState != null) {
			message = savedInstanceState.getCharSequence(KEY_MSG);
			button_vis = savedInstanceState.getBoolean(KEY_BTN);
		}
		if(message != null)
			show(message, button_vis, true);
	}
	
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void setupAnimator() {
		hideAnimator = mView.animate();
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putCharSequence(KEY_MSG, message);
		super.onSaveInstanceState(outState);
	}
	
	
	/** Set the {@link PopupListener} for this bar.
	 *  The listener will be called when this bar's
	 *  button is pressed.
	 *  @param listener The new listener.         */
	public void setListener(PopupListener listener) {
		this.mListener = listener;
	}
	
	
	/** Show this {@link FragmentPopup} and display the message.
	 *  @param messageId The resource id of the message
	 *  to display on the bar.
	 *  @param button {@code true} if the button should
	 *  be displayed.
	 *  @param immediate In Android versions > 4.0 (Ice Cream
	 *  Sandwich) this indicates whether the transition should
	 *  be immediate or should fade in slowly. In versions
	 *  earlier than 4.0, this parameter will be ignored.   */
	public void show(int messageId,
					 boolean button, boolean immediate) {
		show(getActivity().getString(messageId),
			 button, immediate);
	}
	
	
	/** Show this {@link FragmentPopup} and display the message.
	 *  @param message The message to display on the bar.
	 *  @param button {@code true} if the button should
	 *  be displayed.
	 *  @param immediate In Android versions > 4.0 (Ice Cream
	 *  Sandwich) this indicates whether the transition should
	 *  be immediate or should fade in slowly. In versions
	 *  earlier than 4.0, this parameter will be ignored.   */
	public void show(CharSequence message,
					 boolean button, boolean immediate) {
		this.message = message;
		mText.setText(message);
		
		// Show/hide button and separator
		if(mButton != null) {
			if(button) mButton.setVisibility(View.VISIBLE);
			else mButton.setVisibility(View.GONE);
		}
		if(mSeparator != null) {
			if(button) mSeparator.setVisibility(View.VISIBLE);
			else mSeparator.setVisibility(View.GONE);
		}
		
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, HIDE_DELAY);
        
        mView.setVisibility(View.VISIBLE);
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
            hideAnimator
                    .alpha(1)
                    .setDuration(getResources()
                                    .getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(null);
        } else 
            mView.setAlpha(1);
	}
	
	
	/** Hide this {@link FragmentPopup}.
	 *  @param immediate In Android versions > 4.0 (Ice Cream
	 *  Sandwich) this indicates whether the transition should
	 *  be immediate or should fade out slowly. In versions
	 *  earlier than 4.0, this parameter will be ignored.      */
	public void hide(boolean immediate) {
        hideHandler.removeCallbacks(hideRunnable);
        message = null;
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        	fancyHide(immediate);
        else
        	mView.setVisibility(View.GONE);
        mView.invalidate();
    }
	
	
	/** Does fancy fade out effects for versions that support it.
	 *  @param immediate This indicates whether the transition
	 *  should be immediate or should fade out slowly.         */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void fancyHide(boolean immediate) {
		if(! immediate) {
			hideAnimator.cancel();
            hideAnimator
                    .alpha(0)
                    .setDuration(mView.getResources()
                            .getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mView.setVisibility(View.GONE);
                        }
                    });
		} else {
			mView.setVisibility(View.GONE);
            mView.setAlpha(0);
		}
	}
	
	
	/** A listener that responds to popup button clicks. */
	public interface PopupListener {
		/** Called when the button is pressed on a targeted
		 *  {@link FragmentPopup}.                                 */
        void onPopupClicked();
    }
	
	
	/** Listens to {@link #mButton}, and informs this
	 *  {@link FragmentPopup}'s listener when it is clicked.        */
	private class PopupClickListener implements OnClickListener {
		@Override
        public void onClick(View view) {
            hide(false);
            if(mListener == null) return;
            mListener.onPopupClicked();
        }
	}
	
	
	/** Hides the {@link FragmentPopup} when run.    */
	private class PopupHider implements Runnable {
		@Override
        public void run() {
            hide(false);
        }
	}
}