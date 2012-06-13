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
package com.abk.rrp.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

import com.abk.rrp.util.PrefCache;

import android.content.SharedPreferences;

/**
 * Client for Stream Directories ~ central repositories for streaming audio metadata.
 * 
 * @author kgilmer
 *
 */
public class StreamDirectoryClient {
	//Currently only supports dirble.com
	private final static String DIRIBLE_URL = "http://dirble.com/jsonapi";
	private final String apiKey;
	private final SharedPreferences prefs;
	private final List<IStreamSource> directories;
	private final PrefCache prefCache;
		
	/**
	 * @param dirbleApiKey
	 */
	public StreamDirectoryClient(String dirbleApiKey, SharedPreferences prefs) {
		super();
		this.apiKey = dirbleApiKey;
		this.prefs = prefs;
		this.prefCache = new PrefCache(prefs);
		//Currently hardcoded to return a single directory: dirble.com.  To add directories, add to this list.
		this.directories = Arrays.asList(
				new IStreamSource [] 
						{new DirbleStreamSource(DIRIBLE_URL, "dirble.com", apiKey, prefCache)});
	}
	
	/**
	 * @return List of available directories.
	 */
	public List<IStreamSource> getDirectories() {		
		return directories;
	}
	
	/**
	 * Traverse all directories and load metadata to fill the cache.
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public void fillCache() throws IOException, JSONException {
		for (IStreamSource source : getDirectories()) {
			for (IStreamCategory category : source.getPrimaryCategories()) {
				category.getStreams();
			}
		}
	}
	
	/**
	 * Clear all entries.
	 */
	public void clearCache() {
		prefs.edit().clear().commit();
	}
}
