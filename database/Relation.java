package database;

import java.util.LinkedList;
import utilities.Error;

public class Relation {
	private String name;
	private LinkedList<Tuple> tuples;
	private Schema schema;
	
	public Relation() {}
	
	public Relation(String name) {
		this(name, new Schema());
	}
	
	public Relation(String name, Schema schema) {
		this(name, schema, new LinkedList<Tuple>());
	}
	
	public Relation(String name, Schema schema, LinkedList<Tuple> tuples) {
		this.name = name;
		this.schema = schema;
		this.tuples = tuples;
	}
	
	public static Relation join(String name, Relation a, Relation b, String[] atts) {
		int[] inds = new int[2];
		inds[0] =  a.getSchema().getIndexOf(atts[0]);
		inds[1] =  b.getSchema().getIndexOf(atts[1]);
		
		if (inds[0] == -1) {
			Error.printMissingAttributeError(atts[0], a.getName());
			return null;
		}
		else if(inds[1] == -1) {
			Error.printMissingAttributeError(atts[1], b.getName());
			return null;
		}
		
		LinkedList<Tuple> aTups = a.getTuples();
		LinkedList<Tuple> bTups = b.getTuples();
		LinkedList<Tuple> cTups = new LinkedList<Tuple>();
		for (int i = 0; i < aTups.size(); i++) {
			for (int j = 0; j < bTups.size(); j++) {
				Tuple aTupa = aTups.get(i);
				Tuple bTupa = bTups.get(j);
				String atta = aTupa.getAttributes().get(inds[0]).getValue();
				String attb = bTupa.getAttributes().get(inds[1]).getValue();
				if (atta.equals(attb)) {
					LinkedList<Attribute> attsc = new LinkedList<Attribute>();
					attsc.addAll(aTupa.getAttributes());
					attsc.addAll(bTupa.getAttributes());
					cTups.add(new Tuple(attsc));
					cTups.getLast();
				}
			}
		}
		
		
		LinkedList<Header> cHeaders = new LinkedList<Header>();
		cHeaders.addAll(a.getSchema().getHeaders());
		cHeaders.addAll(b.getSchema().getHeaders());
		
		return new Relation(name, new Schema(cHeaders), cTups);
	}
	
	public Relation project(String name, String[] atts) {
		LinkedList<Header> headers = schema.getHeaders();
		LinkedList<Header> newHeaders = new LinkedList<Header>();
		LinkedList<Integer> attsToKeep = new LinkedList<Integer>();
		
		// get att indexes
		for (int i = 0; i < atts.length; i++) {
			attsToKeep.add(this.getAttInd(atts[i]));
			if (attsToKeep.getLast() == -1) {
				Error.printMissingAttributeError(atts[i], this.name);
				return null;
			}
		}
		
		// get new headers
		for (int i = 0; i < attsToKeep.size(); i++)
			newHeaders.add(headers.get(attsToKeep.get(i)));
		
		// create new tuples
		LinkedList<Tuple> newTuples = new LinkedList<Tuple>();
		for (int j = 0; j < this.tuples.size(); j++) {
			Tuple newTup = new Tuple();
			LinkedList<Attribute> oldAtts = this.tuples.get(j).getAttributes();
			
			for (int i = 0; i < attsToKeep.size(); i++)
				newTup.addAttribute(oldAtts.get(attsToKeep.get(i)));
			
			if(!newTuples.contains(newTup))
				newTuples.add(newTup);
		}
		
		return new Relation(name, new Schema(newHeaders), newTuples);
	}
	
	public int getAttInd(String name) {
		int ind = -1;
		LinkedList<Header> headers = this.schema.getHeaders();
			
		for (int j = 0; j < headers.size(); j++)
			if(headers.get(j).getName().equals(name))
				ind = j;
		
		return ind;
	}
	
	public void print() {
		System.out.println(name);
		schema.print();
		int[] headerWidths = schema.getHeaderWidth();
		for (Tuple t : tuples) {
			t.print(headerWidths);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
	public void setTuples(LinkedList<Tuple> tuples) {
		this.tuples = tuples;
	}
	
	public void addTuple(Tuple tuple) {
		if (tuple != null)
			tuples.add(tuple);
	}
	
	public String getName() {
		return name;
	}
	public Schema getSchema() {
		return schema;
	}
	
	public LinkedList<Tuple> getTuples() {
		return tuples;
	}
	
	public void removeTuples(LinkedList<Tuple> rTuples) {
		if (rTuples != null) {
			for (Tuple t : rTuples) {
				tuples.remove(t);
			}
		}
	}
}
