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
package org.apache.usergrid.rest.applications.notifiers;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.entities.File;
import org.apache.usergrid.persistence.entities.GoogleNotifier;
import org.apache.usergrid.rest.applications.ApplicationResource;
import org.apache.usergrid.rest.applications.ServiceResource;
import org.apache.usergrid.rest.security.annotations.RequireApplicationAccess;
import org.apache.usergrid.services.ServicePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.multipart.FormDataParam;

import static org.apache.usergrid.utils.ConversionUtils.string;

@Component("org.apache.usergrid.rest.applications.notifiers.NotifiersResource")
@Scope("prototype")
@Produces(MediaType.APPLICATION_JSON)
public class NotifiersResource extends ServiceResource {
	public static final Logger logger = LoggerFactory
			.getLogger(ApplicationResource.class);
	private static File file;
	
	@GET
	@Path("*")
	public Map<String,EntityNotifier> getAllNotifiers()throws Exception{		
		return management.getAllNotifiers(getApplicationId());		
	}	
	
	@PUT
	@Path("{notifierName}/p12")
	public EntityNotifier bindP12File(@PathParam("notifierName") String notifierName)throws Exception{	
		ServicePayload servicePayload = new ServicePayload();
		servicePayload.setProperty("file",file);
		EntityNotifier entityNotifier = management.updateNotifier(getApplicationId(),notifierName, servicePayload);
		file=null;
        return entityNotifier;
	}
	
	@POST
	@RequireApplicationAccess
	@Path("p12")
	@Consumes(MediaType.MULTIPART_FORM_DATA)	
	public void registerP12ToNotifier(@FormDataParam("file") InputStream inputStream) {		
		if (file == null) {			
			file = new File();			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();			
			byte[] bytes = new byte[1024];
			int data = 0;
			try {			
				if(inputStream!=null){					
					while ((data = inputStream.read(bytes,0,bytes.length))!=-1) {			
						byteArrayOutputStream.write(bytes,0,data);						
					}
					file.setSizeOfFile(byteArrayOutputStream.size());
					file.setFileAsByteArray(byteArrayOutputStream.toByteArray());
					logger.debug("NotifiersResource.executeUploding"+file.getFileDetails());					
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}						
		}
	}	

}