package DML.Where;

import DML.Record;
import DML.ReferedTable;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-02.
 */
public class BooleanFactor {
    public boolean isNot = false;
    public BooleanTest BTest;

    //| -1 error | 0 false | 1 true | 2 unknown |
    public int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
        int result = BTest.execute(RefTableList, RecordList);
        if(result == -1){
            return -1;
        }
        if(isNot){
            if(result==0){
                return 1;
            }
            else if(result == 1){
                return 0;
            }
            else{
                return 2;
            }
        }
        else{
            return result;
        }
    }
}
