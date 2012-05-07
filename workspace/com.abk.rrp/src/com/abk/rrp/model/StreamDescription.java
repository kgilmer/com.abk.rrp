package com.abk.rrp.model;

public class StreamDescription {
	
	public static final int NO_BITRATE_DEFINED_VALUE = -1;
	private final String id;
	private final String name;
	private final String url;
	//in kbs
	private final int bitrate;
	private final String country;
	
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
	
}
