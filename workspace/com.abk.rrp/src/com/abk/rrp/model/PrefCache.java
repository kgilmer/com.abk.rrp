package com.abk.rrp.model;

import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;

import com.abk.rrp.model.RestClient.HttpGETCache;
import com.abk.rrp.model.RestClient.HttpGETCacheEntry;

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
			System.out.println("Cache miss: " + key);
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
				System.out.println("Cache hit: " + key);
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
