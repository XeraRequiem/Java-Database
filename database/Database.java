package database;

import java.util.LinkedList;
import utilities.Error;

public class Database {
	private LinkedList<Relation> relations;
	private LinkedList<Relation> tempRelations;
	
	public Database() {
		relations = new LinkedList<Relation>();
		tempRelations = new LinkedList<Relation>();
	}
	
	public void printRelations(LinkedList<String> names) {
		for (Relation r : relations) {
			if (names.contains(r.getName())) {
				names.remove(r.getName());
				r.print();
				System.out.println();
			}
		}
		
		for (Relation r : tempRelations) {
			if (names.contains(r.getName())) {
				names.remove(r.getName());
				r.print();
				System.out.println();
			}
		}
	
		while (!names.isEmpty()) {
			Error.printExistenceError(names.getFirst(), false);
			names.removeFirst();
		}
	}
	
	public boolean contains(String name) {
		for (Relation r : relations) {
			if (r.getName().equals(name))
				return true;
		}
		return false;
	}
	
	public boolean containsTemp(String name) {
		for (Relation r : tempRelations) {
			if (r.getName().equals(name))
				return true;
		}
		return false;
	}
	
	public Schema getSchema(String name) {
		for (Relation r : relations) {
			if (r.getName().equals(name))
				return r.getSchema();
		}
		
		for (Relation r : tempRelations) {
			if (r.getName().equals(name))
				return r.getSchema();
		}
		return null;
	}
	
	public void insertTuple(String name, Tuple tuple) {
		for (Relation r : relations) {
			if (r.getName().equals(name)) {
				r.addTuple(tuple);
				return;
			}
		}
	}
	
	public void addRelation(Relation relation) {
		if (relation != null) {
			if (!contains(relation.getName()))
				relations.add(relation);
			else
				Error.printExistenceError(relation.getName(), true);
		}
	}
	
	public void addTempRelation(Relation relation) {
		if (relation != null)
			removeTempRelation(relation.getName());
			tempRelations.add(relation);
	}
	
	public Relation getRelation(String name) {
		for (Relation r : relations) {
			if (r.getName().equals(name))
				return r;
		}
		
		for (Relation r : tempRelations) {
			if (r.getName().equals(name))
				return r;
		}
		
		Error.printExistenceError(name, false);
		return null;
	}	
	
	public LinkedList<Relation> getRelations() {
		return relations;
	}
	
	public LinkedList<Relation> getTempRelations() {
		return tempRelations;
	}
	
	public void removeRelation(String name) {
		for (Relation r : relations) {
			if (r.getName().equals(name)) {
				relations.remove(r);
				return;
			}
		}
		Error.printExistenceError(name, false);;
	}
	
	public void removeTempRelation(String name) {
		for (Relation r : tempRelations) {
			if (r.getName().equals(name)) {
				tempRelations.remove(r);
				return;
			}
		}
	}
	
	public void removeRelations(LinkedList<String> names) {
		for (int i = 0; i < relations.size(); i++) {
			Relation current = relations.get(i);
			if (names.contains(current.getName())) {
				relations.remove(current);
				i--;
			}
		}
	}
}
