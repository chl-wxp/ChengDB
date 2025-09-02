package org.apache.chengdb.server.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseConnection {
    private String name;
    private String type;
    private String host;
    private int port;
    private String database;
    private String username;

    public String toString(){
        return name;
    }
}