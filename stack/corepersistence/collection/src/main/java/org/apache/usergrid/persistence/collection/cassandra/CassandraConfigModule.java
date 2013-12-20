/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/13/13
 * Time: 8:26 PM
 */
package org.apache.usergrid.persistence.collection.cassandra;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.usergrid.persistence.collection.archaius.DynamicPropertyNames;
import org.apache.usergrid.persistence.collection.guice.PropertyUtils;

import com.google.inject.AbstractModule;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.ConfigurationManager;

/**
 * This Module is responsible for injecting dynamic properties into a {@link
 * DynamicCassandraConfig} object and injecting a singleton instance of it
 * anywhere an {@link ICassandraConfig} or {@link IDynamicCassandraConfig} is
 * required.
 */
public class CassandraConfigModule extends AbstractModule {
    /** The location of the defaults properties file */
    private static final String CASSANDRA_DEFAULTS_PROPERTIES = "cassandra-defaults.properties";



    public CassandraConfigModule() {
    }



    protected void configure() {

        bind( ICassandraConfig.class ).to( DynamicCassandraConfig.class ).asEagerSingleton();
        bind( IDynamicCassandraConfig.class ).to( DynamicCassandraConfig.class ).asEagerSingleton();



    }
}
