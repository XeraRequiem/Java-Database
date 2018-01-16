package utilities;

import java.util.LinkedList;

import database.Attribute;
import database.Header;
import database.Relation;
import database.Schema;
import database.Tuple;

public class Parser {

	//Create a relation and add it to the database with parsed schema
	public static Relation createRelation(String arg) {
		String[] args = arg.split(" ", 2);
		String name = args[0];
		try {
			Schema schema = parseSchema(args[1]);
			if (schema == null) {
				Error.printSyntaxError(arg);
				return null;
			}
			return new Relation(name, schema);
		} catch (IndexOutOfBoundsException e) {
			Error.printSyntaxError(arg);
			return null;
		}
	}
	
	public static LinkedList<String> getParsedNames(String args) {
		String[] names = args.split(", ");
		LinkedList<String> rNames = new LinkedList<String>();
		for (String name : names) {
			rNames.add(name);
		}
		return rNames;
	}
	
	//Create a tuple that adheres to the schema of given relationship and add it to the relation
	public static Tuple insertTuple(String args, Schema schema) {
		int headerCount = schema.getHeaderSize();
		Tuple tuple = new Tuple();

		String[] segments;
		
		for (int i = 0; i < headerCount; i++) {
			// check that the next arg is not a string with a space
			if (args.indexOf('\'') < args.indexOf(' ') && args.indexOf('\'') != -1) {
				 segments = new String[] { args.substring(1, args.indexOf("'", 1)),
						 				   args.substring(args.indexOf("'", 1) + 1) };

				 args = segments[1];
			} // if this is the last arg, trim instead of split
			else if (i == headerCount - 1) {
				segments = new String[] {args.trim()};
			} // grab the next argument
			else {
				segments = args.split(" ", 2);
				args = segments[1];
			}
			
			// input is too large
			if (segments[0].length() > schema.getHeaders().get(i).getMax()) {
				Error.printSchemaError(segments[0]);
				return null;
			}
			
			tuple.addAttribute(new Attribute(schema.getHeaders().get(i).getType(), segments[0]));
		}
		return tuple;
		
	}

	//Remove tuples that satisfy given conditions from relation
	public static LinkedList<Tuple> deleteTuples(String arg, Relation relation) {
		String[] args = arg.split(" ", 2);
		LinkedList<Tuple> tuplesToRemove = null;
		try {
			if (args[0].equals("WHERE")) {
				tuplesToRemove = getTuplesWhere(relation.getSchema(), relation.getTuples(), args[1]);
			} else
				Error.printSyntaxError(args[0] + " " + args[1]);
		} catch (Exception e) {
			tuplesToRemove = null;
		}
		return tuplesToRemove;
	}
	
	//Remove the tuples from list that don't satisfy conditions
	public static LinkedList<Tuple> getTuplesWhere(Schema schema, LinkedList<Tuple> tuples, String conds) {
		String[] unions = conds.split(" OR ");
		
		LinkedList<LinkedList<Tuple>> unionList = new LinkedList<LinkedList<Tuple>>();
		for (String union : unions) {
			LinkedList<Tuple> result = getIntersectOfTuples(tuples, schema, union.split(" AND "));
			if (result == null) {
				return null;
			}
			unionList.add(result);				
		}
		LinkedList<Tuple> result;
		try {
			result = unionList.get(0);
			for (LinkedList<Tuple> list : unionList) {
				for (Tuple t : list) {
					if (!result.contains(t))
						result.add(t);
				}
			}
		} catch (NullPointerException e) {
			Error.printSyntaxError(conds);
			result = null;
		}
		return result;
	}
	
	//Performs AND operation on given conditions
	private static LinkedList<Tuple> getIntersectOfTuples(LinkedList<Tuple> tuples, Schema schema, String[] conds) {
		for (String cond : conds) {
			if (tuples == null) 
				return null;
			tuples = tuplesGivenCondition(tuples, schema, cond);
		}
		return tuples;
	}
	
	
	//Return the tuples from list that satisfy given condition
	private static LinkedList<Tuple> tuplesGivenCondition(LinkedList<Tuple> tuples, Schema schema, String cond) {
		try {
			String[] args = cond.split(" ", 3);
			
			String att = args[0];
			String op = args[1];
			String val = args[2].replaceAll("'", "");
			
			int index = schema.getIndexOf(att);
			String type = schema.getHeaders().get(index).getType();
			
			if (type.equals("CHAR") && val.matches("^[0-9]*") || type.equals("NUM") && !val.matches("[0-9]*")) {
				Error.printMismatchError(val, type);
				return null;
			}
			LinkedList<Tuple> temp = new LinkedList<Tuple>();
			
			for (Tuple t : tuples) {
				int cmp = compare(op, t.getAttributes().get(index).getValue(), val);
				if (cmp == 1)
					temp.add(t);
				else if (cmp == -1) {
					throw new IndexOutOfBoundsException();
				}
			}
			return temp;
		} catch (IndexOutOfBoundsException e) {
			Error.printConditionError(cond);
			return null;
		}
	}
	
	//1 = true, 0 = false, -1 = error
	private static int compare(String op, String aVal, String cVal) {
		int cmp = aVal.compareTo(cVal);
		switch (op) {
		case "=":
			if (cmp == 0) 
				return 1;
			break;
		case "<":
			if (cmp < 0) 
				return 1;
			break;
		case "<=":
			if (cmp <= 0) 
				return 1;
			break;
		case ">":
			if (cmp > 0)
				return 1;
			break;
		case ">=":
			if (cmp >= 0) 
				return 1;
			break;
		case "!=":
			if (cmp != 0)
				return 1;
			break;
		default:
			return -1;
		}
		return 0;
	}
		
	//Create and return parsed schema 
	private static Schema parseSchema(String args) {
		LinkedList<Header> headers = new LinkedList<Header>();
		String[] atts = args.replaceAll("[()]", "").split(", ");
		for (String s : atts) {
			String[] parts = s.split(" ");
			if (parts.length != 3) {
				return null;
			}
			Header att = new Header(parts[0], parts[1], Integer.parseInt(parts[2]));
			headers.add(att);
		}
		return new Schema(headers);
	}
	
	public static Relation selectTuples(String name, String arg, Relation rel) {
		if (rel == null){
			Error.printExistenceError(name, false);
			return null;
		}
		
		String[] args = arg.split(" ", 2);
		if (args.length == 1) {
			return new Relation(name, rel.getSchema(), rel.getTuples());
		}
		else if (!args[0].equals("WHERE") || args.length != 2) {
			Error.printSyntaxError(arg);
		} else {	
			LinkedList<Tuple> tups = Parser.getTuplesWhere(rel.getSchema(), rel.getTuples(), args[1]);
			if (tups != null)
				return new Relation(name, rel.getSchema(), tups);
		}
		return null;
	}
	
	public static LinkedList<String []> project(String arg) {
		String[] args = arg.split(" FROM ");
		String[] atts = args[0].trim().split(", ");
		String[] relationName = new String[1];
		relationName[0] = args[1].trim();
		
		LinkedList<String []> joinConditions = new LinkedList<String []>();
		joinConditions.add(relationName);
		joinConditions.add(atts);
		
		return joinConditions;
	}
	
	public static LinkedList<String []> join(String arg) {
		String[] args = arg.split(" ON ");
		String[] relationNames = args[0].trim().split(", ");
		String[] onAtts = args[1].trim().split(" = ");
		
		LinkedList<String []> joinConditions = new LinkedList<String []>();
		joinConditions.add(relationNames);
		joinConditions.add(onAtts);
		
		return joinConditions;
	}
}
