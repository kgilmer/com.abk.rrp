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
package com.abk.rrp;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.content.SharedPreferences;

import com.abk.rrp.model.IStreamCategory;
import com.abk.rrp.model.StreamDescription;

public class RecentStreams implements IStreamCategory {

	private final SharedPreferences prefs;

	public RecentStreams(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public String getId() {
		
		return "recent";
	}

	@Override
	public String getName() {		
		return "Recently Played";
	}

	@Override
	public String getDescription() {		
		return "Recently played stations.";
	}

	@Override
	public List<StreamDescription> getStreams() throws JSONException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
