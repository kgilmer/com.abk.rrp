package com.abk.rrp.model;

import java.util.Arrays;
import java.util.List;

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
		
	/**
	 * @param dirbleApiKey
	 */
	public StreamDirectoryClient(String dirbleApiKey) {
		super();
		this.apiKey = dirbleApiKey;
	}
	
	/**
	 * @return List of available directories.
	 */
	public List<StreamSourceDirectory> getDirectories() {
		return Arrays.asList(
				new StreamSourceDirectory [] 
						{new StreamSourceDirectory(DIRIBLE_URL, "dirble.com", apiKey)});
	}
}
