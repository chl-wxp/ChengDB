package org.apache.chengdb.ui.service;

import org.apache.chengdb.ui.model.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    
    public static List<DatabaseConnection> getConnectionList() {
        // 模拟从后端获取连接列表
        List<DatabaseConnection> connections = new ArrayList<>();
        
        connections.add(new DatabaseConnection(
            "MySQL - 本地开发", 
            "mysql", 
            "localhost", 
            3306, 
            "test_db", 
            "root"
        ));
        
        connections.add(new DatabaseConnection(
            "PostgreSQL - 测试环境", 
            "postgresql", 
            "test.example.com", 
            5432, 
            "test_db", 
            "postgres"
        ));
        
        connections.add(new DatabaseConnection(
            "Oracle - 生产环境", 
            "oracle", 
            "prod.example.com", 
            1521, 
            "ORCL", 
            "admin"
        ));
        
        return connections;
    }
    
    public static boolean testConnection(DatabaseConnection connection) {
        // 模拟连接测试
        try {
            Thread.sleep(1000); // 模拟网络延迟
            return Math.random() > 0.2; // 80%成功率
        } catch (InterruptedException e) {
            return false;
        }
    }
}