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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abk.rrp.model.StreamCategory;
import com.abk.rrp.model.StreamDescription;
import com.abk.rrp.model.StreamDirectoryClient;

public class PlayerActivity extends GDActivity {
	private final static String API_KEY = "8371fe0078a1f16f35168a08fab7bfb670b5eb5d";

	private PageIndicator mPageIndicatorOther;

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
	}

	private void playStream(String streamUrl, ImageView image) throws IllegalArgumentException, IllegalStateException, IOException {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setDataSource(streamUrl);
		mediaPlayer.prepare(); // might take long! (for buffering, etc)
		mediaPlayer.start();
		image.setImageResource(R.drawable.ic_stop);
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
			StreamDescription stream = (StreamDescription) parent.getItemAtPosition(position);

			try {
				ImageView image=(ImageView) view.findViewById(R.id.list_item_action_image);
				image.setImageResource(R.drawable.ic_wait);
				playStream(stream.getStreamUrl(), image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private class StreamDescriptionArrayAdapter extends ArrayAdapter<StreamDescription> {

		private final List<StreamDescription> streamDescs;

		public StreamDescriptionArrayAdapter(Context context, int textViewResourceId, List<StreamDescription> streamDescs) {
			super(context, textViewResourceId, streamDescs);
			this.streamDescs = streamDescs;
		}
		
		@Override
		public int getCount() {
			
			return streamDescs.size();
		}
		
		@Override
		public StreamDescription getItem(int position) {
			return streamDescs.get(position);
		}
		
		@Override
		public long getItemId(int position) {		
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView == null)
				vi = getLayoutInflater().inflate(R.layout.list_item, null);

			TextView text = (TextView) vi.findViewById(R.id.list_item_title);
			ImageView image=(ImageView)vi.findViewById(R.id.list_item_action_image);
			text.setText(streamDescs.get(position).getName());
			image.setImageResource(R.drawable.ic_play);
			// imageLoader.DisplayImage(data[position], image);
			return vi;
		}
	}
}
