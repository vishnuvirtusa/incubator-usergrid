/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.usergrid.services.notifierServices.gcm;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.server.UID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.entities.GoogleNotifier;
import org.apache.usergrid.services.notifierServices.IMessageSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*This class provides service for google cloud messaging. */

public class GoogleNotificationService{
	private final GoogleNotifier notifier;
	private String messagePayload;
	private static final String BASE_URL = "https://android.googleapis.com/gcm/send";
	private List<String> devicesIds;
	private String deviceId;

	public GoogleNotificationService(final GoogleNotifier notifier,
			String messagePayload, List<String> devicesIds) {
		this.notifier = notifier;
		this.messagePayload = messagePayload;
		this.devicesIds = devicesIds;
	}

	public GoogleNotificationService(final GoogleNotifier notifier,
			String messagePayload, String deviceId) {
		this.notifier = notifier;
		this.messagePayload = messagePayload;
		this.deviceId = deviceId;
	}

	public Integer executeHTTPSConnectionBuilder() {
		Integer responseCode = 0;
		try {
			URL url = new URL(BASE_URL);
			responseCode = post(getPostHTTPConnection(url, "application/json"),
					getJSONPayload());

		} catch (Exception exception) {
		}
		return responseCode;
	}

	private HttpURLConnection getPostHTTPConnection(URL url, String contentType)
			throws Exception {
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setRequestMethod("POST");

		if (!contentType.isEmpty())
			httpURLConnection.setRequestProperty("Content-Type", contentType);

		httpURLConnection.setRequestProperty("Authorization", " key="
				+ notifier.getApiKey());

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setUseCaches(false);

		return httpURLConnection;
	}

	private Integer post(HttpURLConnection httpURLConnection,
			JSONObject jsonObject) {
		Integer responseCode = 0;
		OutputStream outputStream = null;
		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStream.write(jsonObject.toString().getBytes());
			responseCode = httpURLConnection.getResponseCode();
		} catch (Exception exception) {
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
				}
			}
		}
		return responseCode;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONPayload() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject subJsonObject2 = new JSONObject();
		subJsonObject2.put("score","4x8");	
		subJsonObject2.put("time",getTime());
		subJsonObject2.put("message",messagePayload);
		if(devicesIds!=null && devicesIds.size()>0){
			for(String deviceId:devicesIds)
				jsonArray.add(deviceId);			
		}
		else{			
			jsonArray.add(deviceId);			
		}
		jsonObject.put("registration_ids",jsonArray);		
		jsonObject.put("collapse_key",new UID().toString());
		jsonObject.put("data",subJsonObject2);
		
		System.out.println(jsonObject.toString());
		
		return jsonObject;	
	}

	private String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date();

		return dateFormat.format(date);
	}
}