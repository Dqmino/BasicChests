package tk.dqmino.basicchest.blocking.impl;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import tk.dqmino.basicchest.blocking.interfaces.Database;

import java.net.UnknownHostException;

public class MongoDatabase implements Database {

    private final String USERNAME;
    private final int PORT;
    private final String IP;
    private final String PASSWORD;
    private DBCollection playersCollection;
    private DB serverDB;
    private MongoClient client;

    public MongoDatabase(String username, int port, String ip, String password) {
        USERNAME = username;
        PORT = port;
        IP = ip;
        PASSWORD = password;
    }

    @Override
    public boolean connect() {

        try {
            client = new MongoClient(new MongoClientURI("mongodb://" + USERNAME + ":" + PASSWORD + "@" + IP + ":" + PORT + "/"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        serverDB = client.getDB("playerchests");

        playersCollection = serverDB.getCollection("players");
        return true;
    }

    @Override
    public boolean disconnect() {
        client.close();
        return true;
    }

    public DBCollection getPlayersCollection() {
        return playersCollection;
    }

    public DB getServerDB() {
        return serverDB;
    }
}
