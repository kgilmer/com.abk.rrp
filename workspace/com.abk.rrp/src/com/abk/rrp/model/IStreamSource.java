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
	public abstract List<StreamCategory> getAllCategories() throws IOException, JSONException;

	/**
	 * @return top-level categories
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract List<StreamCategory> getPrimaryCategories() throws IOException, JSONException;

	/**
	 * 
	 * @param parentId
	 * @return child categories of specified top-level category.
	 * @throws IOException
	 * @throws JSONException
	 */
	public abstract List<StreamCategory> getChildCategories(String parentId) throws IOException, JSONException;

	/**
	 * 
	 * @param categoryId
	 * @return List of streams for a given category.
	 * @throws JSONException
	 * @throws IOException
	 */
	public abstract List<StreamDescription> getStreams(String categoryId) throws JSONException, IOException;

}