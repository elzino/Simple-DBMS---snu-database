package DML.Where;

import DML.Record;
import DML.ReferedTable;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-03.
 */
public class BoolValExp extends BooleanTest {
    public ArrayList<BooleanTerm> BTermList = new ArrayList<>();

    //  | -1 error | 0 false | 1 true | 2 unknown |
    public int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
        int result = 0;
        for(BooleanTerm BTerm : this.BTermList){
            int termResult = BTerm.execute(RefTableList, RecordList);
            if(termResult == -1){
                return -1;
            }
            else if(termResult == 1){
                return 1;
            }
            else if(termResult == 2){
                result = 2;
            }
        }
        return result;
    }
}
