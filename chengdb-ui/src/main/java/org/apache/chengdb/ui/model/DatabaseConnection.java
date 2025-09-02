package org.apache.chengdb.ui.model;

public class DatabaseConnection {
    private String name;
    private String type; // mysql, postgresql, oracle, etc.
    private String host;
    private int port;
    private String database;
    private String username;
    private boolean connected;

    public DatabaseConnection(String name, String type, String host, int port, String database, String username) {
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.connected = false;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    @Override
    public String toString() {
        return name;
    }
}