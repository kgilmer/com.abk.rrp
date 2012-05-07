package com.abk.rrp.model;

import java.util.Arrays;
import java.util.List;

public class StreamDirectoryClient {
	//Currently only supports dirble.com
	private final static String DIRIBLE_URL = "http://dirble.com/jsonapi";
	private final String apiKey;
		
	public StreamDirectoryClient(String apiKey) {
		super();
		this.apiKey = apiKey;
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
