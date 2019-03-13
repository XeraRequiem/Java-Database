package database;

public class Header {
	private String name;
	private String type;
	private int max;
	private int width;
	
	public Header(String name, String type, int max) {
		this.name = name;
		this.type = type;
		this.max = max;
		width = Math.max(name.length(), max);
	}
	
	public void print() {
		System.out.print(name);
		for (int i = name.length(); i <= width; i++) {
			System.out.print(" ");
		}
		System.out.print("|");
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getWidth() {
		return width;
	}
}