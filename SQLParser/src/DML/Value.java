package DML;

import DDL.ColumnDefinition;

import java.rmi.MarshalException;

/**
 * Created by 1997j on 2017-11-28.
 */


public class Value {
    String tableName;
    String NewTableName;
    String newColumnName;

    String columnName;
    boolean notNull;
    boolean primaryKey;
    boolean foreignKey;
    int colDefType;
    int colDefCharLength;

    // | integer 1 | char 2 | date 3 | null 4 |
    int type;
    String value;

    //parse from data to Construct Value Class
    public Value(String typePlusValue){
        int index = typePlusValue.indexOf('#');
        this.type = Integer.parseInt(typePlusValue.substring(0, index));
        this.value = typePlusValue.substring(index+1);
    }

    // jj파일에서 Value 만들 때
    public Value(String value, int type){
        this.value = value;
        this.type = type;
    }

    public void upDateColDef(ColumnDefinition Coldef){
        this.colDefType = Coldef.type;
        this.colDefCharLength = Coldef.charLength;
        this.notNull = Coldef.notNull;
        this.primaryKey = Coldef.primaryKey;
        this.foreignKey = Coldef.foreignKey;
        this.columnName = Coldef.columnName;
    }

    public boolean hasTypeError(){
        if(type==4){
            if(notNull) {
                System.out.println(Messages.InsertColumnNonNullableError(columnName));
                return true;
            }
            else{
                return false;
            }
        }
        if(type!=colDefType){
            System.out.println(Messages.InsertTypeMismatchError);
            return true;
        }
        //truncate char value
        else if(type==2 && value.length() > this.colDefCharLength){
            this.value = this.value.substring(0, this.colDefCharLength);
        }
        return false;
    }

    public void updateToNull(){
        this.type = 4;
        this.value = null;
    }

    public String toString(){
        return type+"#"+value+"'";
    }

    public int getType(){
        return this.type;
    }

    public String getValue(){
        return this.value;
    }

    public int compareTo(Value RightValue){
        if(this.type == 1){
            int left = Integer.parseInt(this.value);
            int right = Integer.parseInt(RightValue.getValue());
            if(left<right){
                return -1;
            }
            else if (left==right){
                return 0;
            }
            else{
                return 1;
            }
        }
        else{
            return this.value.compareTo(RightValue.getValue());
        }
    }
}