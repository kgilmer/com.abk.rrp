package com.abk.rrp.model;

import android.content.SharedPreferences;

import com.abk.rrp.model.RestClient.HttpGETCache;
import com.abk.rrp.model.RestClient.HttpGETCacheEntry;

public class PrefCache implements HttpGETCache {

	private final SharedPreferences prefs;

	public PrefCache(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public HttpGETCacheEntry get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(String key, HttpGETCacheEntry entry) {
		// TODO Auto-generated method stub

	}

}
