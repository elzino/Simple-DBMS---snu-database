package DDL;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-11-04.
 */
public class ForeignKeyDefinition {
    public ArrayList<String> referencingColumns = new ArrayList<String>();
    public String referencedTable;
    public ArrayList<String> referencedColumns = new ArrayList<String>();

    // ex) column1/column2/column3$tableName$column4/column5
    public String toString(){
        String description = "";

        description += String.join("/", referencingColumns) +"%";
        description += referencedTable + "%";
        description += String.join("/", referencedColumns);

        return description;
    }
}
