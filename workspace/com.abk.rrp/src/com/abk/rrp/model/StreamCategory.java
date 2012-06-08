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

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

/**
 * Genre designation for a given stream.
 * 
 * @author kgilmer
 *
 */
public class StreamCategory implements IStreamCategory {
	
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

	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamCategory#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamCategory#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamCategory#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamCategory#getStreams()
	 */
	@Override
	public List<StreamDescription> getStreams() throws JSONException, IOException {
		return source.getStreams(id);
	}
	
	@Override
	public String toString() {		
		return name + " (" + id + ")";
	}
}
