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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.entities.AppleNotifier;
import org.apache.usergrid.services.notifierServices.apns.IApnsService;
import org.apache.usergrid.services.notifierServices.apns.impl.ApnsServiceImpl;
import org.apache.usergrid.services.notifierServices.apns.model.ApnsConfig;
import org.apache.usergrid.services.notifierServices.apns.model.Payload;

public class AppleMessageSender implements IMessageSender {

	private static IApnsService apnsService;
	private AppleNotifier appleNotifier;
	private String deviceToken = "";

	public AppleMessageSender() {

	}

	@Override
	public void sendPushNotification(String message, String body) {

		getApnsService();

		Payload payload = new Payload();
		payload.setAlert("Usergrid Demo Notification");		
		payload.setAlertBody(message);
		// If this property is absent, the badge is not changed. To remove the
		// badge, set the value of this property to 0
		payload.setBadge(0);

		apnsService.sendNotification(deviceToken, payload);

	}

	/**
	 * Get the APNS service
	 */
	private void getApnsService() {

		if (apnsService == null) {
			ApnsConfig config = new ApnsConfig();

			InputStream is = new ByteArrayInputStream(appleNotifier.getFile()
					.getFileAsByteArray());

			config.setKeyStore(is);
			config.setDevEnv(true);
			config.setPassword(appleNotifier.getCertificatePassword());
			config.setPoolSize(5);
			apnsService = ApnsServiceImpl.createInstance(config);
		}
	}

	@Override
	public void setPushParameters(EntityNotifier appleNotifier,
			String deviceToken) {

		this.appleNotifier = (AppleNotifier) appleNotifier;
		this.deviceToken = deviceToken;

	}

}
