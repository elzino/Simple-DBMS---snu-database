package DML.Where;

import DBQuery.DBQuery;
import DDL.ColumnDefinition;
import DDL.TableSchema;
import DML.Messages;
import DML.Record;
import DML.ReferedTable;
import DML.Value;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-03.
 */
public class CompOperand {
    // | 1 ComparableValue | 2 newTableNAme, columnName |
    int type;
    Value Value;
    String newTableName = null;
    String columnName;

    public CompOperand(Value Value){
        this.type = 1;
        this.Value = Value;
    }

    public CompOperand(String newTableName, String columnName){
        this.type = 2;
        this.newTableName = newTableName;
        this.columnName = columnName;
    }

    // if it has error, return null
    public Value getValue(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
        if(this.type == 1){
            return this.Value;
        }
        else{
            if(newTableName!=null) {
                ArrayList<Integer> tableIndex = new ArrayList<>();
                String oldTableName = null;
                ReferedTable RefTable;
                for (int i = 0; i < RefTableList.size(); i++) {
                    RefTable = RefTableList.get(i);
                    if (RefTable.newTableName.equals(this.newTableName)) {
                        tableIndex.add(i);
                        oldTableName = RefTable.tableName;
                    }
                }
                RefTable = null;
                if (tableIndex.size() == 0) {
                    System.out.println(Messages.WhereTableNotSpecified);
                    return null;
                }
                if (tableIndex.size() > 1) {
                    System.out.println(Messages.WhereAmbiguousReference);
                    return null;
                }

                TableSchema Schema = new TableSchema(oldTableName, DBQuery.findTableSchemaByKey(oldTableName));
                Record Record = RecordList.get(tableIndex.get(0));
                int columnIndex = -1;

                for (int i = 0; i < Schema.ColDefList.size(); i++) {
                    ColumnDefinition ColDef = Schema.ColDefList.get(i);
                    if (ColDef.columnName.equals(this.columnName)) {
                        columnIndex = i;
                        break;
                    }
                }

                if (columnIndex == -1) {
                    System.out.println(Messages.WhereColumnNotExist);
                    return null;
                }
                return Record.ValueList.get(columnIndex);
            }
            else{
                ArrayList<Value> ValueList = new ArrayList<>();
                for(int j = 0; j<RefTableList.size(); j++){
                    ReferedTable RefTable = RefTableList.get(j);
                    Record Record = RecordList.get(j);
                    String oldTableName = RefTable.tableName;
                    int columnIndex = -1;
                    TableSchema Schema = new TableSchema(oldTableName, DBQuery.findTableSchemaByKey(oldTableName));

                    for (int i = 0; i < Schema.ColDefList.size(); i++) {
                        ColumnDefinition ColDef = Schema.ColDefList.get(i);
                        if (ColDef.columnName.equals(this.columnName)) {
                            columnIndex = i;
                            break;
                        }
                    }
                    if(columnIndex==-1){
                        continue;
                    }
                    else{
                        ValueList.add(Record.ValueList.get(columnIndex));
                    }
                }

                if(ValueList.size()==0){
                    System.out.println(Messages.WhereColumnNotExist);
                    return null;
                }
                else if(ValueList.size()>1){
                    System.out.println(Messages.WhereAmbiguousReference);
                    return null;
                }
                return ValueList.get(0);
            }
        }

    }



}
