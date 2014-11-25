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
package org.apache.usergrid.services.notifiers;

import static org.apache.usergrid.services.ServiceParameter.parameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.usergrid.persistence.Entity;
import org.apache.usergrid.persistence.EntityManager;
import org.apache.usergrid.persistence.EntityManagerFactory;
import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.entities.AppleNotifier;
import org.apache.usergrid.persistence.entities.GoogleNotifier;
import org.apache.usergrid.services.ServiceAction;
import org.apache.usergrid.services.ServiceManager;
import org.apache.usergrid.services.ServiceManagerFactory;
import org.apache.usergrid.services.ServicePayload;
import org.apache.usergrid.services.ServiceResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NotifiersServiceProvider {

	
	private static final Logger logger = LoggerFactory
			.getLogger(NotifiersServiceProvider.class);
	
	
	protected EntityManagerFactory emf;
	protected ServiceManagerFactory smf;

	@Autowired
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		logger.info("NotifiersServiceProvider.setEntityManagerFactory");
		this.emf = emf;
	}

	@Autowired
	public void setServiceManagerFactory(ServiceManagerFactory smf) {
		logger.info("NotifiersServiceProvider.setServiceManagerFactory");
		this.smf = smf;
	}
	
	
	
	public Map<String, EntityNotifier> getAllNotifiers(UUID appId) {
		Map<String, EntityNotifier> notifiers = new HashMap<String, EntityNotifier>();
		try {
			EntityManager entityManager = emf.getEntityManager(appId);
			ServiceManager serviceManager = smf.getServiceManager(appId);
			ServiceResults serviceResults = serviceManager.newRequest(
					ServiceAction.GET, parameters("notifiers")).execute();
			List<Entity> resultEntities = serviceResults.getEntities();

			for (Entity entity : resultEntities) {
				if (entity.getName().equals("apple")) {
					notifiers.put(entity.getName(), entityManager.get(
							entity.getUuid(), AppleNotifier.class));
				} else if (entity.getName().equals("google")) {
					notifiers.put(entity.getName(), entityManager.get(
							entity.getUuid(), GoogleNotifier.class));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return notifiers;
	}

	public EntityNotifier getNotifier(UUID appId, String notifierName)
			throws Exception {
		Map<String, EntityNotifier> notifiers = getAllNotifiers(appId);
		if (notifiers.containsKey(notifierName)) {
			return notifiers.get(notifierName);
		} else {
			return null;
		}
	}


	public EntityNotifier updateNotifier(UUID appId, String notifierName,
			ServicePayload servicePayload) throws Exception {
		ServiceManager serviceManager = smf.getServiceManager(appId);

		serviceManager.newRequest(ServiceAction.PUT,
				parameters("notifiers", notifierName), servicePayload)
				.execute();

		return getNotifier(appId, notifierName);
	}
}
