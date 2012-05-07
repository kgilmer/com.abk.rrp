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

import com.abk.rrp.model.RestClient.Response;
import com.abk.rrp.model.RestClient.ResponseDeserializer;
import com.abk.rrp.model.RestClient.URLBuilder;
import com.abk.rrp.model.StreamSourceDirectory.SourceProtocol;

public class StreamSource {		
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
	private final SourceProtocol protocol;
	private final RestClient restClient;

	private final URLBuilder allCategoryUrl;
	private final URLBuilder primaryCategoryUrl;
	private final URLBuilder childCategoryUrl;
	private final URLBuilder stationUrl;
	
	public StreamSource(String baseUrl, String label, SourceProtocol protocol, String apiKey) {
		super();

		this.restClient = new RestClient();
		restClient.addConnectionInitializer(
				new RestClient.TimeoutConnectionInitializer(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT));
		restClient.setErrorHandler(RestClient.THROW_ALL_ERRORS);
		
		this.baseUrl = restClient.buildURL(baseUrl);
		this.allCategoryUrl = this.baseUrl.copy("categories", apiKey);
		this.primaryCategoryUrl = this.baseUrl.copy("primaryCategories", apiKey);
		this.childCategoryUrl = this.baseUrl.copy("childCategories", apiKey);
		this.stationUrl = this.baseUrl.copy("stations", apiKey);
		
		this.label = label;
		this.protocol = protocol;
	}
	
	public String getBaseUrl() {
		return baseUrl.toString();
	}
	
	public String getLabel() {
		return label;
	}
	
	public SourceProtocol getProtocol() {
		return protocol;
	}
	
	public List<StreamCategory> getAllCategories() throws IOException, JSONException {
		List<StreamCategory> rl = new ArrayList<StreamCategory>();
		
		JSONArray response = getJSONArray(allCategoryUrl.toString());
		
		for (int i = 0; i < response.length(); ++i) {
			JSONObject jo = response.getJSONObject(i);
			rl.add(new StreamCategory("" + jo.getInt("id"), jo.getString("name"), jo.getString("description")));		
		}
		
		return rl;
	}
	
	public List<StreamCategory> getPrimaryCategories() throws IOException, JSONException {
		List<StreamCategory> rl = new ArrayList<StreamCategory>();
		
		JSONArray response = getJSONArray(primaryCategoryUrl.toString());
		
		for (int i = 0; i < response.length(); ++i) {
			JSONObject jo = response.getJSONObject(i);
			rl.add(new StreamCategory("" + jo.getInt("id"), jo.getString("name"), jo.getString("description")));		
		}
		
		return rl;
	}
	
	public List<StreamCategory> getChildCategories(String parentId) throws IOException, JSONException {
		List<StreamCategory> rl = new ArrayList<StreamCategory>();
		
		JSONArray response = getJSONArray(childCategoryUrl.copy(parentId).toString());
		
		for (int i = 0; i < response.length(); ++i) {
			JSONObject jo = response.getJSONObject(i);
			rl.add(new StreamCategory("" + jo.getInt("id"), jo.getString("name"), jo.getString("description")));		
		}
		
		return rl;
	}
	
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
	
	private boolean isErrorResponse(JSONArray response) {
		//TODO: Cleaner way of determining error condition.
		return response.toString().contains("errormsg");
	}

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
