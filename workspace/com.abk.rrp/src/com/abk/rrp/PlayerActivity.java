/*
 * Copyright (C) 2012 Ken Gilmer
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
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.LoaderActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abk.rrp.model.IStreamSource;
import com.abk.rrp.model.StreamCategory;
import com.abk.rrp.model.StreamDescription;
import com.abk.rrp.model.StreamDirectoryClient;

/**
 * Entry point to application.
 * 
 * @author kgilmer
 * 
 */
public class PlayerActivity extends GDActivity {
	private final static String TAG = PlayerActivity.class.getPackage().getName();
	private final static String API_KEY = "8371fe0078a1f16f35168a08fab7bfb670b5eb5d";
	public final static String PREF_ROOT_NAME = TAG;

	private PageIndicator pageIndicator;

	private StreamDirectoryClient streamClient;

	private List<StreamCategory> primaryCategories;
	private MediaPlayer mediaPlayer;
	private StreamDescription currentStream;
	private ColorStateList defaultColors;
	
	//private ActionBarItem settingsActionbarItem;
	private LoaderActionBarItem refreshActionbarItem;
	
	public PlayerActivity() {
		super(ActionBar.Type.Empty);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setActionBarContentView(R.layout.paged_view);
		//settingsActionbarItem = addActionBarItem(Type.Settings, R.id.action_bar_settings);		
		addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(R.drawable.ic_stop)
                .setContentDescription(R.string.stop_stream), R.id.action_bar_stop);
		refreshActionbarItem = (LoaderActionBarItem) addActionBarItem(Type.Refresh, R.id.action_bar_refresh);

		final PagedView pagedView = (PagedView) findViewById(R.id.paged_view);
		pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);

		try {
			pageIndicator = (PageIndicator) findViewById(R.id.page_indicator_other);

			streamClient = new StreamDirectoryClient(API_KEY, getSharedPreferences(PREF_ROOT_NAME, MODE_PRIVATE));

			// TODO: need to call fillCache in a background thread and show
			// modal "loading..." dialog on first app load.
			new LoadCategoriesTask().execute(streamClient);
			// TODO: need to add to action bar: "Reload station data" which
			// should delete pref data and fillcache().

			primaryCategories = streamClient.getDirectories().get(0).getPrimaryCategories();
			pagedView.setAdapter(new CategorySwipeAdapter(primaryCategories));
			pageIndicator.setDotCount(primaryCategories.size());

			setActivePage(pagedView.getCurrentPage());

			pagedView.scrollToPage(primaryCategories.size() / 2);

			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} catch (Exception e) {
			Log.e(TAG, "An error occurred while initializing the player.", e);
			showDialog("Error", "An error occurred while initializing the player.", "Exit", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: enable following line for release.
					// PlayerActivity.this.finish();
				}
			});
		}
	}	

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

		switch (item.getItemId()) {
		case R.id.action_bar_settings:
			System.out.println("settings");
			return true;
		case R.id.action_bar_refresh:
			streamClient.clearCache();
			new LoadCategoriesTask().execute(streamClient);			
			return true;
		case R.id.action_bar_stop:
			stopStream();
			return true;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		
		
	}

	private void setActivePage(int page) {
		pageIndicator.setActiveDot(page);

	}

	synchronized private void playStream(String streamUrl) throws IllegalArgumentException, IllegalStateException, IOException {
		if (currentStream != null) {
			stopStream();
		}

		mediaPlayer.setDataSource(streamUrl);
		mediaPlayer.prepare();
		mediaPlayer.start();	
	}

	private void stopStream() {
		getActionBar().setTitle("Red Radio Player");
		mediaPlayer.stop();
		mediaPlayer.reset();
		currentStream = null;
		
		//Toggle play
		if (playingStreamTitle != null) {
			playingStreamTitle.setTypeface(null, Typeface.NORMAL);
			playingStreamTitle.setTextColor(defaultColors);
			playingStreamTitle = null;
		}
	}

	private void showDialog(String title, String message, CharSequence buttonLabel, DialogInterface.OnClickListener clickListener) {
		new AlertDialog.Builder(PlayerActivity.this).setTitle(title).setMessage(message).setPositiveButton(buttonLabel, clickListener).show();
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
	public TextView playingStreamTitle;

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
			VerticalTextView categoryTextSwitcher = (VerticalTextView) findViewById(R.id.category_label);
			categoryTextSwitcher.setText(primaryCategories.get(position).getName());

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
					Log.e(TAG, "The server returned invalid data.", e);
					showDialog("Error", "The server returned invalid data.", "Exit", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlayerActivity.this.finish();
						}
					});
				} catch (IOException e) {
					Log.e(TAG, "Unable to access station data from server.", e);
					showDialog("Error", "Unable to access station data from server.", "Exit", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlayerActivity.this.finish();
						}
					});
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

			if (currentStream != null && currentStream.equals(stream)) {				
				stopStream();
			} else {
				try {
					if (playingStreamTitle != null) {
						playingStreamTitle.setTypeface(null, Typeface.NORMAL);	
						playingStreamTitle.setTextColor(defaultColors);
						playingStreamTitle = null;
					}
	
					playingStreamTitle = (TextView) view.findViewById(R.id.list_item_title);
					defaultColors = playingStreamTitle.getTextColors();
					playingStreamTitle.setTextColor(Color.RED);
					playingStreamTitle.setTypeface(null, Typeface.BOLD);
					
					getActionBar().setTitle(stream.getName());
	
					playStream(stream.getStreamUrl());
					currentStream = stream;
				} catch (IOException e) {
					Log.e(TAG, "The server returned invalid data.", e);
					// TODO: allow retry here.
					showDialog("Error", "Unable to access station.", "Exit", new OnClickListener() {
	
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							stopStream();
						}
					});
				}
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
			// ImageView image = (ImageView)
			// vi.findViewById(R.id.list_item_action_image);
			text.setText(streamDescs.get(position).getName());
			// image.setImageResource(R.drawable.ic_play);
			// imageLoader.DisplayImage(data[position], image);
			return vi;
		}
	}

	/**
	 * @author kgilmer
	 * 
	 */
	private class LoadCategoriesTask extends AsyncTask<StreamDirectoryClient, Integer, Void> {

		@Override
		protected Void doInBackground(StreamDirectoryClient... params) {
			StreamDirectoryClient client = params[0];

			List<IStreamSource> directories = client.getDirectories();
			int count = directories.size();
			int index = 1;
			for (IStreamSource source : directories) {
				try {
					for (StreamCategory category : source.getPrimaryCategories()) {
						category.getStreams();
					}
					publishProgress((int) ((index / (float) count) * 100));
					index++;
				} catch (Exception e) {
					Log.e(TAG, "An error occurred while loading station data.", e);
				}
			}						

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (refreshActionbarItem != null)
				refreshActionbarItem.setLoading(false);
		}

	}
}
