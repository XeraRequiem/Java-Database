package database;

public class Attribute {

	private String type;
	private String value;
	
	public Attribute(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public void print(int width) {
		System.out.print(value);
		for (int i = value.length(); i <= width; i++) {
			System.out.print(" ");
		}
		System.out.print("|");

	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
}