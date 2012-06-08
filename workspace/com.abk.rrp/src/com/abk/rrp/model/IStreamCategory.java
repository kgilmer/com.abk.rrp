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

public interface IStreamCategory {

	/**
	 * @return id
	 */
	public abstract String getId();

	/**
	 * @return name
	 */
	public abstract String getName();

	/**
	 * @return description
	 */
	public abstract String getDescription();

	/**
	 * @return List of StreamDiscription for all streams in category.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	public abstract List<StreamDescription> getStreams() throws JSONException, IOException;

}