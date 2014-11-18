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

import java.util.List;
import java.util.UUID;

import org.apache.usergrid.persistence.entities.User;


public interface PushService {
	/**
	    * Send the push notification to the device
	    * @param message 
	    *     message as a JSON String 
	    * @throws Exception 
	    */
	    public void sendNotification(String message,UUID appId) throws Exception;
	    
	    public void sendNotificationByGroups(String jsonInput,UUID appliUuid)throws Exception;
	    
	    public void sendNotificationByDevices(String jsonInput,UUID appliUuid)throws Exception;
	    
	    public void sendNotificationByUsers(String jsonInput,UUID appliUuid,List<String> usersnamesList)throws Exception;
	    
	    public void sendNotificationByApp(String jsonInput,UUID appliUuid)throws Exception;
	    
	    public void sendNotificationByDeviceId(String jsonInput,UUID appliUuid,UUID devicesUUID)throws Exception;
	    
	    public void sendNotificationByGroupName(String jsonInput,UUID appliUuid,String groupName)throws Exception;
	    
	    public void sendNotificationByUsername(String jsonInput,UUID appliUuid,String username)throws Exception;	
	    
	    
	    
}
