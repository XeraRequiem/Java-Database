package database;

import java.util.LinkedList;

public class Schema {
	private LinkedList<Header> headers; 
	
	public Schema() {
		headers = new LinkedList<Header>();
	}
	
	public Schema(LinkedList<Header> headers) {
		this.headers = headers;
	}
	
	public void print() {
		String divider = "";
		for (Header header: headers) {
			header.print();
			for (int i = 0; i <= header.getWidth()+1; i++) {
				divider += "-";
			}
		}
		System.out.println();
		System.out.println(divider);
	}
	
	public int getIndexOf(String name) {
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getHeaderSize() {
		return headers.size();
	}
	
	public int[] getHeaderWidth() {
		int[] maxes = new int[headers.size()];
		for (int i = 0; i < headers.size(); i++) {
			maxes[i] = headers.get(i).getWidth();
		}
		return maxes;
	}
	
	public LinkedList<Header> getHeaders() {
		return headers;
	}
}
