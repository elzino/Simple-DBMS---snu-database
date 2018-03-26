package DBQuery;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import DML.Record;
import DML.Value;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


import com.sleepycat.je.DatabaseEntry;


import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;


/**
 * Created by 1997j on 2017-11-05.
 */

public class DBQuery {
    public static String findTableSchemaByKey (String tableName, Cursor cursor){
        String result = null;
        try {
            DatabaseEntry searchKey = new DatabaseEntry(tableName.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry();
            if (cursor.getSearchKey(searchKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                result = new String(foundData.getData(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static String findTableSchemaByKey (String tableName){
        String result = null;

        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "tableSchema", dbConfig);

        Cursor cursor = null;

        try {
            cursor = myDatabase.openCursor(null, null);
            DatabaseEntry searchKey = new DatabaseEntry(tableName.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry();
            if (cursor.getSearchKey(searchKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                result = new String(foundData.getData(), "UTF-8");
            }
            cursor.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        myDbEnvironment.sync();
        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();

        return result;
    }

    // return data or null
    public static String findRecordByKey(String tableName, String key){
        String result = null;

        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "data#"+tableName , dbConfig);



        try {
            DatabaseEntry searchKey = new DatabaseEntry(key.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry();
            if (myDatabase.get(null, searchKey, foundData, null) == OperationStatus.SUCCESS) {
                result = new String(foundData.getData(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        myDbEnvironment.sync();
        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();

        //if record does not exist, it returns null
        return result;
    }

    // return data or null
    public static String findRecordByKey(Database myDatabase , String key){
        String result = null;
        try {
            DatabaseEntry searchKey = new DatabaseEntry(key.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry();
            if (myDatabase.get(null, searchKey, foundData, null) == OperationStatus.SUCCESS) {
                result = new String(foundData.getData(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        //if record does not exist, it returns null
        return result;
    }

    public static void writeRecordByKey(String tableName, String key, String data){
        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "data#"+tableName , dbConfig);

        try {
            DatabaseEntry searchKey = new DatabaseEntry(key.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry(data.getBytes("UTF-8"));
            myDatabase.delete(null, searchKey);
            myDatabase.put(null, searchKey, foundData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        myDbEnvironment.sync();
        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();
    }

    public static void writeRecordByKey(Database myDatabase , String key, String data){
        String result = null;
        try {
            DatabaseEntry searchKey = new DatabaseEntry(key.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry(data.getBytes("UTF-8"));
            myDatabase.delete(null, searchKey);
            myDatabase.put(null, searchKey, foundData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void upDateRecordNullByKeyIndex(String tableName, String key, ArrayList<Integer> IndexList){
        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "data#"+tableName , dbConfig);

        try {
            DatabaseEntry searchKey = new DatabaseEntry(key.getBytes("UTF-8"));
            DatabaseEntry foundData = new DatabaseEntry();
            if (myDatabase.get(null, searchKey, foundData, null) == OperationStatus.SUCCESS) {
                //read record and update to null value
                String foundrecord = new String(foundData.getData(), "UTF-8");
                Record Record = new Record(foundrecord);
                ArrayList<Value> ValueList = Record.ValueList;
                for(Integer i : IndexList){
                    Value Value = ValueList.get(i);
                    Value.updateToNull();
                }
                //write Updated record to DB
                String updatedRecord = Record.toString();
                DatabaseEntry updatedData = new DatabaseEntry(updatedRecord.getBytes("UTF-8"));
                myDatabase.delete(null, searchKey);
                myDatabase.put(null, searchKey, updatedData);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        myDbEnvironment.sync();
        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();

    }
}