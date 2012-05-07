package com.abk.rrp.model;


public class StreamSourceDirectory {
	
	public enum SourceProtocol {
		DIRBLE;
	}

	private final String url;
	private final String label;
	private final String apiKey;
	
	public StreamSourceDirectory(String url, String label, String apiKey) {
		super();
		this.url = url;
		this.label = label;
		this.apiKey = apiKey;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getUrl() {
		return url;
	}
	
	public StreamSource getSources() {
		return new StreamSource(url, label, SourceProtocol.DIRBLE, apiKey);
	}
}
