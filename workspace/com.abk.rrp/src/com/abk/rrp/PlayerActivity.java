/*
 * Copyright (C) 2011 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abk.rrp;

import greendroid.app.GDActivity;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.abk.rrp.model.StreamCategory;
import com.abk.rrp.model.StreamDescription;
import com.abk.rrp.model.StreamDirectoryClient;

public class PlayerActivity extends GDActivity {
	private final static String API_KEY = "8371fe0078a1f16f35168a08fab7bfb670b5eb5d";
	
	//private static final int PAGE_COUNT = 7;
	//private static final int PAGE_MAX_INDEX = PAGE_COUNT - 1;

	private PageIndicator mPageIndicatorOther;
	private ArrayAdapter<String> stationListAdapter;

	private StreamDirectoryClient streamClient;

	private List<StreamCategory> primaryCategories;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setActionBarContentView(R.layout.paged_view);

		final PagedView pagedView = (PagedView) findViewById(R.id.paged_view);
		pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
		try {				
			mPageIndicatorOther = (PageIndicator) findViewById(R.id.page_indicator_other);
			
			streamClient = new StreamDirectoryClient(API_KEY);
		
			primaryCategories = streamClient.getDirectories().get(0).getSources().getPrimaryCategories();
			pagedView.setAdapter(new CategorySwipeAdapter(primaryCategories));
			mPageIndicatorOther.setDotCount(primaryCategories.size());
			
			//stationListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, planetList);
			
			setActivePage(pagedView.getCurrentPage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	private void setActivePage(int page) {
		mPageIndicatorOther.setActiveDot(page);
		/*
		 * mPageIndicatorNext.setActiveDot(PAGE_MAX_INDEX - page);
		 * mPageIndicatorPrev.setActiveDot(page);
		 */
	}

	private final OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {

		@Override
		public void onStopTracking(PagedView pagedView) {
		}

		@Override
		public void onStartTracking(PagedView pagedView) {
		}

		@Override
		public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
			setActivePage(newPage);
		}
	};

	private class CategorySwipeAdapter extends PagedAdapter {

		private final List<StreamCategory> categories;
		private final Map<Integer, ListAdapter> adapters;

		public CategorySwipeAdapter(List<StreamCategory> primaryCategories) {
			categories = primaryCategories;	
			adapters = new HashMap<Integer, ListAdapter>();
		}

		@Override
		public int getCount() {
			return categories.size();
		}

		@Override
		public Object getItem(int position) {
			return categories.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.parseLong(categories.get(position).getId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.paged_view_item, parent, false);
				((ListView) convertView).setOnItemClickListener(new StationSelectedListener());
			}
			
			ListView stationListView = ((ListView) convertView);
			
			if (!adapters.containsKey(position)) {
				StreamCategory category = (StreamCategory) getItem(position);
				
				List<StreamDescription> streams;
				try {
					streams = category.getStreams();
					
					adapters.put(position, new StreamDescriptionArrayAdapter(PlayerActivity.this, R.layout.list_item, streams));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			stationListView.setAdapter(adapters.get(position));
			return convertView;
		}

	}
	
	private class StationSelectedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object item = parent.getItemAtPosition(position);
			System.out.println(item.toString());
			
		}

		
	}
	
	private class StreamDescriptionArrayAdapter extends ArrayAdapter<StreamDescription> {

		public StreamDescriptionArrayAdapter(Context context, int textViewResourceId, List<StreamDescription> objects) {
			super(context, textViewResourceId, objects);			
		}

		
	}
}
