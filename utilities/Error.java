package utilities;

public class Error {
	
	public static void printSyntaxError(String syntax) {
		System.out.println("Error: '" + syntax + "' is not valid syntax.");
		System.out.println();
	}
	
	public static void printExistenceError(String relation, boolean exists) {
		String existence = (exists) ? "already exists." : "does not exist";
		System.out.println("Error: '" + relation + "' " + existence);
		System.out.println();
	}
	
	public static void printMissingAttributeError(String attribute, String relation) {
		System.out.println("Error: '" + attribute + "' is not a valid attribute in " + relation + ".");
		System.out.println();
	}
	
	public static void printConditionError(String condition) {
		System.out.println("Error: '" + condition + "' is not a valid condition.");
		System.out.println();
	}
	
	public static void printCommandError(String command) {
		System.out.println("Error: '" + command + "' does not contain a valid command.");
		System.out.println();
	}
	
	public static void printMismatchError(String value, String type) {
		System.out.println("Error: '" + value + "' is not of type '" + type + "'.");
		System.out.println();
	}
	
	public static void printSchemaError(String value) {
		System.out.println("Error: '" + value + "' is too large.");
		System.out.println();
	}
}
