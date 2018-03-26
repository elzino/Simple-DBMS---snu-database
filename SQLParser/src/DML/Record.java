package DML;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 1997j on 2017-11-29.
 */
public class Record {
    public String newTableName;
    private String recordData;
    public ArrayList<Value> ValueList = new ArrayList<>();
    public ArrayList<ForeignRecordData> ReferencedData = new ArrayList<ForeignRecordData>();
    private boolean isReferenced;
    private boolean notNullReferenced;


    public Record(String record){
        this.recordData = record;
        int index = record.indexOf('"');
        // not referenced
        if (index == -1) {
            isReferenced = false;
            String[] values = record.split("'");
            for(int i = 0; i < values.length; i++){
                this.ValueList.add(new Value(values[i]));
            }
        }
        // has referenced
        else{
            isReferenced = true;
            String[] values = record.substring(0, index).split("'");
            for(int i = 0; i < values.length; i++){
                this.ValueList.add(new Value(values[i]));
            }
            String[] referencedDatas = record.substring(index+1).split("\"");
            int num = referencedDatas.length/4;
            for(int i= 0; i < num ; i++){
                if(referencedDatas[i*4 + 2].equals("1")){
                    this.notNullReferenced = true;
                }
                this.ReferencedData.add(new ForeignRecordData(Arrays.copyOfRange(referencedDatas,i*4, (i+1)*4)));
            }
        }
    }

    public boolean isReferenced(){
        return this.isReferenced;
    }

    public boolean isNotNullReferenced(){
        return this.notNullReferenced;
    }

    public void setIsReferenced(boolean bool){
        this.isReferenced = bool;
    }

    public String convertIndexToKey(ArrayList<Integer> IndexList){
        String key = "";
        for(Integer i : IndexList){
            Value Val = this.ValueList.get(i);
            if(Val.getType() == 4){
                return null;
            }
            key += Val.toString();
        }
        return key;
    }

    public String toString(){
        String record = "";
        for(Value value : this.ValueList){
            record += value.toString();
        }
        if(this.isReferenced){
            record += '"';
            for(ForeignRecordData ForeignData : this.ReferencedData){
                record += ForeignData.toString();
            }
        }
        return  record;
    }
}
