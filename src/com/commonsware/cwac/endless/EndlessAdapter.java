/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	Portions (c) 2009 Google, Inc.
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/		

package com.commonsware.cwac.endless;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adapter that assists another adapter in appearing endless.
 * For example, this could be used for an adapter being
 * filled by a set of Web service calls, where each call returns
 * a "page" of data.
 *
 * Subclasses need to be able to return, via getPendingView()
 * a row that can serve as both a placeholder while more data
 * is being appended, and then later an actual row in the
 * result set. You might accomplish this via two widgets
 * in a FrameLayout, only one of which is visible (e.g., an
 * ImageView doing a rotation animation while loading new
 * data, then your regular row).
 *
 * Subclasses will be handed that row View back on rebindPendingView()
 * so they can flip back to normal mode.
 *
 * The actual logic for loading new data should be done in
 * appendInBackground(). This method, as the name suggests,
 * is run in a background thread. It should return true if
 * there might be more data, false otherwise.
 *
 * If your situation is such that you will not know if there
 * is more data until you do some work (e.g., make another
 * Web service call), it is up to you to do something useful
 * with that row returned by getPendingView() to let the user
 * know you are out of data, plus return false from that final
 * call to appendInBackground().
 */
abstract public class EndlessAdapter extends BaseAdapter {
	abstract protected View getPendingView(ViewGroup parent);
	abstract protected void rebindPendingView(int position,
																						View convertView);
	abstract protected boolean appendInBackground();
	
	private ListAdapter wrapped=null;
	private View pendingView=null;
	private int pendingPosition=-1;
	private AtomicBoolean keepOnAppending=new AtomicBoolean(true);

	/**
		* Constructor wrapping a supplied ListAdapter
    */
	public EndlessAdapter(ListAdapter wrapped) {
		super();
		
		this.wrapped=wrapped;
	}

	/**
		* Get the data item associated with the specified
		* position in the data set.
		* @param position Position of the item whose data we want
    */
	@Override
	public Object getItem(int position) {
		return(wrapped.getItem(position));
	}

	/**
		* How many items are in the data set represented by this
		* Adapter.
    */
	@Override
	public int getCount() {
		if (keepOnAppending.get()) {
			return(wrapped.getCount()+1);		// one more for "pending"
		}
		
		return(wrapped.getCount());
	}

	/**
		* Returns the number of types of Views that will be
		* created by getView().
    */
	@Override
	public int getViewTypeCount() {
		return(wrapped.getViewTypeCount());
	}

	/**
		* Get the type of View that will be created by getView()
		* for the specified item.
		* @param position Position of the item whose data we want
    */
	@Override
	public int getItemViewType(int position) {
		return(wrapped.getItemViewType(position));
	}

	/**
		* Are all items in this ListAdapter enabled? If yes it
		* means all items are selectable and clickable.
    */
	@Override
	public boolean areAllItemsEnabled() {
		return(wrapped.areAllItemsEnabled());
	}

	/**
		* Returns true if the item at the specified position is
		* something selectable.
		* @param position Position of the item whose data we want
    */
	@Override
	public boolean isEnabled(int position) {
		return(wrapped.isEnabled(position));
	}

	/**
		* Get a View that displays the data at the specified
		* position in the data set. In this case, if we are at
		* the end of the list and we are still in append mode,
		* we ask for a pending view and return it, plus kick
		* off the background task to append more data to the
		* wrapped adapter.
		* @param position Position of the item whose data we want
		* @param convertView View to recycle, if not null
		* @param parent ViewGroup containing the returned View
    */
	@Override
	public View getView(int position, View convertView,
											ViewGroup parent) {
		if (position==wrapped.getCount() &&
				keepOnAppending.get()) {
			pendingView=getPendingView(parent);
			pendingPosition=position;
			
			new AppendTask().execute();
			
			return(pendingView);
		}
		
		return(wrapped.getView(position, convertView, parent));
	}

	/**
		* Get the row id associated with the specified position
		* in the list.
		* @param position Position of the item whose data we want
    */
	@Override
	public long getItemId(int position) {
		return(wrapped.getItemId(position));
	}
	
	/**
		* Returns the ListAdapter that is wrapped by the endless
		* logic.
    */
	protected ListAdapter getWrappedAdapter() {
		return(wrapped);
	}
	
	/**
	 * A background task that will be run when there is a need
	 * to append more data. Mostly, this code delegates to the
	 * subclass, to append the data in the background thread and
	 * rebind the pending view once that is done.
	 */
	class AppendTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			keepOnAppending.set(appendInBackground());
			
			return(null);
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			rebindPendingView(pendingPosition, pendingView);
			pendingView=null;
			pendingPosition=-1;
		}
	}
}