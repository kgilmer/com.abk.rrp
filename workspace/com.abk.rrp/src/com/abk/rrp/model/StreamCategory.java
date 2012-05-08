package com.abk.rrp.model;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

/**
 * Genre designation for a given stream.
 * 
 * @author kgilmer
 *
 */
public class StreamCategory {
	
	private final String id;
	private final String name;
	private final String description;
	private final IStreamSource source;
	
	/**
	 * @param id of stream
	 * @param name of stream
	 * @param description of stream
	 * @param source StreamSource
	 */
	public StreamCategory(String id, String name, String description, IStreamSource source) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.source = source;
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
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return List of StreamDiscription for all streams in category.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	public List<StreamDescription> getStreams() throws JSONException, IOException {
		return source.getStreams(id);
	}
	
	@Override
	public String toString() {		
		return name + " (" + id + ")";
	}
}
