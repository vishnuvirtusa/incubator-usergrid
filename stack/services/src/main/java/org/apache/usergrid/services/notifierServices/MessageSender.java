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
import org.apache.usergrid.persistence.entities.Device;

/**
 * This class use to get the message sender depend on the devices' platform.
 * 
 * @author virtusa
 * 
 * 
 */
public class MessageSender {
	private static final String IOS_PLATFORM = "iOS";
	private static final String ANDROID_PLATFORM = "google";
	private IMessageSender appleSender;
	private IMessageSender gooleSender;
	private EntityNotifier appleNotifier;
	private EntityNotifier googleNotifier;
	private static MessageSender messageSender;

	private MessageSender() {		
		this.appleSender = new AppleMessageSender();
		this.gooleSender = new AndroidMessageSender();			
	}
	
	public static MessageSender getMessageSenderInstance(EntityNotifier appNotifier,EntityNotifier googleNotifier){
		if(messageSender==null){
			synchronized (MessageSender.class) {
				messageSender = new MessageSender();
				messageSender.appleNotifier = appNotifier;
				messageSender.googleNotifier = googleNotifier;
			}
		}
		return messageSender;
	}

	public IMessageSender getSenderInstance(Device device) {
		
		String platform = device.getPlatform();
		if (IOS_PLATFORM.equals(platform)) {			
			if (appleNotifier.getEnablePush()) {
				System.out.println("APPLE NOTIFIER :" + appleNotifier.toString());
				appleSender.setPushParameters(appleNotifier, device.getToken());
			} else {
				System.out.println("Notifier is disabled.");
			}
			return appleSender;

		} else if (ANDROID_PLATFORM.equals(platform)) {			
			if (googleNotifier.getEnablePush()) {
				System.out.println("ANDROID NOTIFIER :" + googleNotifier.toString());
				gooleSender.setPushParameters(googleNotifier, device.getToken());
			} else {
				System.out.println("Notifier is disabled.");
			}
			return gooleSender;
		}
		return null;

	}
	
}
