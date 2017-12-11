package database;

import java.util.LinkedList;

public class Tuple {
	private LinkedList<Attribute> atts;
	
	public Tuple() {
		atts = new LinkedList<Attribute>();
	}
	
	public Tuple(LinkedList<Attribute> atts) {
		this.atts = atts;
	}
	
	public void print(int[] widths) {
		for (int i = 0; i < atts.size(); i++) {
			atts.get(i).print(widths[i]);
		}
		System.out.println();
	}
	
	public void addAttribute(Attribute att) {
		atts.add(att);
	}
	
	public void setAttributes(LinkedList<Attribute> atts) {
		this.atts = atts;
	}
	
	public LinkedList<Attribute> getAttributes() {
		return atts;
	}
}