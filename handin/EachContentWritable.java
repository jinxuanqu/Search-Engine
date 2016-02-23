package chaohParse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class EachContentWritable implements WritableComparable<EachContentWritable>{
	//variable: title, word
    private String title ;
    private String word;
    
    //constructor---
    public EachContentWritable(String a,String b){
    	title = a;
    	word = b;
	}
    
    public EachContentWritable(){
    	title = "";
    	word = "";
    }
    
    /*------ set function ------*/
    public void setWord(String x) { this. word = x;}
    public void setTitle(String x) { this. title = x;}
    
    
    /*------ get function ------*/
    public String getWord() { return word ;}
    public String getTitle() { return title ;}
    
    @Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		word = in.readUTF();
		title = in.readUTF();
	}
    @Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(word);
		out.writeUTF(title);
	}
    @Override
	public int compareTo(EachContentWritable arg0) {
		// TODO Auto-generated method stub
		int tmp = word .compareTo(arg0. word);
        if ( tmp != 0) return tmp;
        return title.compareTo(arg0.title);
	}
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return title + ":" + word ;
    }
}