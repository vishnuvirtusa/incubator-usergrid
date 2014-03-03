
// may be needed in the future 

String hostName  = (String)System.getenv().get("PUBLIC_HOSTNAME")
def nodeParts = ((String)System.getenv().get("DB_SERVERS")).split(",")
def nodes = ""
def sep = ""
nodeParts.each() { part -> 
    nodes = nodes + sep + "\"" + part + "\"";  
    sep = ","
}

def elasticSearchConfig = """
cluster.name: usergrid
discovery.zen.ping.multicast.enabled: false
discovery.zen.ping.unicast.hosts: [${nodes}]
node:
    name: ${hostName} 
network:
    host: ${hostName}
"""
println elasticSearchConfig