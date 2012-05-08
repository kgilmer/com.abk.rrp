package com.abk.rrp.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

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
		
	/**
	 * @param dirbleApiKey
	 */
	public StreamDirectoryClient(String dirbleApiKey, SharedPreferences prefs) {
		super();
		this.apiKey = dirbleApiKey;
		this.prefs = prefs;
		
		//Currently hardcoded to return a single directory: dirble.com.  To add directories, add to this list.
		this.directories = Arrays.asList(
				new IStreamSource [] 
						{new DirbleStreamSource(DIRIBLE_URL, "dirble.com", apiKey, new PrefCache(prefs))});
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
			for (StreamCategory category : source.getPrimaryCategories()) {
				category.getStreams();
			}
		}
	}
}
