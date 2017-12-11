import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import utilities.Surly;

public class Main {
	
	public static void main(String[] args) {
		try {
			Scanner input;
			Surly surly = new Surly();
			String file = (args.length > 0 && args[0].equals("-input")) ? "BaseInput.txt" : "Input.txt";
			input = new Scanner(new File(file));
			if (file.equals("BaseInput.txt")) {
				while (!surly.executeQueries(input)) {
					System.out.print("Query: ");
					input = new Scanner(System.in);
				}
			} else {
				surly.executeQueries(input);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}