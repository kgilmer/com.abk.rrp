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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import com.abk.rrp.util.RestClient;

/**
 * Metadata for a specific audio stream.
 * 
 * @author kgilmer
 *
 */
public class StreamDescription {
	
	/**
	 * Value for bitrate when none is defined or available.
	 */
	public static final int NO_BITRATE_DEFINED_VALUE = -1;
	private final String id;
	private final String name;
	private final String url;
	//in kbs
	private final int bitrate;
	private final String country;
	private String streamUrl;
	
	/**
	 * @param id id of stream (id defined by stream directory)
	 * @param name name of stream
	 * @param url url to access stream data.  May or may not be the actual audio stream (ex: .pls).
	 * @param bitrate bitrate of stream
	 * @param country country of origin
	 */
	public StreamDescription(String id, String name, String url, int bitrate, String country) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.bitrate = bitrate;
		this.country = country;
	}
	
	/**
	 * @return true if bitrate defined for stream.
	 */
	public boolean hasBitrate() {
		return bitrate != NO_BITRATE_DEFINED_VALUE;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return bitrate
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * @return country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @return url of actual audio stream.  May or may not be the same as the url field and may require network access to resolve.
	 * @throws IOException
	 */
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

	public static StreamDescription deserialize(String streamString) {
		String [] elems = streamString.split(",");
		
		return new StreamDescription(elems[0], elems[1], elems[2], Integer.parseInt(elems[3]), elems[4]);
	}

	public String serialize() {		
		StringBuilder sb = new StringBuilder();
		
		sb.append(id);
		sb.append(",");
		sb.append(name);
		sb.append(",");
		sb.append(url);
		sb.append(",");
		sb.append(bitrate);
		sb.append(",");
		sb.append(country);
		
		return sb.toString();
	}
	
}
