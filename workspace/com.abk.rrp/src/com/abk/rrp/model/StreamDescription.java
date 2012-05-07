package com.abk.rrp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class StreamDescription {
	
	public static final int NO_BITRATE_DEFINED_VALUE = -1;
	private final String id;
	private final String name;
	private final String url;
	//in kbs
	private final int bitrate;
	private final String country;
	private String streamUrl;
	
	public StreamDescription(String id, String name, String url, int bitrate, String country) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.bitrate = bitrate;
		this.country = country;
	}
	
	public boolean hasBitrate() {
		return bitrate != NO_BITRATE_DEFINED_VALUE;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public int getBitrate() {
		return bitrate;
	}

	public String getCountry() {
		return country;
	}
	
	public String getStreamUrl() throws IOException {
		if (streamUrl == null) {
			
			String lcurl = url.toLowerCase();
			
			if (lcurl.endsWith(".mp3")) {
				streamUrl = url;
			} else if (lcurl.endsWith(".pls")) {			
				RestClient rc = new RestClient();
				String plsResponse = rc.callGet(url);
				
				if (plsResponse != null) {
					BufferedReader br = new BufferedReader(new StringReader(plsResponse));
					String line;
					while ((line = br.readLine()) != null) {
						if (line.startsWith("File")) {
							streamUrl = line.split("=")[1].trim();
							break;
						}
					}				
				}
			} else {
				streamUrl = url;
				//throw new IllegalStateException("Unhandled stream content descriptor format: " + url);
			}
		}
		
		return streamUrl;
	}
	
	@Override
	public String toString() {		
		return name;
	}
	
}
