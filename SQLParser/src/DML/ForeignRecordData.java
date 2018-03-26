package DML;

import java.io.File;
import java.util.ArrayList;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import DBQuery.DBQuery;

/**
 * Created by 1997j on 2017-11-29.
 */
public class ForeignRecordData {
    //referenced Data to help write
    private String referencedTableName;
    private String referencedPrimaryKey;

    // written data
    String referencingTableName;
    String primaryKey;
    boolean notNull;
    ArrayList<Integer> ForeignColumnIndexToDelete = new ArrayList<>();

    // referencingTableName primaryKey notNull ForeignColumnIndexToDelete
    public ForeignRecordData(String[] writtenDatas){
        this.referencingTableName = writtenDatas[0];
        this.primaryKey = writtenDatas[1];
        this.notNull = writtenDatas[2].equals("1");
        String[] indexs = writtenDatas[3].split("\\$");
        for(String s : indexs){
            ForeignColumnIndexToDelete.add(Integer.parseInt(s));
        }
    }

    public ForeignRecordData(String referencedTableName, String referencedPrimaryKey, String referencingTableName, String primaryKey, boolean notNull, ArrayList<Integer> foreignColumnIndexToDelete) {
        this.referencedTableName = referencedTableName;
        this.referencedPrimaryKey = referencedPrimaryKey;
        this.referencingTableName = referencingTableName;
        this.primaryKey = primaryKey;
        this.notNull = notNull;
        ForeignColumnIndexToDelete = foreignColumnIndexToDelete;
    }

    public void writeDB(){
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
        myDatabase = myDbEnvironment.openDatabase(null, "data#"+this.referencedTableName , dbConfig);

        String record = DBQuery.findRecordByKey(myDatabase, this.referencedPrimaryKey);
        //referenced가 없는 경우
        if(record.indexOf('"')==-1){
            record+='"';
        }
        record+=this.toString();

        DBQuery.writeRecordByKey(myDatabase,this.referencedPrimaryKey, record);
        myDbEnvironment.sync();
        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();
    }

    public boolean isNotNull(){
        return notNull;
    }

    // tableName"primaryKey"1"1$2$3$"
    public String toString(){
        String record = "";
        record += this.referencingTableName +'"' + this.primaryKey + '"';
        record += (this.notNull)? 1 : 0;
        record += '"';
        for(Integer i : this.ForeignColumnIndexToDelete){
            record += Integer.toString(i) + '$';
        }
        record += '"';
        return record;
    }
}
