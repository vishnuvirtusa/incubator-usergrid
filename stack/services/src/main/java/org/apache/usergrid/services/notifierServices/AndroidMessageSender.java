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
package org.apache.usergrid.services.notifierServices;

import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.entities.GoogleNotifier;
import org.apache.usergrid.services.notifierServices.gcm.GoogleNotificationService;

public class AndroidMessageSender implements IMessageSender {
	private GoogleNotifier notifier;
	//private String messagePayload;
	//private static final String BASE_URL = "https://android.googleapis.com/gcm/send";
	//private List<String> devicesIds;
	private String deviceId;

	@Override
	public void setPushParameters(EntityNotifier googleNotifier,
			String deviceToken) {
		notifier = (GoogleNotifier) googleNotifier;
		deviceId = deviceToken;
		
	}

	@Override
	public void sendPushNotification(String message, String body) {
		GoogleNotificationService googleNotificationService = new GoogleNotificationService(notifier,message,deviceId);
		Integer executedState = googleNotificationService.executeHTTPSConnectionBuilder();
		System.out.println(executedState);		
	}	

}
