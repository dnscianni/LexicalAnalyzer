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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * LexicalAnalyzer represents the first stage in a compiler for the new
 * programming language Toy. It will take in a file of type toy, and will
 * analyze its content for items such as keywords, int constants, etc. and will
 * send out a numerical token for each one found in the file. It will also
 * create a symbol table in a trie data structure that will hold all the
 * keywords and identifiers created by the user.
 * 
 * @author David Scianni
 * 
 */
public class LexicalAnalyzer {

	/**
	 * These final integers are the numbers of each of the tokens contained in
	 * the toy language.
	 */
	private final int BOOL = 1001;
	private final int BREAK = 1002;
	private final int CLASS = 1003;
	private final int DOUBLE = 1004;
	private final int ELSE = 1005;
	private final int EXTENDS = 1006;
	private final int FOR = 1007;
	private final int IF = 1008;
	private final int IMPLEMENTS = 1009;
	private final int INT = 1010;
	private final int INTERFACE = 1011;
	private final int NEWARRAY = 1012;
	private final int PRINTLN = 1013;
	private final int READLN = 1014;
	private final int RETURN = 1015;
	private final int STRING = 1016;
	private final int VOID = 1017;
	private final int WHILE = 1018;
	private final int PLUS = 1019;
	private final int MINUS = 1020;
	private final int MULTIPLICATION = 1021;
	private final int DIVISION = 1022;
	private final int LESS = 1023;
	private final int LESSEQUAL = 1024;
	private final int GREATER = 1025;
	private final int GREATEREQUAL = 1026;
	private final int EQUAL = 1027;
	private final int NOTEQUAL = 1028;
	private final int AND = 1029;
	private final int OR = 1030;
	private final int NOT = 1031;
	private final int ASSIGNOP = 1032;
	private final int SEMICOLON = 1033;
	private final int COMMA = 1034;
	private final int PERIOD = 1035;
	private final int LEFTPAREN = 1036;
	private final int RIGHTPAREN = 1037;
	private final int LEFTBRACKET = 1038;
	private final int RIGHTBRACKET = 1039;
	private final int LEFTBRACE = 1040;
	private final int RIGHTBRACE = 1041;
	private final int BOOLCONSTANT = 1042;
	private final int INTCONSTANT = 1043;
	private final int DOUBLECONSTANT = 1044;
	private final int STRINGCONSTANT = 1045;
	private final int ID = 1046;

	/**
	 * file is used to import the file that is to be analyzed. This file will be
	 * a file written in the Toy language.
	 */
	private static FileReader file;

	/**
	 * In order to read the contents of the file, the BufferedReader inputStream
	 * will be used.
	 */
	private static BufferedReader inputStream;

	/**
	 * This is to hold the file that contains the keywords for the language Toy.
	 */
	private static File keywords;

	/**
	 * As a result of the program, the tokens and the symbol table will be saved
	 * to an output file held in the outfile PrintWriter.
	 */
	private static PrintWriter outfile;

	/**
	 * firstSymbol is an array of type int, which will hold the pointer to the
	 * index in symbol and next. each index of firstSymbol represents a
	 * character, with 0-25 being a-z, 26-51 being A-Z. For example if
	 * firstSymbol[0] = 12, then the first a word is located in index 12 of
	 * symbol.
	 */
	private int[] firstSymbol;

	/**
	 * symbol is an array of type char that will hold all the letters and end
	 * symbols of the words in the symbol table, except for the first letter.
	 * The first letter does not need to be added because of the firstSymbol
	 * array, which holds the reference to the first letter.
	 */
	private char[] symbol;

	/**
	 * next is an array of type int that holds the pointer to the next word in
	 * the array symbol.
	 */
	private int[] next;

	/**
	 * flag is an int that will hold the pointer to the next available position
	 * in the symbol array.
	 */
	private int flag;

	/**
	 * This constructor first assigns files to the four input and output files.
	 * It then sets uo the three arrays in the trie structure. Lastly, it fills
	 * the trie symbol table with all of the keywords in the Toy language.
	 * 
	 * @param f
	 *            The name of the file to be read.
	 * @param of
	 *            the name of the file to be written into.
	 * @throws FileNotFoundException
	 */
	public LexicalAnalyzer(String f, String of) throws FileNotFoundException {
		file = new FileReader(f);
		inputStream = new BufferedReader(file);
		keywords = new File("toy.dat");

		outfile = new PrintWriter(of);

		firstSymbol = new int[52];
		for (int i = 0; i < firstSymbol.length; i++) {
			firstSymbol[i] = -1;
		}
		symbol = new char[400];
		next = new int[400];
		for (int i = 0; i < next.length; i++) {
			next[i] = -1;
		}
		flag = 0;

		Scanner iFile = new Scanner(keywords);
		String s;
		char[] w, word;
		int nextSymbol, fSym, ptr;
		while (iFile.hasNext()) {
			s = iFile.next();
			w = s.toCharArray();
			word = new char[w.length + 1];
			for (int i = 0; i < w.length; i++) {
				word[i] = w[i];
			}
			if (s.compareTo("true") == 0 || s.compareTo("false") == 0) {
				word[w.length] = '$';
			} else {
				word[w.length] = '*';
			}
			nextSymbol = 0;
			fSym = getCharValue(word[nextSymbol++]);
			ptr = firstSymbol[fSym];
			if (ptr < 0) {
				create(word, fSym, ptr, nextSymbol);
			} else {
				while (true) {
					if (symbol[ptr] == word[nextSymbol]) {
						if (word[nextSymbol] != '*') {
							ptr++;
							nextSymbol++;
						} else {
							break;
						}
					} else if (next[ptr] >= 0) {
						ptr = next[ptr];
					} else {
						create(word, fSym, ptr, nextSymbol);
						break;
					}
				}
			}
		}
		iFile.close();

	}

	/**
	 * lex is a method that will take in each element of a file and return both
	 * an output file and a result written on the screen displaying all the
	 * tokens found in the file. It does this by reading each character in the
	 * file, and analyzing it. It begins by checking to see if it is a non
	 * numeric or alphabetical symbol, and if it is, it responds by either
	 * sending a token when appropriate, or by not sending one and skipping to
	 * the next character if need be, such as if the character is a white space.
	 * If it is not one of these, it will then check to see if it is either a
	 * double constant or an integer constant, and will return an appropriate
	 * token. Lastly, it checks to see if it is an alphabetical character, and
	 * then both returns a corresponding token based on its type, and it will
	 * also add the word to the symbol table, if it does not already exist
	 * within the table.
	 * 
	 * @throws IOException
	 */
	public void lex() throws IOException {
		int i;
		String word;
		while ((i = inputStream.read()) != -1) {
			word = "";
			switch ((char) i) {
			case '+':
				printToken(PLUS);
				break;
			case '-':
				printToken(MINUS);
				break;
			case '*':
				printToken(MULTIPLICATION);
				break;
			case '/':
				inputStream.mark(100);
				i = inputStream.read();
				if (i == '/') {
					while ((i = inputStream.read()) != 10 && i != -1) {
					}
					System.out.println();
					outfile.println();
				} else if (i == '*') {
					while (true) {
						while ((char) (i = inputStream.read()) != '*'
								&& i != -1) {
						}
						if ((char) (i = inputStream.read()) == '/') {
							break;
						}
					}
				} else {
					inputStream.reset();
					printToken(DIVISION);
				}
				break;
			case '<':
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == '=') {
					printToken(LESSEQUAL);
				} else {
					inputStream.reset();
					printToken(LESS);
				}
				break;
			case '>':
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == '=') {
					printToken(GREATEREQUAL);
				} else {
					inputStream.reset();
					printToken(GREATER);
				}
				break;
			case '=':
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == '=') {
					printToken(EQUAL);
				} else {
					inputStream.reset();
					printToken(ASSIGNOP);
				}
				break;
			case '!':
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == '=') {
					printToken(NOTEQUAL);
				} else {
					inputStream.reset();
					printToken(NOT);
				}
				break;
			case '&':
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == '&') {
					printToken(AND);
				} else {
					inputStream.reset();
				}
				break;
			case '|':
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == '|') {
					printToken(OR);
				} else {
					inputStream.reset();
				}
				break;
			case ';':
				printToken(SEMICOLON);
				break;
			case ',':
				printToken(COMMA);
				break;
			case '.':
				printToken(PERIOD);
				break;
			case '(':
				printToken(LEFTPAREN);
				break;
			case ')':
				printToken(RIGHTPAREN);
				break;
			case '[':
				printToken(LEFTBRACKET);
				break;
			case ']':
				printToken(RIGHTBRACKET);
				break;
			case '{':
				printToken(LEFTBRACE);
				break;
			case '}':
				printToken(RIGHTBRACE);
				break;
			case '\"':
				while ((i = inputStream.read()) != 10 && (char) i != '\"'
						&& i != -1) {
				}
				if (i == 10) {
					System.out.println();
					outfile.println();
				} else if ((char) i == '\"') {
					printToken(STRINGCONSTANT);
				}
				break;
			case '\n':
				System.out.println();
				outfile.println();
				break;
			default:
				break;
			}
			if ((char) i == '0') {
				inputStream.mark(5);
				if ((char) (i = inputStream.read()) == 'x' || i == 'X') {
					if (Character.isDigit(i = inputStream.read())
							|| ((char) i >= 'a' && (char) i <= 'f')
							|| ((char) i >= 'A' && (char) i <= 'F')) {
						inputStream.mark(2);
						while ((Character.isDigit(i = inputStream.read())
								|| ((char) i >= 'a' && (char) i <= 'f') || ((char) i >= 'A' && (char) i <= 'F'))
								&& i != -1) {
							inputStream.mark(2);
						}
						inputStream.reset();
						printToken(INTCONSTANT);
					} else {
						i = '0';
						inputStream.reset();
					}
				} else {
					i = '0';
					inputStream.reset();
				}
			}
			if (Character.isDigit(i)) {
				inputStream.mark(5);
				while (Character.isDigit(i = inputStream.read()) && i != -1) {
					inputStream.mark(2);
				}
				if ((char) i == '.') {
					inputStream.mark(5);
					while (Character.isDigit(i = inputStream.read()) && i != -1) {
						inputStream.mark(2);
					}
					if ((char) i == 'E' || (char) i == 'e') {
						inputStream.mark(2);
						if ((i = inputStream.read()) != '+' && i != '-'
								&& !Character.isDigit(i)) {
							inputStream.reset();
						} else {
							inputStream.mark(5);
							while (Character.isDigit(i = inputStream.read())
									&& i != -1) {
								inputStream.mark(2);
							}
							inputStream.reset();
							printToken(DOUBLECONSTANT);
						}
					} else {
						inputStream.reset();
						printToken(DOUBLECONSTANT);
					}
				} else {
					inputStream.reset();
					printToken(INTCONSTANT);
				}
			} else if (Character.isLetter(i)) {
				inputStream.mark(5);
				word += (char) i;
				while ((Character.isLetterOrDigit(i = inputStream.read()) || i == '_')
						&& i != -1) {
					inputStream.mark(2);
					word += (char) i;
				}
				inputStream.reset();
				if (check(word)) {
					keywordCheck(word);
				} else {
					printToken(ID);
				}
			}
		}
		inputStream.close();
	}

	/**
	 * The keywordCheck method will take in a word that is either a keyword or a
	 * boolean constant, and will call the corresponding printToken method to
	 * print out the correct token.
	 * 
	 * @param word
	 *            the word to be tested.
	 */
	private void keywordCheck(String word) {
		if (word.compareTo("bool") == 0) {
			printToken(BOOL);
		} else if (word.compareTo("break") == 0) {
			printToken(BREAK);
		} else if (word.compareTo("class") == 0) {
			printToken(CLASS);
		} else if (word.compareTo("double") == 0) {
			printToken(DOUBLE);
		} else if (word.compareTo("else") == 0) {
			printToken(ELSE);
		} else if (word.compareTo("extends") == 0) {
			printToken(EXTENDS);
		} else if (word.compareTo("for") == 0) {
			printToken(FOR);
		} else if (word.compareTo("if") == 0) {
			printToken(IF);
		} else if (word.compareTo("implements") == 0) {
			printToken(IMPLEMENTS);
		} else if (word.compareTo("int") == 0) {
			printToken(INT);
		} else if (word.compareTo("interface") == 0) {
			printToken(INTERFACE);
		} else if (word.compareTo("newarray") == 0) {
			printToken(NEWARRAY);
		} else if (word.compareTo("println") == 0) {
			printToken(PRINTLN);
		} else if (word.compareTo("readln") == 0) {
			printToken(READLN);
		} else if (word.compareTo("return") == 0) {
			printToken(RETURN);
		} else if (word.compareTo("string") == 0) {
			printToken(STRING);
		} else if (word.compareTo("void") == 0) {
			printToken(VOID);
		} else if (word.compareTo("while") == 0) {
			printToken(WHILE);
		} else if (word.compareTo("true") == 0 || word.compareTo("false") == 0) {
			printToken(BOOLCONSTANT);
		}
	}

	/**
	 * The printToken method will print out the appropraite token to both the
	 * screen and an output file.
	 * 
	 * @param token
	 *            the token to be printed.
	 */
	private void printToken(int token) {
		switch (token) {
		case BOOL:
			System.out.print("_bool ");
			outfile.print("_bool ");
			break;
		case BREAK:
			System.out.print("_break ");
			outfile.print("_break ");
			break;
		case CLASS:
			System.out.print("_class ");
			outfile.print("_class ");
			break;
		case DOUBLE:
			System.out.print("_double ");
			outfile.print("_double ");
			break;
		case ELSE:
			System.out.print("_else ");
			outfile.print("_else ");
			break;
		case EXTENDS:
			System.out.print("_extends ");
			outfile.print("_extends ");
			break;
		case FOR:
			System.out.print("_for ");
			outfile.print("_for ");
			break;
		case IF:
			System.out.print("_if ");
			outfile.print("_if ");
			break;
		case IMPLEMENTS:
			System.out.print("_implements ");
			outfile.print("_implements ");
			break;
		case INT:
			System.out.print("_int ");
			outfile.print("_int ");
			break;
		case INTERFACE:
			System.out.print("_interface ");
			outfile.print("_interface ");
			break;
		case NEWARRAY:
			System.out.print("_newarray ");
			outfile.print("_newarray ");
			break;
		case PRINTLN:
			System.out.print("_println ");
			outfile.print("_println ");
			break;
		case READLN:
			System.out.print("_readln ");
			outfile.print("_readln ");
			break;
		case RETURN:
			System.out.print("_return ");
			outfile.print("_return ");
			break;
		case STRING:
			System.out.print("_string ");
			outfile.print("_string ");
			break;
		case VOID:
			System.out.print("_void ");
			outfile.print("_void ");
			break;
		case WHILE:
			System.out.print("_while ");
			outfile.print("_while ");
			break;
		case PLUS:
			System.out.print("_plus ");
			outfile.print("_plus ");
			break;
		case MINUS:
			System.out.print("_minus ");
			outfile.print("_minus ");
			break;
		case MULTIPLICATION:
			System.out.print("_multiplication ");
			outfile.print("_multiplication ");
			break;
		case DIVISION:
			System.out.print("_division ");
			outfile.print("_division ");
			break;
		case LESS:
			System.out.print("_less ");
			outfile.print("_less ");
			break;
		case LESSEQUAL:
			System.out.print("_lessequal ");
			outfile.print("_lessequal ");
			break;
		case GREATER:
			System.out.print("_greater ");
			outfile.print("_greater ");
			break;
		case GREATEREQUAL:
			System.out.print("_greaterequal ");
			outfile.print("_greaterequal ");
			break;
		case EQUAL:
			System.out.print("_equal ");
			outfile.print("_equal ");
			break;
		case NOTEQUAL:
			System.out.print("_notequal ");
			outfile.print("_notequal ");
			break;
		case AND:
			System.out.print("_and ");
			outfile.print("_and ");
			break;
		case OR:
			System.out.print("_or ");
			outfile.print("_or ");
			break;
		case NOT:
			System.out.print("_not ");
			outfile.print("_not ");
			break;
		case ASSIGNOP:
			System.out.print("_assignop ");
			outfile.print("_assignop ");
			break;
		case SEMICOLON:
			System.out.print("_semicolon ");
			outfile.print("_semicolon ");
			break;
		case COMMA:
			System.out.print("_comma ");
			outfile.print("_comma ");
			break;
		case PERIOD:
			System.out.print("_period ");
			outfile.print("_period ");
			break;
		case LEFTPAREN:
			System.out.print("_leftparen ");
			outfile.print("_leftparen ");
			break;
		case RIGHTPAREN:
			System.out.print("_rightparen ");
			outfile.print("_rightparen ");
			break;
		case LEFTBRACKET:
			System.out.print("_leftbracket ");
			outfile.print("_leftbracket ");
			break;
		case RIGHTBRACKET:
			System.out.print("_rightbracket ");
			outfile.print("_rightbracket ");
			break;
		case LEFTBRACE:
			System.out.print("_leftbrace ");
			outfile.print("_leftbrace ");
			break;
		case RIGHTBRACE:
			System.out.print("_rightbrace ");
			outfile.print("_rightbrace ");
			break;
		case BOOLCONSTANT:
			System.out.print("_boolconstant ");
			outfile.print("_boolconstant ");
			break;
		case INTCONSTANT:
			System.out.print("_intconstant ");
			outfile.print("_intconstant ");
			break;
		case DOUBLECONSTANT:
			System.out.print("_doubleconstant ");
			outfile.print("_doubleconstant ");
			break;
		case STRINGCONSTANT:
			System.out.print("_stringconstant ");
			outfile.print("_stringconstant ");
			break;
		case ID:
			System.out.print("_id ");
			outfile.print("_id ");
			break;
		default:
			break;
		}
	}

	/**
	 * check will do the same thing as initialCheck, except that it will return
	 * the word with the correct end symbol, which will be either *, ?, or @.
	 * 
	 * @param s
	 *            the word being checked
	 * @return the word followed by either *,?, or @.
	 */
	/**
	 * check will read in a word and search for it in the symbol table. If it is
	 * found it will not add it. If it is an acceptable word, and is not in the
	 * symbol table it will add it. It will also return true if the word is
	 * either a keyword or a boolean constant, and false if it is an id.
	 * 
	 * @param s
	 *            the word being checked
	 * @return true if keyword or constant, false if its an id
	 */
	private boolean check(String s) {
		char[] w = s.toCharArray();
		char[] word = new char[w.length + 1];
		for (int i = 0; i < w.length; i++) {
			word[i] = w[i];
		}
		word[w.length] = '@';
		int nextSymbol = 0;
		int fSym = getCharValue(word[nextSymbol++]);
		int ptr = firstSymbol[fSym];
		if (ptr < 0) {
			create(word, fSym, ptr, nextSymbol);
			return false;
		} else {
			while (true) {
				if (symbol[ptr] == word[nextSymbol]
						|| ((symbol[ptr] == '*' || symbol[ptr] == '$') && word[nextSymbol] == '@')) {
					if (word[nextSymbol] != '@') {
						ptr++;
						nextSymbol++;
					} else {
						if (symbol[ptr] == '*' || symbol[ptr] == '$') {
							return true;
						} else {
							return false;
						}
					}
				} else if (next[ptr] >= 0) {
					ptr = next[ptr];
				} else {
					create(word, fSym, ptr, nextSymbol);
					return false;
				}
			}
		}
	}

	/**
	 * Create will add the word to the symbol table in one of two ways. It will
	 * either add a word that starts with a new letter, or it will add it if
	 * there is already a word with the same beginning letter. If the word is
	 * the first word with the beginning letter, then the corresponding index of
	 * the firstSymbol array will be changed to the flag, and then the word will
	 * be placed in letter by letter in the symbol array. Else, it will put the
	 * flag in the correct index of next, and then add all the letters of the
	 * word in symbol.
	 * 
	 * @param word
	 *            the word being added
	 * @param fSym
	 *            the index of firstSymbol that corresponds to the first letter
	 *            of word.
	 * @param ptr
	 *            the position in next that needs to be replaced with the flag
	 * @param nextSymbol
	 *            the index in word that was left off at while searching.
	 */
	private void create(char[] word, int fSym, int ptr, int nextSymbol) {

		if (firstSymbol[fSym] < 0) {
			firstSymbol[fSym] = flag;
			for (int i = 1; i < word.length; i++) {
				symbol[flag++] = word[i];
			}
		} else {
			next[ptr] = flag;
			for (int i = nextSymbol; i < word.length; i++) {
				symbol[flag++] = word[i];
			}
		}
	}

	/**
	 * Checks the number value of the char and sends the corresponding number
	 * that the char represents in the firstSymbol array.
	 * 
	 * @param c
	 *            the char being checked
	 * @return The number the character represents
	 */
	private int getCharValue(char c) {
		if (c >= 97 && c <= 122) {
			return c - 97;
		} else {
			return c - 39;
		}
	}

	/**
	 * Print will print out the entire symbol table by first printing the
	 * contents of the switch array, then by printing out the symbol and next
	 * arrays next to each other in an easy to read format.
	 */
	public void print() {
		System.out.println("\n\n");
		outfile.println("\n\n");
		System.out
				.printf("%13c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%n",
						'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
						'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't');
		outfile.printf(
				"%13c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%n",
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't');
		System.out.print("switch:");
		outfile.print("switch:");
		for (int i = 0; i < 20; i++) {
			System.out.printf("%6d", firstSymbol[i]);
			outfile.printf("%6d", firstSymbol[i]);
		}

		System.out.println("\n\n");
		outfile.println("\n\n");

		System.out
				.printf("%13c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%n",
						'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
						'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N');
		outfile.printf(
				"%13c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%n",
				'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
				'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N');
		System.out.print("switch:");
		outfile.print("switch:");
		for (int i = 20; i < 40; i++) {
			System.out.printf("%6d", firstSymbol[i]);
			outfile.printf("%6d", firstSymbol[i]);
		}

		System.out.println("\n\n");
		outfile.println("\n\n");

		System.out.printf("%13c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%n", 'O', 'P',
				'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
		outfile.printf("%13c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%6c%n", 'O', 'P',
				'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
		System.out.print("switch:");
		outfile.print("switch:");
		for (int i = 40; i < 52; i++) {
			System.out.printf("%6d", firstSymbol[i]);
			outfile.printf("%6d", firstSymbol[i]);
		}

		System.out.println("\n");
		outfile.println("\n");

		for (int i = 0; i < symbol.length; i += 20) {
			System.out.println("\n\n");
			outfile.println("\n\n");

			System.out.printf("       ");
			outfile.printf("       ");
			for (int j = i; j < i + 20; j++) {
				System.out.printf("%6d", j);
				outfile.printf("%6d", j);
			}
			System.out.println();
			outfile.println();
			System.out.print("symbol:");
			outfile.print("symbol:");
			for (int j = i; j < i + 20; j++) {
				System.out.printf("%6c", symbol[j]);
				outfile.printf("%6c", symbol[j]);
			}
			System.out.println();
			outfile.println();
			System.out.print("next:  ");
			outfile.print("next:  ");
			for (int j = i; j < i + 20; j++) {
				System.out.printf("%6d", next[j]);
				outfile.printf("%6d", next[j]);
			}
		}

		outfile.close();
	}
}
