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
package com.abk.rrp.util;

import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;

import com.abk.rrp.util.RestClient.HttpGETCache;
import com.abk.rrp.util.RestClient.HttpGETCacheEntry;


/**
 * A GET cache implementation that is backed by Android SharedPreferences storage.
 * 
 * @author kgilmer
 *
 */
public class PrefCache implements HttpGETCache {

	private final SharedPreferences prefs;

	/**
	 * @param prefs
	 */
	public PrefCache(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public HttpGETCacheEntry get(final String key) {
		if (prefs == null || !prefs.contains(key) || prefs.getString(key, "").length() == 0) {
			//System.out.println("Cache miss: " + key);
			return null;
		}
		
		return new HttpGETCacheEntry() {
			
			@Override
			public int getResponseCode() {				
				return 200;
			}
			
			@Override
			public Map<String, List<String>> getHeaders() {				
				return null;
			}
			
			@Override
			public byte[] getContent() {			
				//System.out.println("Cache hit: " + key);
				return prefs.getString(key, null).getBytes();
			}
		}; 
	}

	@Override
	public void put(String key, HttpGETCacheEntry entry) {
		if (prefs == null)
			return;
		
		//TODO: This may not work for unicode values, needs testing.
		prefs.edit().putString(key, new String(entry.getContent())).commit();
	}

}
