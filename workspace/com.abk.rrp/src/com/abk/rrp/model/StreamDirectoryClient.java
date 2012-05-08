package com.abk.rrp.model;

import java.util.Arrays;
import java.util.List;

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
		
	/**
	 * @param dirbleApiKey
	 */
	public StreamDirectoryClient(String dirbleApiKey, SharedPreferences prefs) {
		super();
		this.apiKey = dirbleApiKey;
		this.prefs = prefs;
	}
	
	/**
	 * @return List of available directories.
	 */
	public List<IStreamSource> getDirectories() {
		//Currently hardcoded to return a single directory: dirble.com.  To add directories, add to this list.
		return Arrays.asList(
				new IStreamSource [] 
						{new DirbleStreamSource(DIRIBLE_URL, "dirble.com", apiKey, new PrefCache(prefs))});
	}
}
