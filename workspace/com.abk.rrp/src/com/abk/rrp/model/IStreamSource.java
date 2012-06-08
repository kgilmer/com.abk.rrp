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

public interface IStreamSource {

	/**
	 * @return name of stream source
	 */
	public abstract String getLabel();

	/**
	 * @return global list of categories
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract List<IStreamCategory> getAllCategories() throws IOException, JSONException;

	/**
	 * @return top-level categories
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract List<IStreamCategory> getPrimaryCategories() throws IOException, JSONException;

	/**
	 * 
	 * @param parentId
	 * @return child categories of specified top-level category.
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract List<IStreamCategory> getChildCategories(String parentId) throws IOException, JSONException;

	/**
	 * 
	 * @param categoryId
	 * @return List of streams for a given category.
	 * @throws JSONException
	 * @throws IOException
	 */
	public abstract List<StreamDescription> getStreams(String categoryId) throws JSONException, IOException;

}