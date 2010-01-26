/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
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

package com.commonsware.cwac.endless.demo;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.commonsware.cwac.endless.EndlessAdapter;
import java.util.ArrayList;
import java.util.Arrays;

public class EndlessAdapterDemo extends ListActivity {
	private static String[] items={"lorem", "ipsum", "dolor",
																	"sit", "amet", "consectetuer",
																	"adipiscing", "elit", "morbi",
																	"vel", "ligula", "vitae",
																	"arcu", "aliquet", "mollis",
																	"etiam", "vel", "erat",
																	"placerat", "ante",
																	"porttitor", "sodales",
																	"pellentesque", "augue",
																	"purus"};
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		
		setListAdapter(new DemoAdapter(new ArrayList<String>(Arrays.asList(items))));
	}
	
	class DemoAdapter extends EndlessAdapter {
		private RotateAnimation rotate=null;
		
		DemoAdapter(ArrayList<String> list) {
			super(new ArrayAdapter<String>(EndlessAdapterDemo.this,
																			R.layout.row,
																			android.R.id.text1,
																			list));
			
			rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
																	0.5f, Animation.RELATIVE_TO_SELF,
																	0.5f);
			rotate.setDuration(600);
			rotate.setRepeatMode(Animation.RESTART);
			rotate.setRepeatCount(Animation.INFINITE);
		}
		
		protected View getPendingView(ViewGroup parent) {
			View row=getLayoutInflater().inflate(R.layout.row, null);
			
			View child=row.findViewById(android.R.id.text1);
			
			child.setVisibility(View.GONE);
			
			child=row.findViewById(R.id.throbber);
			child.setVisibility(View.VISIBLE);
			child.startAnimation(rotate);
			
			return(row);
		}
		
		protected void rebindPendingView(int position, View row) {
			View child=row.findViewById(android.R.id.text1);
			
			child.setVisibility(View.VISIBLE);
			((TextView)child).setText(getWrappedAdapter()
																					.getItem(position)
																					.toString());
			
			child=row.findViewById(R.id.throbber);
			child.setVisibility(View.GONE);
			child.clearAnimation();
		}
		
		protected boolean cacheInBackground() {
			SystemClock.sleep(2000);				// pretend to do work
			
			return(getWrappedAdapter().getCount()<60);
		}
		
		protected void appendCachedData() {
			ArrayAdapter<String> a=(ArrayAdapter<String>)getWrappedAdapter();
			
			for (String item : items) {
				a.add(item);
			}
		}
	}
}
