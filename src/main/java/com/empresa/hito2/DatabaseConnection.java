package com.empresa.hito2;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect(String uri, String dbName) {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(dbName);
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }
}