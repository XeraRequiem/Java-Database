package utilities;

import java.util.LinkedList;
import java.util.Scanner;

import database.Database;
import database.Relation;
import database.Tuple;
import utilities.Error;
import utilities.Parser;

public class Surly {
	Database db;
	
	public Surly() {
		db = new Database();
	}
	
	public boolean executeQueries(Scanner input) {
		while(input.hasNextLine()) {
			String query = input.nextLine();
			String[] args = query.replace(";", "").split(" ", 2);
			String arg = "";
			if (args.length < 2) {
				if (query.toLowerCase().equals("quit")) {
					return true;
				}
			} else {
				arg = args[1];
			}
			
			String command = args[0];
			switch(command) {
				case "RELATION":
					db.addRelation(Parser.createRelation(arg));
					break;
				case "INSERT":
					parseInsert(arg);
					break;
				case "PRINT":
					db.printRelations(Parser.getParsedNames(arg));
					break;
				case "DESTROY":
					db.removeRelations(Parser.getParsedNames(arg));
					break;
				case "DELETE":
					parseDelete(arg);
					break;
				case "IMPORT":
					parseImport(arg);
					break;
				case "EXPORT":
					parseExport(arg);
					break;
				default:
					if (!parseTempCommand(command, arg)) {
						Error.printCommandError(query);
					}
			}
		}
		return false;
	}
	
	private void parseInsert(String arg) {
		String[] args = arg.split(" ", 2);
		Relation result = db.getRelation(args[0]);
		if (result != null) {
			Tuple res = Parser.insertTuple(args[1], result.getSchema());
			result.addTuple(res);
		}
	}
	
	private void parseDelete(String arg) {
		String[] args = arg.split(" ", 2);
		Relation rel = db.getRelation(args[0]);
		
		if (rel != null) {
			if (args.length == 2) {
				rel.removeTuples(Parser.deleteTuples(args[1], rel));
			} else {
				rel.setTuples(new LinkedList<Tuple>());
			}
		}
	}
	
	private void parseImport(String arg) {
		String[] args = arg.split(" ");
		
		if (args.length  == 1) {
			Database newDB = XMLParser.importDatabase(arg);
			
			for (Relation relation : newDB.getRelations()) {
				db.addRelation(relation);
			}
		} else {
			Error.printSyntaxError(arg);
		}
	}
	
	private void parseExport(String arg) {
		String[] args = arg.split(" ");
		
		if (args.length == 1) {
			XMLParser.exportDatabase(db, arg);
		} else {
			Error.printSyntaxError(arg);
		}
	}
	
	private boolean parseTempCommand(String name, String arg) {
		String[] args = arg.split(" ", 4);
		Relation result;
		if (args[0].equals("=")) {
			try {
				switch (args[1]) {
					case "PROJECT":
						LinkedList<String []> projConditions = Parser.project(args[2] + " " + args[3]);
						result = db.getRelation(projConditions.get(0)[0]);
						result = (result != null) ? result.project(name, projConditions.get(1)) : null;
						break;
					case "JOIN":
						LinkedList<String []> joinConditions = Parser.join(args[2] + " " + args[3]);
						result = Relation.join(name, db.getRelation(joinConditions.get(0)[0]), db.getRelation(joinConditions.get(0)[1]), joinConditions.get(1));
						break;
					case "SELECT":
						String clause = (args.length == 4) ? args[3] : "";
						result = Parser.selectTuples(name, clause, db.getRelation(args[2]));
						break;
					default:
						return false;
				}
				
				if (result != null)
					db.addTempRelation(result);
				
			} catch (ArrayIndexOutOfBoundsException e) {
				Error.printSyntaxError(arg);
			}
		} else {
			Error.printSyntaxError(name + " " + arg);
		}		
		return true;
	}
}
