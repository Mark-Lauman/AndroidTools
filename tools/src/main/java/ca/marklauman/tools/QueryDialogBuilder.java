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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.NonNull;

public class QueryDialogBuilder extends Builder {
	private boolean pos_set = false;
	private boolean neg_set = false;
	private boolean cancel_set = false;
	
	private PosListener pos_listener;
	private NegListener neg_listener;
	private QueryListener query_listener;

	public QueryDialogBuilder(Context context) {
		super(context);
		
		pos_listener = new PosListener();
		neg_listener = new NegListener();
		query_listener = null;
	}
	
	
	@Override
    @NonNull
	public QueryDialogBuilder setTitle(CharSequence message) {
		super.setMessage(message);
		return this;
	}
	
	@Override
    @NonNull
	public QueryDialogBuilder setTitle(int messageId) {
		super.setMessage(messageId);
		return this;
	}
	
	
	@Override
    @NonNull
	public QueryDialogBuilder setMessage(CharSequence message) {
		super.setMessage(message);
		return this;
	}
	
	@Override
    @NonNull
	public QueryDialogBuilder setMessage(int messageId) {
		super.setMessage(messageId);
		return this;
	}
	
	
	@Override
	@Deprecated
    @NonNull
	public QueryDialogBuilder setPositiveButton(int textId, OnClickListener listener) {
		super.setPositiveButton(textId, listener);
		pos_set = true;
		return this;
	}
	
	/** Set the text of the positive button
	 *  @param textId The resource id of the
	 *  text to display in the positive button
	 *  @return This Builder object to allow
	 *  for chaining of calls to set methods */
	public QueryDialogBuilder setPositiveButton(int textId) {
		super.setPositiveButton(textId, pos_listener);
		pos_set = true;
		return this;
	}
	
	@Override
	@Deprecated
    @NonNull
	public QueryDialogBuilder setPositiveButton(CharSequence text, @NonNull OnClickListener listener) {
		super.setPositiveButton(text, listener);
		pos_set = true;
		return this;
	}
	
	/** Set the text of the positive button
	 *  @param text The text to display in the
	 *  positive button
	 *  @return This Builder object to allow
	 *  for chaining of calls to set methods */
	public QueryDialogBuilder setPositiveButton(CharSequence text) {
		super.setPositiveButton(text, pos_listener);
		pos_set = true;
		return this;
	}
	
	@Override
	@Deprecated
    @NonNull
	public QueryDialogBuilder setNegativeButton(int textId, OnClickListener listener) {
		super.setNegativeButton(textId, listener);
		neg_set = true;
		return this;
	}
	
	/** Set the text of the negative button
	 *  @param textId The resource id of the
	 *  text to display in the negative button
	 *  @return This Builder object to allow
	 *  for chaining of calls to set methods */
	public QueryDialogBuilder setNegativeButton(int textId) {
		super.setNegativeButton(textId, neg_listener);
		neg_set = true;
		return this;
	}
	
	@Override
	@Deprecated
    @NonNull
	public QueryDialogBuilder setNegativeButton(CharSequence text, OnClickListener listener) {
		super.setNegativeButton(text, listener);
		neg_set = true;
		return this;
	}
	
	/** Set the text of the negative button
	 *  @param text The text to display in the
	 *  negative button
	 *  @return This Builder object to allow
	 *  for chaining of calls to set methods */
	public QueryDialogBuilder setNegativeButton(CharSequence text) {
		super.setNegativeButton(text, neg_listener);
		neg_set = true;
		return this;
	}
	
	
	@Override
	@Deprecated
    @NonNull
	public QueryDialogBuilder setOnCancelListener(OnCancelListener onCancelListener) {
		super.setOnCancelListener(onCancelListener);
		cancel_set = true;
		return this;
	}
	
	
	@Override
    @NonNull
	public AlertDialog show() {
		if(!pos_set)
			setPositiveButton(android.R.string.ok);
		if(!neg_set)
			setNegativeButton(android.R.string.cancel);
		if(!cancel_set)
			super.setOnCancelListener(neg_listener);
		return super.show();
	}
	
	
	/** Set a listener to be invoked when either
	 *  button of the dialog is pressed or it is
	 *  cancelled entirely.
	 *  @param listener The {@link QueryListener} to use.
	 *  @return This Builder object to allow for
	 *  chaining of calls to set methods                */
    public QueryDialogBuilder setQueryListener(QueryListener listener) {
		query_listener = listener;
		return this;
	}
	
	public interface QueryListener {
		/** Called when the query dialog closes.
		 *  @param positive {@code true} if the
		 *  positive result was chosen.      */
		void onDialogClose(boolean positive);
	}
	
	private class PosListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if(query_listener != null)
				query_listener.onDialogClose(true);
		}
	}
	
	private class NegListener implements OnClickListener,
										 OnCancelListener {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if(query_listener != null)
				query_listener.onDialogClose(false);
		}

		@Override
		public void onCancel(DialogInterface arg0) {
			if(query_listener != null)
				query_listener.onDialogClose(false);
		}
	}
}