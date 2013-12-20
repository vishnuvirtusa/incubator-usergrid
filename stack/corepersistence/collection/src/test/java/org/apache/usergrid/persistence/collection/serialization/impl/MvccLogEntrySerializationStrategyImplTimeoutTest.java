package org.apache.usergrid.persistence.collection.serialization.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.usergrid.persistence.collection.CollectionScope;
import org.apache.usergrid.persistence.collection.cassandra.CassandraRule;
import org.apache.usergrid.persistence.collection.guice.MigrationManagerRule;
import org.apache.usergrid.persistence.collection.guice.TestCollectionModule;
import org.apache.usergrid.persistence.collection.impl.CollectionScopeImpl;
import org.apache.usergrid.persistence.collection.mvcc.MvccLogEntrySerializationStrategy;
import org.apache.usergrid.persistence.collection.mvcc.entity.MvccLogEntry;
import org.apache.usergrid.persistence.collection.mvcc.entity.Stage;
import org.apache.usergrid.persistence.collection.mvcc.entity.impl.MvccLogEntryImpl;
import org.apache.usergrid.persistence.model.entity.Id;
import org.apache.usergrid.persistence.model.entity.SimpleId;
import org.apache.usergrid.persistence.model.util.UUIDGenerator;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;


/** @author tnine */
@RunWith( JukitoRunner.class )
@UseModules(  MvccLogEntrySerializationStrategyImplTimeoutTest.TimeoutEnv.class )
public class MvccLogEntrySerializationStrategyImplTimeoutTest {


    /** Set our timeout to 1 second.  If it works for 1 seconds, we'll be good a any value */
    private static final int TIMEOUT = 1;


    @Inject
    private MvccLogEntrySerializationStrategy logEntryStrategy;


    @ClassRule
    public static CassandraRule rule = new CassandraRule();


    @Inject
    @Rule
    public MigrationManagerRule migrationManagerRule;



    /**
     * No need to add the @Inject annotation here, Jukito injects automatically:
     * doing so will create a serious issue. Note we must inject this
     * MvccLogEntrySerializationStrategy to override the one created by the
     * class level module for the logEntryStrategy class field. The method argument
     * version is injected by the method level module.
     *
     * @param logEntryStrategy automatically injected using the method's own module TimeoutEnv
     */
    @Test
    public void transientTimeout( MvccLogEntrySerializationStrategy logEntryStrategy ) throws ConnectionException, InterruptedException {
        final Id organizationId = new SimpleId( "organization" );
        final Id applicationId = new SimpleId( "application" );
        final String name = "test";


        CollectionScope context = new CollectionScopeImpl(organizationId, applicationId, name );


        final SimpleId id = new SimpleId( "test" );
        final UUID version = UUIDGenerator.newTimeUUID();

        for ( Stage stage : Stage.values() ) {
            MvccLogEntry saved = new MvccLogEntryImpl( id, version, stage );
            logEntryStrategy.write( context, saved ).execute();

            //Read it back after the timeout

            //noinspection PointlessArithmeticExpression
            Thread.sleep( TIMEOUT * 1000 );

            MvccLogEntry returned = logEntryStrategy.load( context, id, version );


            if ( stage.isTransient() ) {
                assertNull( "Active is transient and should time out", returned );
            }
            else {
                assertNotNull( "Committed is not transient and should be returned", returned );
                assertEquals( "Returned should equal the saved", saved, returned );
            }
        }
    }


    public static class TimeoutEnv extends AbstractModule {
        @Override
        protected void configure() {

            //override the timeout property
            Map<String, String> overrides = new HashMap<String, String>();
            overrides.put( MvccLogEntrySerializationStrategyImpl.TIMEOUT_PROP, String.valueOf( TIMEOUT ) );

            //use the default module with cass
            install( new TestCollectionModule( overrides ) );
        }
    }
}

