package com.abk.rrp.test;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.json.JSONException;

import com.abk.rrp.model.IStreamSource;
import com.abk.rrp.model.StreamCategory;
import com.abk.rrp.model.StreamDescription;
import com.abk.rrp.model.StreamDirectoryClient;

public class DirbleTests extends TestCase {

	private final static String API_KEY = "8371fe0078a1f16f35168a08fab7bfb670b5eb5d";
	
	public void testCreateClient() {
		StreamDirectoryClient client = new StreamDirectoryClient(API_KEY, null);
		
		List<IStreamSource> dirs = client.getDirectories();
		
		assertNotNull(dirs);
		assertTrue(dirs.size() > 0);
	}
	
	public void testGetDirbleSource() {
		StreamDirectoryClient client = new StreamDirectoryClient(API_KEY, null);
		
		List<IStreamSource> dirs = client.getDirectories();
		
		assertNotNull(dirs);
		assertTrue(dirs.size() > 0);
		
		IStreamSource sources = dirs.get(0);
				
		assertNotNull(sources);
		
		assertNotNull(sources.getLabel());
		
	}
	
	public void testGetAllCategoriesFromDirbleSource() throws IOException, JSONException {
		StreamDirectoryClient client = new StreamDirectoryClient(API_KEY, null);
		
		List<IStreamSource> dirs = client.getDirectories();
		
		assertNotNull(dirs);
		assertTrue(dirs.size() > 0);
		
		IStreamSource sources = dirs.get(0);
		
		assertNotNull(sources);
		
		
		assertNotNull(sources.getLabel());
		
		
		
		List<StreamCategory> allCategories = sources.getAllCategories();
		assertNotNull(allCategories);
		assertTrue(allCategories.size() > 0);
	}
	
	public void testGetPrimaryCategoriesFromDirbleSource() throws IOException, JSONException {
		StreamDirectoryClient client = new StreamDirectoryClient(API_KEY, null);
		
		List<IStreamSource> dirs = client.getDirectories();
		
		assertNotNull(dirs);
		assertTrue(dirs.size() > 0);
		
		IStreamSource sources = dirs.get(0);
		
		assertNotNull(sources);
		
		assertNotNull(sources.getLabel());
	
		List<StreamCategory> primaryCategories = sources.getPrimaryCategories();
		assertNotNull(primaryCategories);
		assertTrue(primaryCategories.size() > 0);
	}
	
	public void testGetChildCategoriesFromDirbleSource() throws IOException, JSONException {
		StreamDirectoryClient client = new StreamDirectoryClient(API_KEY, null);
		
		List<IStreamSource> sources = client.getDirectories();
		
		assertNotNull(sources);
		assertTrue(sources.size() > 0);
		
		IStreamSource dirbleDirectory = sources.get(0);
		
		assertNotNull(dirbleDirectory);
		
		assertNotNull(dirbleDirectory.getLabel());
		
		List<StreamCategory> primaryCategories = sources.get(0).getPrimaryCategories();
		assertNotNull(primaryCategories);
		assertTrue(primaryCategories.size() > 0);
		
		for (StreamCategory pc : primaryCategories) {
			List<StreamCategory> childCategories = sources.get(0).getChildCategories(pc.getId());
			
			assertNotNull(childCategories);
			assertTrue(childCategories.size() > 0);
		}
	}
	
	public void testGetStreamsFromDirbleSource() throws IOException, JSONException {
		StreamDirectoryClient client = new StreamDirectoryClient(API_KEY, null);
		
		List<IStreamSource> dirs = client.getDirectories();
		
		assertNotNull(dirs);
		assertTrue(dirs.size() > 0);
		
		IStreamSource sources = dirs.get(0);
		
		assertNotNull(sources);
		
		assertNotNull(sources.getLabel());
		
		List<StreamCategory> primaryCategories = sources.getPrimaryCategories();
		assertNotNull(primaryCategories);
		assertTrue(primaryCategories.size() > 0);
		
		for (StreamCategory pc : primaryCategories) {
			List<StreamCategory> childCategories = sources.getChildCategories(pc.getId());
			
			assertNotNull(childCategories);
			assertTrue(childCategories.size() > 0);
			
			for (StreamCategory cc : childCategories) {
				List<StreamDescription> stations = sources.getStreams(cc.getId());
				
				for (StreamDescription sd : stations) {
					System.out.println("Station url: " + sd.getUrl());
					assertNotNull(sd.getUrl());
					assertNotNull(sd.getName());
					assertNotNull(sd.getId());
					if (sd.hasBitrate())
						assertTrue(sd.getBitrate() > 0);
				}
				
				assertNotNull(stations);
				
				if (stations.size() == 0) {
					System.out.println("Empty category: " + cc.toString());
				}
			}
		}
	}
}
