/**
 * CS 411: Compilers and Interpreters
 * Professor: Daisy Sang
 *
 * Programming Assignment #1
 *
 * To run this project, open up Eclipse, and click file -> import.
 * From the import page, open up the General folder, and select 
 * "Existing Projects into Workspace", and click next.  From this 
 * page, select browse next to the "Select root directory: " option, 
 * and select the "LexicalAnalyzer" folder, to import 
 * the whole project. On the left side is the Package Explorer, open 
 * up the LexicalAnalyzer folder, then the src folder, 
 * then the edu.csupomona.cs.cs411.Project1 package.  From there open 
 * up the Project1.java file. To compile the code and run it, from 
 * the Project1 file, click the Run button in the top list of options, 
 * and click the run option, which should have a green circle with a 
 * white arrow icon next to it. The output will be saved to a text 
 * file named output1.dat for the test.toy file and output2.dat for 
 * the test2.toy file in the LexicalAnalyzer folder.
 *
 * David Scianni
 */
package edu.csupomona.cs.cs411.Project1;

import java.io.IOException;

/**
 * Project 1 will take in two test files, one written by Dr. Sang, and one
 * written by me in order to test the lexical analyzer, and make sure that it
 * correctly sends the tokens to both the screen and the output file.
 * 
 * @author David Scianni
 * 
 */
public class Project1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * a lexical analyzer is created twice, once for each test file. This
		 * way both test files have a corresponding output file: output1 and
		 * output2. Both lex and print are called for each in order to display
		 * both the token list and the symbol table for both test files.
		 */
		try {
			LexicalAnalyzer la = new LexicalAnalyzer("test.toy", "output1.dat");
			la.lex();
			la.print();

			System.out.println("\n");

			la = new LexicalAnalyzer("test2.toy", "output2.dat");
			la.lex();
			la.print();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
