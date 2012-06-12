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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.SharedPreferences;

import com.abk.rrp.model.IStreamCategory;
import com.abk.rrp.model.StreamDescription;

/**
 * Recent streams that have been played.
 * 
 * @author kgilmer
 *
 */
public class RecentStreams implements IStreamCategory {

	private static final String PREF_KEY_RECENT_STREAMS = "RECENT_STREAMS";
	private final SharedPreferences prefs;
	private List<StreamDescription> streamList;

	/**
	 * @param prefs
	 */
	public RecentStreams(SharedPreferences prefs) {
		this.prefs = prefs;
		
		String serializedStreams = prefs.getString(PREF_KEY_RECENT_STREAMS, null);
		
		if (serializedStreams == null)
			streamList = new ArrayList<StreamDescription>();
		else
			streamList = deserializeStreams(serializedStreams);
	}

	@Override
	public String getId() {
		
		return "recent";
	}

	@Override
	public String getName() {		
		return "Recent";
	}

	@Override
	public String getDescription() {		
		return "Recently played stations.";
	}

	@Override
	public List<StreamDescription> getStreams() throws JSONException, IOException {		
		return streamList;
	}

	/**
	 * @param stream
	 */
	public void addStream(StreamDescription stream) {		
		StreamDescription existingStream = null;
		for (StreamDescription sd : streamList)  {
			if (sd.getUrl().equals(stream.getUrl())) {
				existingStream = sd;
				break;
			}
		}
		
		if (existingStream != null)
			streamList.remove(existingStream);
		
		streamList.add(0, stream);
		
		serializeStreams(streamList);
	}

	private void serializeStreams(List<StreamDescription> streams) {
		StringBuilder sb = new StringBuilder();
		
		for (StreamDescription stream : streams) {
			sb.append(stream.serialize());
			sb.append('|');
		}
			
		prefs.edit().putString(PREF_KEY_RECENT_STREAMS, sb.toString()).commit();
	}
	
	private List<StreamDescription> deserializeStreams(String serializedStreams) {
		List<StreamDescription> streams = new ArrayList<StreamDescription>();
		
		for (String stream : serializedStreams.split("\\|"))
			if (stream.trim().length() > 0)
				streams.add(StreamDescription.deserialize(stream));
			
		return streams;
	}

}
