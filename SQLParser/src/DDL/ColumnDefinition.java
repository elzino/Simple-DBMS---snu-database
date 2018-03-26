package DDL;


public class ColumnDefinition {
    public String columnName;
    // | int 1 | char 2 | date 3 | null 4 |
    public int type;
    public int charLength= 0;
    public boolean notNull = false;
    public boolean primaryKey = false;
    public boolean foreignKey = false;

    public ColumnDefinition(){}
    public ColumnDefinition(String columnName){
        this.columnName = columnName;
    }


    public String toString(){
        String description = "";
        description+=columnName+"/";
        description+=type+"/";
        description+=charLength+"/";
        description+= (notNull)? 1 : 0 ;
        description+="/";
        description+= (primaryKey)? 1 : 0 ;
        description+="/";
        description+= (foreignKey)? 1 : 0 ;

        return description;
    }

    public String getType(){
        return type+"/"+charLength;
    }
}
