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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.abk.rrp.util.RestClient;
import com.abk.rrp.util.RestClient.HttpGETCache;
import com.abk.rrp.util.RestClient.Response;
import com.abk.rrp.util.RestClient.ResponseDeserializer;
import com.abk.rrp.util.RestClient.URLBuilder;

/**
 * Entity representing actual directory of stream metadata.
 * 
 * @author kgilmer
 *
 */
public class DirbleStreamSource implements IStreamSource {		
	/**
	 * Default HTTP connection timeout.
	 */
	public static final int DEFAULT_CONNECT_TIMEOUT = 6000;
	/**
	 * Default HTTP read timeout.
	 */
	public static final int DEFAULT_READ_TIMEOUT = 10000;

	private final URLBuilder baseUrl;
	private final String label;
	
	private final RestClient restClient;

	private final URLBuilder allCategoryUrl;
	private final URLBuilder primaryCategoryUrl;
	private final URLBuilder childCategoryUrl;
	private final URLBuilder stationUrl;
	
	/**
	 * @param baseUrl root url in common of all REST calls.
	 * @param label name of stream source
	 * @param protocol protocol of stream source
	 * @param apiKey api key used in calling server
	 */
	public DirbleStreamSource(String baseUrl, String label, String apiKey, HttpGETCache cache) {
		super();

		this.restClient = new RestClient();
		restClient.addConnectionInitializer(
				new RestClient.TimeoutConnectionInitializer(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT));
		restClient.setErrorHandler(RestClient.THROW_ALL_ERRORS);
		restClient.setCache(cache);
		
		this.baseUrl = restClient.buildURL(baseUrl);
		this.allCategoryUrl = this.baseUrl.copy("categories", apiKey);
		this.primaryCategoryUrl = this.baseUrl.copy("primaryCategories", apiKey);
		this.childCategoryUrl = this.baseUrl.copy("childCategories", apiKey);
		this.stationUrl = this.baseUrl.copy("stations", apiKey);
		
		this.label = label;
	}
	
	/**
	 * @return base URL
	 */
	public String getBaseUrl() {
		return baseUrl.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamSource#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}	
	
	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamSource#getAllCategories()
	 */
	@Override
	public List<IStreamCategory> getAllCategories() throws IOException, JSONException {
		List<IStreamCategory> rl = new ArrayList<IStreamCategory>();
		
		JSONArray response = getJSONArray(allCategoryUrl.toString());
		
		for (int i = 0; i < response.length(); ++i) {
			JSONObject jo = response.getJSONObject(i);
			rl.add(new StreamCategory("" + jo.getInt("id"), jo.getString("name"), jo.getString("description"), this));		
		}
		
		return rl;
	}
	
	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamSource#getPrimaryCategories()
	 */
	@Override
	public List<IStreamCategory> getPrimaryCategories() throws IOException, JSONException {
		List<IStreamCategory> rl = new ArrayList<IStreamCategory>();
		
		JSONArray response = getJSONArray(primaryCategoryUrl.toString());
		
		for (int i = 0; i < response.length(); ++i) {
			JSONObject jo = response.getJSONObject(i);
			rl.add(new StreamCategory("" + jo.getInt("id"), jo.getString("name"), jo.getString("description"), this));		
		}
		
		return rl;
	}
	
	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamSource#getChildCategories(java.lang.String)
	 */
	@Override
	public List<IStreamCategory> getChildCategories(String parentId) throws IOException, JSONException {
		List<IStreamCategory> rl = new ArrayList<IStreamCategory>();
		
		JSONArray response = getJSONArray(childCategoryUrl.copy(parentId).toString());
		
		for (int i = 0; i < response.length(); ++i) {
			JSONObject jo = response.getJSONObject(i);
			rl.add(new StreamCategory("" + jo.getInt("id"), jo.getString("name"), jo.getString("description"), this));		
		}
		
		return rl;
	}
	
	/* (non-Javadoc)
	 * @see com.abk.rrp.model.IStreamSource#getStreams(java.lang.String)
	 */
	@Override
	public List<StreamDescription> getStreams(String categoryId) throws JSONException, IOException {
		List<StreamDescription> rl = new ArrayList<StreamDescription>();
		
		JSONArray response = getJSONArray(stationUrl.copy(categoryId).toString());
		
		if (!isErrorResponse(response))	{
			for (int i = 0; i < response.length(); ++i) {
				JSONObject jo = response.getJSONObject(i);
				rl.add(new StreamDescription("" + jo.getInt("id"), jo.getString("name"), jo.getString("streamurl"), parseBitrateField(jo.getString("bitrate")), jo.getString("country")));		
			}
		}
		
		return rl;
	}
	
	/**
	 * @param response
	 * @return true if given JSON message is an error response.
	 */
	private boolean isErrorResponse(JSONArray response) {
		//TODO: Cleaner way of determining error condition.
		return response.toString().contains("errormsg");
	}

	/**
	 * @param bitrateString
	 * @return bitrate as integer or NO_BITRATE_DEFINED_VALUE if undefined or unparsable.
	 */
	private int parseBitrateField(String bitrateString) {
		//Assumes in field is "<nnn> kbs" ex: "128 kbps"
		try {
			int val = Integer.parseInt(bitrateString.split(" ")[0]);
			return val;
		} catch (NumberFormatException e) {
			return StreamDescription.NO_BITRATE_DEFINED_VALUE;
		}
		
		//return Integer.parseInt(bitrateString.trim().substring(0, bitrateString.length() - 4));
	}

	/**
	 * Get JSON Object from server or returned cached copy if configured and available.
	 * 
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private synchronized JSONObject getJSONObject(String url) throws IOException {		
		Response<JSONObject> response = restClient.callGet(url, new JSONObjectDeserializer());
		JSONObject content = response.getContent();
				
		return content;
	}
	
	/**
	 * Get JSON Object from server or returned cached copy if configured and available.
	 * 
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private synchronized JSONArray getJSONArray(String url) throws IOException {		
		Response<JSONArray> response = restClient.callGet(url, new JSONArrayDeserializer());
		JSONArray content = response.getContent();
				
		return content;
	}
	
	/**
	 * Deserializer for JSON data using org.json class.  Used to configure the Touge rest client to return JSON objects from the HTTP message.
	 *
	 */
	private static class JSONObjectDeserializer implements ResponseDeserializer<JSONObject> {
		@Override
		public JSONObject deserialize(InputStream input, int responseCode, Map<String, List<String>> headers) throws IOException {
			if (input.available() == 0) 
				throw new IOException("Server returned no data.");
			
			String message = new String(RestClient.readStream(input));

			if (message == null || message.length() == 0) 
				throw new IOException("Server returned no data.");
			
			Object json;

			try {
				json = new JSONTokener(message).nextValue();

				if (json instanceof JSONObject)
					return (JSONObject) json;

			} catch (JSONException e) {
				throw new IOException(e.getMessage());
			}

			throw new IOException("Server did not return JSON object: " + message);
		}
	}
	
	/**
	 * Deserialize a server response into a JSON array.
	 *
	 */
	private static class JSONArrayDeserializer implements ResponseDeserializer<JSONArray> {
		@Override
		public JSONArray deserialize(InputStream input, int responseCode, Map<String, List<String>> headers) throws IOException {
			if (input.available() == 0) 
				throw new IOException("Server returned no data.");
			
			String message = new String(RestClient.readStream(input));

			if (message == null || message.length() == 0) 
				throw new IOException("Server returned no data.");
			
			Object json;

			try {
				json = new JSONTokener(message).nextValue();

				if (json instanceof JSONArray)
					return (JSONArray) json;

			} catch (JSONException e) {
				throw new IOException(e.getMessage());
			}

			throw new IOException("Server did not return JSON object: " + message);
		}
	}
}
