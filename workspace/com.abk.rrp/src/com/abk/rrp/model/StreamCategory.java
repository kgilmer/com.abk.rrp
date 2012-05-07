package com.abk.rrp.model;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

public class StreamCategory {
	
	private final String id;
	private final String name;
	private final String description;
	private final StreamSource source;
	
	public StreamCategory(String id, String name, String description, StreamSource source) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.source = source;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public List<StreamDescription> getStreams() throws JSONException, IOException {
		return source.getStreams(id);
	}
	
	@Override
	public String toString() {		
		return name + " (" + id + ")";
	}
}
