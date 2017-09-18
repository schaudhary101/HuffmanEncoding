
// Creating a data type to hold 2 variables in the data of a tree
public class DataHolder {
	char c;
	int frequency;
	
	public DataHolder( char c, int frequency){
		this.c = c;
		this.frequency = frequency;
	}
	
	public DataHolder(int frequency){
		this.frequency = frequency;
	}
	
	public char getC(){
		return c;
	}
	
	public int getFrequency(){
		return frequency;
	}
	
	public String toString() {
		return "(" + c + ", " + frequency + ")";
	}
}
