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
 * Created by 1997j on 2017-12-02.
 */
public class NullPredicate extends Predicate {
    String newTableName = null;
    String columnName;
    boolean isNot = false;

    public NullPredicate(String newTableName, String columnName, boolean isNot) {
        this.newTableName = newTableName;
        this.columnName = columnName;
        this.isNot = isNot;
    }

    // | -1 error | 0 false | 1 true | 2 unknown |
    @Override
    public int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
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
                return -1;
            }
            if (tableIndex.size() > 1) {
                System.out.println(Messages.WhereAmbiguousReference);
                return -1;
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
                return -1;
            }

            Value Val = Record.ValueList.get(columnIndex);
            boolean result = Val.getType()==4;
            if(isNot){
                result = !result;
            }
            return result ? 1 : 0;
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
                return -1;
            }
            else if(ValueList.size()>1){
                System.out.println(Messages.WhereAmbiguousReference);
                return -1;
            }
            boolean result = ValueList.get(0).getType() == 4;
            if(isNot){
                result = !result;
            }
            return result ? 1 : 0;
        }
    }
}
