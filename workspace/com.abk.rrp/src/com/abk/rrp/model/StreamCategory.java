package com.abk.rrp.model;

import java.util.List;

public class StreamCategory {
	
	private final String id;
	private final String name;
	private final String description;
	
	public StreamCategory(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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
	
	public List<StreamDescription> getStreams() {
		return null;
	}
	
	@Override
	public String toString() {		
		return name + " (" + id + ")";
	}
}
