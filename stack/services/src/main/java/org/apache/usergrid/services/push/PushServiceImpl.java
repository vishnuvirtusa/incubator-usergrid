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
package org.apache.usergrid.services.push;

import static org.apache.usergrid.services.ServiceParameter.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.usergrid.persistence.Entity;
import org.apache.usergrid.persistence.EntityManager;
import org.apache.usergrid.persistence.EntityManagerFactory;
import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.entities.Device;
import org.apache.usergrid.persistence.entities.User;
import org.apache.usergrid.services.ServiceAction;
import org.apache.usergrid.services.ServiceManager;
import org.apache.usergrid.services.ServiceManagerFactory;
import org.apache.usergrid.services.ServiceResults;
import org.apache.usergrid.services.notifierServices.IMessageSender;
import org.apache.usergrid.services.notifierServices.MessageSender;
import org.apache.usergrid.services.notifiers.NotifiersServiceProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PushServiceImpl implements PushService {
	private static final Logger logger = LoggerFactory
			.getLogger(PushServiceImpl.class);

	protected EntityManagerFactory emf;
	protected ServiceManagerFactory smf;
	protected NotifiersServiceProvider nsp;	

	@Autowired
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		logger.info("PushServiceImpl.setEntityManagerFactory");
		this.emf = emf;
	}

	@Autowired
	public void setServiceManagerFactory(ServiceManagerFactory smf) {
		logger.info("PushServiceImpl.setServiceManagerFactory");
		this.smf = smf;
	}

	@Autowired
	public void setNotifierService(NotifiersServiceProvider nsp) {
		logger.info("NotifierServiceImpl.NotifiersServiceProvider");
		this.nsp = nsp;
	}
	

	/**
	 * Override method for push request.
	 */
	@Override
	public void sendNotification(String jsonInput, UUID appid) throws Exception {
		List<Device> deviceList = getUserDevices(appid, getUserName(jsonInput));
		EntityNotifier appleNotifier = nsp.getNotifier(appid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		for (Device d : deviceList) {
			System.out.println("DEVICE :" + d.getPlatform() + " : "
					+ d.getName() + " : " + d.getToken());
			messageSender = messageSenderInstance.getSenderInstance(d);
			if(messageSender!=null)
				messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));
		}

	}

	/**
	 * Get the device list related to the application and the user.
	 * 
	 * @param applicationId
	 *            application ID
	 * @param username
	 *            Username of the user
	 * @return
	 */
	private List<Device> getUserDevices(UUID applicationId, String username)
			throws Exception {

		EntityManager em = emf.getEntityManager(applicationId);

		List<Device> devices = new ArrayList<Device>();

		ServiceManager sm = smf.getServiceManager(applicationId);
		ServiceResults srq = null;

		srq = sm.newRequest(ServiceAction.GET,
				parameters("users", username, "devices")).execute();

		List<Entity> entityList = srq.getEntities();

		for (Entity entity : entityList) {
			UUID id = entity.getUuid();
			try {
				devices.add(em.get(id, Device.class));
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}
		}

		return devices;

	}

	/**
	 * Get the username from the JSON input
	 * 
	 * @param jsonStrng
	 *            JSON Input
	 * @return username of the JSON input
	 */
	private String getUserName(String jsonStrng) {
		String username = null;
		try {
			JSONObject inputObject = new JSONObject(jsonStrng);
			username = inputObject.getString("recv");
			
		} catch (JSONException ex) {
			System.out.println(ex.getMessage());
		}
		return username;
	}

	/**
	 * Get the message from the JSON input.
	 * 
	 * @param jsonString
	 *            JSON input
	 * @return message from the JSON input
	 */
	private String getMessage(String jsonString) {
		String message = null;
		try {
			JSONObject raveObject = new JSONObject(jsonString);
			message = raveObject.getString("msg");
		} catch (JSONException ex) {
			System.out.println("Exception: " + ex);
		}
		return message;
	}

	/**
	 * Get the body from the JSON input.
	 * 
	 * @param jsonString
	 *            JSON input
	 * @return message from the JSON input
	 */
	private String getBody(String jsonString) {
		String body = null;
		try {
			JSONObject raveObject = new JSONObject(jsonString);
			body = raveObject.getJSONObject("data").toString();
		} catch (JSONException ex) {
			System.out.println("Exception: " + ex);

		}
		return body;
	}
	

	@Override
	public void sendNotificationByGroups(String jsonInput,UUID appliUuid)throws Exception {
		List<String> groupsNames = getGroupsListFromJSONPayload(jsonInput);
		List<User> users = null;
		List<Device> devices = null;
		EntityNotifier appleNotifier = nsp.getNotifier(appliUuid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appliUuid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		if(groupsNames!=null){
			for (String groupName : groupsNames) {
				users = getUsersFromGroup(groupName,appliUuid);
				if(users!=null){
					for (User user : users) {
						devices = getUserDevices(appliUuid, user.getUsername());
						if(devices!=null){
							for (Device device : devices) {
								messageSender = messageSenderInstance.getSenderInstance(device);
								if(messageSender!=null)
									messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));
							}
						}
					}					
				}
			}}
	}

	@Override
	public void sendNotificationByDevices(String jsonInput,UUID appliUuid)throws Exception {
		Device device = null;
		EntityManager em = null;
		EntityNotifier appleNotifier = nsp.getNotifier(appliUuid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appliUuid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		List<UUID> devicesUUID = getDevicesListFromJSONPayload(jsonInput);
		for (UUID uuid : devicesUUID) {
			em = emf.getEntityManager(appliUuid);
			device = em.get(uuid,Device.class);
			if(device!=null){				
				messageSender = messageSenderInstance.getSenderInstance(device);
				if(messageSender!=null)
					messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));				
			}
		}		
	}

	@Override
	public void sendNotificationByUsers(String jsonInput,UUID appliUuid,List<String> usersnamesList)throws Exception {
		List<String> usersnames = null;
		if(usersnamesList==null){
			usersnames = getUsersListFromJSONPayload(jsonInput);
		}
		else{
			usersnames = usersnamesList;
		}
		List<Device> devicesOfUser = null;
		EntityNotifier appleNotifier = nsp.getNotifier(appliUuid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appliUuid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		if(usersnames!=null){
			for (String username : usersnames) {
				devicesOfUser = getUserDevices(appliUuid, username);
				if(devicesOfUser!=null){
					for (Device device : devicesOfUser) {
						messageSender = messageSenderInstance.getSenderInstance(device);
						if(messageSender!=null)
							messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));					
					}				
				}			
			}			
		}		
	}
	
	private List<UUID> getDevicesListFromJSONPayload(String jsonInput){
		List<UUID> devicesUUIDs = null;
		JSONArray jsonArray = null;		
		try {
			JSONObject jsonObject = new JSONObject(jsonInput);			
			if(jsonObject!=null){
				jsonArray = jsonObject.getJSONArray("recv");
				if(jsonArray!=null){					
					devicesUUIDs = new ArrayList<UUID>();
					for(int index=0;index<jsonArray.length();++index){
						devicesUUIDs.add(UUID.fromString(jsonArray.getString(index)));			
					}
				}
			}			
			
		} catch (JSONException ex) {
			System.out.println("Exception: " + ex);
		}		
		return devicesUUIDs;		
	}
	
	private List<String> getUsersListFromJSONPayload(String jsonInput){
		List<String> usersnames = null;
		JSONArray jsonArray = null;		
		try {
			JSONObject jsonObject = new JSONObject(jsonInput);			
			if(jsonObject!=null){
				jsonArray = jsonObject.getJSONArray("recv");
				if(jsonArray!=null){					
					usersnames = new ArrayList<String>();
					for(int index=0;index<jsonArray.length();++index){
						usersnames.add(jsonArray.getString(index));			
					}
				}
			}			
			
		} catch (JSONException ex) {
			System.out.println("Exception: " + ex);
		}		
		return usersnames;		
	}
	
	private List<String> getGroupsListFromJSONPayload(String jsonInput){
		List<String> groupsNames = null;
		JSONArray jsonArray = null;		
		try {
			JSONObject jsonObject = new JSONObject(jsonInput);			
			if(jsonObject!=null){
				jsonArray = jsonObject.getJSONArray("recv");
				if(jsonArray!=null){					
					groupsNames = new ArrayList<String>();
					for(int index=0;index<jsonArray.length();++index){
						groupsNames.add(jsonArray.getString(index));			
					}
				}
			}			
			
		} catch (JSONException ex) {
			System.out.println("Exception: " + ex);
		}		
		return groupsNames;	
	}
	
	private List<User> getUsersFromGroup(String groupName,UUID appUuid)throws Exception{
		EntityManager em = emf.getEntityManager(appUuid);

		List<User> users = null;

		ServiceManager sm = smf.getServiceManager(appUuid);
		ServiceResults srq = null;

		srq = sm.newRequest(ServiceAction.GET,
				parameters("groups", groupName, "users")).execute();

		List<Entity> entityList = srq.getEntities();

		if(entityList!=null){
			users = new ArrayList<User>();
			for (Entity entity : entityList) {
				UUID id = entity.getUuid();
				try {
					users.add(em.get(id, User.class));
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}
			}			
		}
		return users;		
	}

	@Override
	public void sendNotificationByApp(String jsonInput, UUID appliUuid)
			throws Exception {
		List<Device> deviceList = getAllDevicesFromApp(appliUuid);
		EntityNotifier appleNotifier = nsp.getNotifier(appliUuid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appliUuid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		if(deviceList!=null){
			for (Device device : deviceList) {			
				messageSender = messageSenderInstance.getSenderInstance(device);
				if(messageSender!=null)
					messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));
			}			
		}				
	}
	
	private List<Device> getAllDevicesFromApp(UUID appUuid)throws Exception{		
		ServiceManager serviceManager = smf.getServiceManager(appUuid);
		ServiceResults serviceResults = serviceManager.newRequest(ServiceAction.GET, parameters("devices")).execute();
		List<Device> devices = null;
		List<Entity> resultEntities = serviceResults.getEntities();
		if(resultEntities!=null){
			devices = new ArrayList<Device>();
			for (Entity entity : resultEntities) {
				devices.add((Device)entity);
			}			
		}
		return devices;		
	}

	@Override
	public void sendNotificationByDeviceId(String jsonInput, UUID appliUuid,
			UUID devicesUUID) throws Exception {
		Device device = null;		
		EntityNotifier appleNotifier = nsp.getNotifier(appliUuid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appliUuid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		EntityManager em = emf.getEntityManager(appliUuid);
		if(em != null){
			device = em.get(devicesUUID,Device.class);
			if(device!=null){
				messageSender = messageSenderInstance.getSenderInstance(device);
				if(messageSender!=null)
					messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));				
			}	
		}
		
	}

	@Override
	public void sendNotificationByGroupName(String jsonInput, UUID appliUuid,
			String groupName) throws Exception {
		List<User> users = getUsersFromGroup(groupName,appliUuid);
		List<String> usersNames = null;
		if(users!=null){
			usersNames = new ArrayList<String>();
			for (User user : users) {
				usersNames.add(user.getUsername());
			}
			sendNotificationByUsers(jsonInput, appliUuid, usersNames);
		}		
	}

	@Override
	public void sendNotificationByUsername(String jsonInput, UUID appliUuid,
			String username) throws Exception {
		List<Device> devices = getUserDevices(appliUuid, username);
		EntityNotifier appleNotifier = nsp.getNotifier(appliUuid, "apple");		
		EntityNotifier androidNotifier = nsp.getNotifier(appliUuid, "google");
		MessageSender messageSenderInstance = MessageSender.getMessageSenderInstance(appleNotifier,androidNotifier);
		IMessageSender messageSender = null;
		for (Device device : devices) {			
			messageSender = messageSenderInstance.getSenderInstance(device);
			if(messageSender!=null)
				messageSender.sendPushNotification(getMessage(jsonInput),getBody(jsonInput));
		}
		
	}
}
