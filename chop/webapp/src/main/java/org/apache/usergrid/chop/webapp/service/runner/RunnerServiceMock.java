/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.webapp.service.runner;

import org.apache.usergrid.chop.api.Runner;
import org.apache.usergrid.chop.api.State;
import org.apache.usergrid.chop.api.StatsSnapshot;

import java.util.Date;

/**
 * A mock implementation of RunnerService for testing.
 */
public class RunnerServiceMock implements RunnerService {

    public State getState(Runner runner) {
        return State.RUNNING;
    }

    @Override
    public StatsSnapshot getStats( Runner runner ) {
        return new StatsSnapshot( 5L, 333L, 111L, 222L, true, new Date().getTime(), 50 );
    }
}
