package org.apache.usergrid.persistence.collection.astynax;


import java.util.HashSet;
import java.util.Set;

import org.apache.usergrid.persistence.collection.cassandra.CassandraConfigEvent;
import org.apache.usergrid.persistence.collection.cassandra.CassandraConfigListener;
import org.apache.usergrid.persistence.collection.cassandra.ConfigChangeType;
import org.apache.usergrid.persistence.collection.cassandra.ICassandraConfig;
import org.apache.usergrid.persistence.collection.cassandra.IDynamicCassandraConfig;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netflix.astyanax.AstyanaxConfiguration;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.Slf4jConnectionPoolMonitorImpl;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;


/**
 * TODO.  Provide the ability to do a service hook for realtime tuning without the need of a JVM restart This could be
 * done with governator and service discovery
 *
 * @author tnine
 */
public class AstynaxKeyspaceProvider implements Provider<Keyspace> {
    private final IDynamicCassandraConfig cassandraConfig;


    @Inject
    public AstynaxKeyspaceProvider( final IDynamicCassandraConfig cassandraConfig ) {
        this.cassandraConfig = cassandraConfig;
    }


    @Override
    public Keyspace get() {
        AstyanaxConfiguration config = new AstyanaxConfigurationImpl().setDiscoveryType( NodeDiscoveryType.TOKEN_AWARE )
                                                                      .setTargetCassandraVersion(
                                                                              cassandraConfig.getVersion() );

        ConnectionPoolConfiguration connectionPoolConfiguration =
                new ConnectionPoolConfigurationImpl( "UsergridConnectionPool" ).setPort( cassandraConfig.getPort() )
                                                                               .setMaxConnsPerHost( cassandraConfig
                                                                                       .getConnections() )
                                                                               .setSeeds( cassandraConfig.getHosts() )
                                                                               .setSocketTimeout(
                                                                                       cassandraConfig.getTimeout() );

        AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder().forCluster( cassandraConfig.getClusterName() )
                .forKeyspace( cassandraConfig.getKeyspaceName() )

                        /*
                         * TODO tnine Filter this by adding a host supplier.  We will get token discovery from cassandra
                         * but only connect
                         * to nodes that have been specified.  Good for real time updates of the cass system without
                         * adding
                         * load to them during runtime
                         */

                .withAstyanaxConfiguration( config ).withConnectionPoolConfiguration( connectionPoolConfiguration )
                .withConnectionPoolMonitor( new Slf4jConnectionPoolMonitorImpl() )
                .buildKeyspace( ThriftFamilyFactory.getInstance() );

        context.start();


        cassandraConfig.register( new ContextUpdater( context ) );


        return context.getClient();
    }


    private static class ContextUpdater implements CassandraConfigListener {

        private final AstyanaxContext<Keyspace> context;


        private ContextUpdater( final AstyanaxContext<Keyspace> context ) {this.context = context;}


        @Override
        public void reconfigurationEvent( final CassandraConfigEvent event ) {
            ICassandraConfig config = event.getCurrent();

            //aok: Do we need the event type, or should we just do a diff all the time?

            //hosts have changed.
            if(event.hasChange( ConfigChangeType.HOSTS ) || event.hasChange( ConfigChangeType.PORT )){
                  updateHosts( event );
            }
        }


        /**
         * Update the hosts internally
         * @param event
         */
        private void updateHosts(CassandraConfigEvent event){

            ICassandraConfig newConfig = event.getCurrent();
            ICassandraConfig oldConfig = event.getOld();

            Set<String> oldHosts = getHosts( event.getOld().getHosts() );
            Set<String> newHosts = getHosts( event.getCurrent().getHosts() );


            //add all hosts, even if the exist.  Existing hosts are no-op'ed
            for(String newHost: newHosts){
                context.getConnectionPool().addHost( new Host(newHost, newConfig.getPort()), true );
            }

            oldHosts.removeAll( newHosts );

            //if there's anything left in old hosts, remove them
            for(String oldHost: oldHosts){
                context.getConnectionPool().removeHost( new Host(oldHost,oldConfig.getPort() ), true );
            }


        }


        /**
         * Get all hosts as a set for intersection help
         * @param hostString
         * @return
         */
        private Set<String> getHosts(String hostString){
            Set<String> hostSet = new HashSet<String>();

            for(String string: hostString.split( "," )){
                hostSet.add( string.trim() );
            }

            return hostSet;
        }
    }
}
