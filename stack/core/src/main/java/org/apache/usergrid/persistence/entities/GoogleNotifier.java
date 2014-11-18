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

package org.apache.usergrid.persistence.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.usergrid.persistence.EntityNotifier;
import org.apache.usergrid.persistence.TypedEntity;
import org.apache.usergrid.persistence.annotations.EntityProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
public class GoogleNotifier extends TypedEntity implements EntityNotifier {

	@EntityProperty(indexed = true, fulltextIndexed = true, required = false, aliasProperty = true, unique = true, basic = true)
	protected String name;
	@EntityProperty
	private String apiKey;
	@EntityProperty
	private Boolean delay_while_idle;
	@EntityProperty
	private Integer time_to_live;
	@EntityProperty
	private Boolean enablePush;
	
	@Override
	@JsonSerialize (include = Inclusion.NON_NULL)
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	@XmlElement (name = "time_to_live")
	public Integer getTimeToLive() {
		return time_to_live;
	}
	
	public void setTimeToLive(Integer time_to_live) {
		this.time_to_live = time_to_live;
	}
	
	@Override
	@XmlElement (name = "enablePush")
	public Boolean getEnablePush() {
		return enablePush;
	}

	@Override
	public void setEnablePush(Boolean enablePush) {
		this.enablePush = enablePush;
	}

	@XmlElement(name="delay_while_idle")
	public Boolean getDelay_while_idle() {
		return delay_while_idle;
	}
	
	public void setDelay_while_idle(Boolean delay_while_idle) {
		this.delay_while_idle = delay_while_idle;
	}

	@XmlElement(name="apiKey")
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}	
}