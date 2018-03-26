package DML.Where;

import DML.Record;
import DML.ReferedTable;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-01.
 */
public class Where {
    public BoolValExp BoolValExp = null;

    public int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
        // | -1 error | 0 false | 1 true | 2 unknown |
    	if(this.BoolValExp != null) {
            int result = this.BoolValExp.execute(RefTableList, RecordList);
            return result;
        }
    	//Where절이 입력안된경우 항상 1을 return
        else{
            return 1;
        }
    }
}
