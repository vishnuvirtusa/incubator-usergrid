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
package org.apache.usergrid.rest.applications.groups;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.usergrid.rest.ApiResponse;
import org.apache.usergrid.rest.applications.ServiceResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.json.JSONWithPadding;

@Component("org.apache.usergrid.rest.applications.groups.GroupsResource")
@Scope("prototype")
@Produces(MediaType.APPLICATION_JSON)
public class GroupsResource extends ServiceResource {	

	@POST
	@Path("{groupName}/push")
	public JSONWithPadding sendPushToDevice(@PathParam("groupName") String groupName,@FormParam("message") String message)throws Exception{		
		ApiResponse response;
		if(message!=null){
			pushService.sendNotificationByGroupName(message, getApplicationId(), groupName);
			response = createApiResponse();
			response.setAction("Send Notifications By Groups");			
			response.setSuccess();
			return new JSONWithPadding(response);
		}		
		return null;
	}	
	
	@POST
	@Path("push")
	public JSONWithPadding sendPushToDevices(@FormParam("message") String message)throws Exception{
		ApiResponse response;
		if(message!=null){
			pushService.sendNotificationByGroups(message,getApplicationId());
			response = createApiResponse();
			response.setAction("Send Notifications By Groups");			
			response.setSuccess();
			return new JSONWithPadding(response);
		}		
		return null;
	}
}