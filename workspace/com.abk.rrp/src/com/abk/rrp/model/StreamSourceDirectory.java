package com.abk.rrp.model;


/**
 * A directory may contain multiple stream metadata sources, each with it's specific protocol.
 * 
 * TODO: This class may represent unnecessary abstraction.  Consider removing.
 * 
 * @author kgilmer
 *
 */
public class StreamSourceDirectory {
	
	public enum SourceProtocol {
		DIRBLE;
	}

	private final String url;
	private final String label;
	private final String apiKey;
	
	/**
	 * @param url url of directory
	 * @param label name of directory
	 * @param apiKey apikey used to access directory
	 */
	public StreamSourceDirectory(String url, String label, String apiKey) {
		super();
		this.url = url;
		this.label = label;
		this.apiKey = apiKey;
	}
	
	/**
	 * @return name of directory
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @return url of directory
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return StreamSource instance of directory
	 */
	public StreamSource getSources() {
		return new StreamSource(url, label, SourceProtocol.DIRBLE, apiKey);
	}
}
