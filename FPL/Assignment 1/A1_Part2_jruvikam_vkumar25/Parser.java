import java.util.*;

/**
 * Grammar for a simple programming language, TinyPL: 
 *
 *	program -> decls stmts end 
 *	
 *	decls -> int idlist ';' 
 *	
 *	idlist -> id [',' idlist ]
 *	
 *	stmts -> stmt [ stmts ]
 *	
 *	stmt -> assign ';'| cmpd
 *	
 *	assign -> id '=' expr 
 *	
 *	cmpd -> '{' stmts '}' 
 *	
 *	expr -> term [ ('+' | '-') expr ] 
 *	
 *	term -> factor [ ('*' | '/') term ] 
 *	
 *	factor -> int_lit | id | '(' expr ')'
 *
 */

/**
 * Parser Class which is driver Class
 * 
 * @author Vipin Kumar and Jruvika Bhimani
 *
 *
 *         References :
 *         http://www.cs.miami.edu/home/burt/reference/java/language_vm_specification.pdf
 */
public class Parser {

	public static void main(String[] args) {
		System.out.println("Enter an expression, end with semi-colon!\n");
		Lexer.lex();
		new Program();
		System.out.println("\nBytecodes:\n");
		Code.output();
	}
}

/*
 * A sub-class for handling : program -> decls stmts end
 */
class Program {
	Decls decls;
	Stmts stmts;

	public Program() {
		decls = new Decls();
		stmts = new Stmts();
		if (Lexer.nextToken == Token.KEY_END) {
			Code.gen(Code.addByteCodeLineNumber("return", 1));
		}
	}
}

/*
 * A sub-class for handling : decls -> int idlist ‘;’
 */
class Decls {
	Idlist idlist;

	public Decls() {
		if (Lexer.nextToken == Token.KEY_INT) {
			Lexer.lex();
			idlist = new Idlist();
			Lexer.lex(); // Skip over ';'
		}
	}
}

/*
 * A sub-class for handling : idlist -> id [',' idlist ]
 */
class Idlist {
	Idlist idlist;
	String id;

	public Idlist() {
		if (Lexer.nextToken == Token.ID) {
			id = Lexer.ident;
			Code.idHashMap.put(id, Code.idCounter);
			Code.idCounter++;
			Lexer.lex();
			if (Lexer.nextToken == Token.COMMA) {
				Lexer.lex();
				idlist = new Idlist();
			}
		}
	}
}

/*
 * A sub-class for handling : stmts -> stmt [ stmts ]
 */
class Stmts {
	Stmt stmt;
	Stmts stmts;

	public Stmts() {
		stmt = new Stmt();
		if (Lexer.nextToken == Token.KEY_END) {
			return;
		}
		if (Lexer.nextToken != Token.RIGHT_BRACE) {
			stmts = new Stmts();
		}
	}
}

/*
 * A sub-class for handling : stmt -> assign ';'| cmpd | cond | loop
 */
class Stmt {
	Assign assign;
	Cmpd cmpd;
	Cond cond;
	Loop loop;

	public Stmt() {
		switch (Lexer.nextToken) {
		case Token.ID:
			assign = new Assign();
			Lexer.lex(); // Skip over ';'
			break;
		case Token.LEFT_BRACE:
			cmpd = new Cmpd();
			break;
		case Token.KEY_IF:
			cond = new Cond();
			break;
		case Token.KEY_FOR:
			loop = new Loop();
			break;
		default:
			break;
		}
	}
}

/*
 * A sub-class for handling : assign -> id '=' expr
 */
class Assign {
	Expr expr;
	String id;

	public Assign() {
		id = Lexer.ident;
		if (Lexer.nextToken == Token.ID) {
			Lexer.lex();
			if (Lexer.nextToken == Token.ASSIGN_OP) {
				Lexer.lex();
				expr = new Expr();
				Code.gen(Code.addStoreLine(id));
			}
		}
	}
}

/*
 * A sub-class for handling : cmpd -> '{' stmts '}'
 */
class Cmpd {
	Stmts stmts;

	public Cmpd() {
		if (Lexer.nextToken == Token.LEFT_BRACE) {
			Lexer.lex();
			stmts = new Stmts();
			Lexer.lex(); // skip over ‘}’
		}
	}

}

/*
 * A sub-class for handling : cond -> if '(' rel_exp ')' stmt [ else stmt ]
 */
class Cond {
	RelExp relExp;
	Stmt stmt1, stmt2;

	public Cond() {
		if (Lexer.nextToken == Token.KEY_IF) {
			Lexer.lex(); // Skip over '('
			if (Lexer.nextToken == Token.LEFT_PAREN) {
				Lexer.lex();
				relExp = new RelExp();
				Lexer.lex();// Skip over ')'
				int relExpLineNum = Code.addIfLine(relExp.operator);
				stmt1 = new Stmt();
				if (Lexer.nextToken == Token.KEY_ELSE) {
					int gotoLineNum = Code.addGotoLine();
					Code.code[relExpLineNum] = Code.code[relExpLineNum] + Code.lineNum;
					Lexer.lex();
					stmt2 = new Stmt();
					Code.code[gotoLineNum] = Code.code[gotoLineNum] + Code.lineNum;
				} else {
					Code.code[relExpLineNum] = Code.code[relExpLineNum] + Code.lineNum;
				}

			}
		}
	}
}

/*
 * A sub-class for handling : rel_exp -> expr ('<' | '>' | '==' | '!= ') expr
 */
class RelExp {
	Expr expr1, expr2;
	String operator;

	public RelExp() {
		expr1 = new Expr();
		switch (Lexer.nextToken) {
		case Token.LESSER_OP:
			operator = "<";
			break;
		case Token.GREATER_OP:
			operator = ">";
			break;
		case Token.EQ_OP:
			operator = "==";
			break;
		case Token.NOT_EQ:
			operator = "!=";
			break;
		default:
			break;
		}
		Lexer.lex();
		expr2 = new Expr();
	}

}

/*
 * A sub-class for handling : loop-> for '('[assign]';' [rel_exp] ';'
 * [assign]')' stmt
 */
class Loop {
	Assign assign1, assign2;
	RelExp relExp;
	Stmt stmt;

	public Loop() {
		if (Lexer.nextToken == Token.KEY_FOR) {
			Lexer.lex();// Skip over '('
			if (Lexer.nextToken == Token.LEFT_PAREN) {
				Lexer.lex();

				if (Lexer.nextToken == Token.ID) {
					assign1 = new Assign();
				}

				Lexer.lex();// Skip over ';'
				int gotoLineNum = Code.lineNum;

				if (Lexer.nextToken != Token.SEMICOLON) {
					relExp = new RelExp();
				}

				int lineNumRef = Code.lineNum;
				int relExpLineNum = Integer.MIN_VALUE;

				if (relExp != null && relExp.operator != null) {
					relExpLineNum = Code.addIfLine(relExp.operator);
				}
				Lexer.lex();// Skip over ';'

				int codePtrRef = Code.codePtr;

				if (Lexer.nextToken == Token.ID) {
					assign2 = new Assign();
				}

				// Copy code for later
				String[] codeRef = new String[100];
				int currCodePtrRef = Code.codePtr;
				if (assign2 != null && assign2 != null) {
					int i = 0;
					for (int j = codePtrRef; j < currCodePtrRef; j++) {
						codeRef[i] = Code.code[j];
						i++;
					}
				}

				Code.codePtr = codePtrRef;
				Code.lineNum = lineNumRef + 3;

				if (Lexer.nextToken == Token.RIGHT_PAREN) {
					Lexer.lex();// Skip over ')'

					stmt = new Stmt();

					int codeRefSize = codeRef.length;

					// Copy code for the increment part of the for loop
					if (codeRefSize != 0) {
						for (int k = 1; k < codeRefSize; k++) {
							int len = 0;
							if (codeRef[k] != null) {
								int endIdx1 = codeRef[k].indexOf(":");
								int endIdx2 = codeRef[k - 1].indexOf(":");
								len = Integer.valueOf(codeRef[k].substring(0, endIdx1))
										- Integer.valueOf(codeRef[k - 1].substring(0, endIdx2));
							}
							if (codeRef[k - 1] != null) {
								int idxOfColon = codeRef[k - 1].indexOf(":");
								Code.gen(Code.addByteCodeLineNumber(codeRef[k - 1].substring(idxOfColon + 2), len));
							}
						}

					}

					Code.gen(Code.addByteCodeLineNumber("goto " + gotoLineNum, 3));
					if (relExpLineNum != Integer.MIN_VALUE) {
						Code.code[relExpLineNum] = Code.code[relExpLineNum] + Code.lineNum;
					}
				}
			}
		}
	}
}

/*
 * A sub-class for handling : expr -> term [ ('+' | '-') expr ]
 */
class Expr {
	Term term;
	Expr expr;
	char operator;

	public Expr() {
		term = new Term();
		if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
			operator = Lexer.nextChar;
			Lexer.lex();
			expr = new Expr();
			Code.gen(Code.addOperatorLine(operator));
		}
	}
}

/*
 * A sub-class for handling : term -> factor [ ('*' | '/') term ]
 */
class Term {
	Factor factor;
	Term term;
	char operator;

	public Term() {
		factor = new Factor();
		if (Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
			operator = Lexer.nextChar;
			Lexer.lex();
			term = new Term();
			Code.gen(Code.addOperatorLine(operator));
		}
	}
}

/*
 * A sub-class for handling : factor -> int_lit | id | '(' expr ')'
 */
class Factor {
	Expr expr;
	int value;
	String ident;

	public Factor() {
		switch (Lexer.nextToken) {
		case Token.INT_LIT:
			value = Lexer.intValue;
			Code.gen(Code.addIntegerLine(value));
			Lexer.lex();
			break;
		case Token.ID:
			Code.gen(Code.addLoadLine(Lexer.ident));
			ident = Lexer.ident;
			Lexer.lex();
			break;
		case Token.LEFT_PAREN: // Skip over '('
			Lexer.lex();
			expr = new Expr();
			Lexer.lex(); // Skip over ')'
			break;
		default:
			break;
		}
	}
}

/*
 * A sub-class for Code generation and keep track of all the id's and generating
 * a byte code.
 */
class Code {
	// The array of string for holding the byte code strings
	static String[] code = new String[100];
	static int codePtr = 0;

	// A HashMap for keeping all the identifiers which are used in the code
	static HashMap<String, Integer> idHashMap = new HashMap<String, Integer>();
	static int idCounter = 0;

	// for printing line number
	static int lineNum = 0;

	/**
	 * Function for adding the byte code string;
	 * 
	 * @param str
	 */
	public static void gen(String str) {
		code[codePtr] = str;
		codePtr++;
	}

	/**
	 * Function for adding line number to the the byte code string
	 * 
	 * @param str
	 *            A string
	 * @param length
	 *            How much the increment in the line number for [0 to 3]
	 *            increment by 1 and for [4...] increment by 2
	 * @return The final sting appended with the line number
	 */
	public static String addByteCodeLineNumber(String str, int length) {
		String resultString = lineNum + ": " + str;
		lineNum += length;
		return resultString;
	}

	/**
	 * Function for loading call
	 * 
	 * 1) For the first three variables declared, the load and store
	 * instructions are, respectively, iload_1, iload_2, iload_3 and istore_1,
	 * istore_2, and istore_3.
	 *
	 * 2) For the fourth and subsequent variables, the load and store
	 * instructions are, respectively, iload n and istore n respectively, where
	 * n > 3. The number n is encoded in one byte and placed after the iload and
	 * istore instructions.
	 * 
	 * @param ident
	 *            An identifier which needs to be loaded from the stack
	 * 
	 * @return A string with the right byte code string for a given identifier,
	 *         stating that it is being loaded onto the stack
	 */
	public static String addLoadLine(String ident) {
		int i = 0;

		if (idHashMap.containsKey(ident)) {
			i = idHashMap.get(ident);
		}

		if (i > 3) {
			return addByteCodeLineNumber("iload " + i, 2);
		} else {
			return addByteCodeLineNumber("iload_" + i, 1);
		}
	}

	/**
	 * Function for storing call
	 * 
	 * 1) For the first three variables declared, the load and store
	 * instructions are, respectively, iload_1, iload_2, iload_3 and istore_1,
	 * istore_2, and istore_3.
	 *
	 * 2) For the fourth and subsequent variables, the load and store
	 * instructions are, respectively, iload n and istore n respectively, where
	 * n > 3. The number n is encoded in one byte and placed after the iload and
	 * istore instructions.
	 * 
	 * @param ident
	 *            An identifier which needs to be stored
	 * 
	 * @return A string with the right byte code string for a given identifier,
	 *         stating that its a result storing process
	 */
	public static String addStoreLine(String ident) {
		int i = 0;

		if (idHashMap.containsKey(ident)) {
			i = idHashMap.get(ident);
		}

		if (i > 3) {
			return addByteCodeLineNumber("istore " + i, 2);
		} else {
			return addByteCodeLineNumber("istore_" + i, 1);
		}
	}

	/**
	 * Function for pushing item on stack
	 * 
	 * 1) For small constants, in the range 0..5, the constant is implicit in
	 * the name of the instruction: iconst_0 ... iconst_5
	 *
	 * 2) In generating code for integers in the range 6..127 ,the actual value
	 * comes immediately after the opcode bipush We are not dealing with
	 * negative literal constants in TinyPL, but Java encodes numbers from -128
	 * to +127 using 8 bits (one byte).Therefore, Java leaves one byte after the
	 * instruction for bipush.
	 *
	 * 3) For short integers greater than 127, the generated opcode is sipush.
	 * Now we need two bytes to encode the value and hence Java leaves two bytes
	 * after the instruction for sipush.
	 * 
	 * @param value
	 *            The value of the identifier
	 * 
	 * @return A string with the right byte code string for a given number i.e
	 *         if the number can be stored in a byte length and how many bytes
	 *         to leave
	 */
	public static String addIntegerLine(int value) {
		if (value > 127) {
			return addByteCodeLineNumber("sipush " + value, 3);
		} else if (value > 5) {
			return addByteCodeLineNumber("bipush " + value, 2);
		} else {
			return addByteCodeLineNumber("iconst_" + value, 1);
		}
	}

	/**
	 * Function for checking which operator is used
	 * 
	 * @param operator
	 *            An operator to be checked
	 * 
	 * @return: A string with the right byte code string for the given operator
	 */
	public static String addOperatorLine(char operator) {
		switch (operator) {
		case '+':
			return addByteCodeLineNumber("iadd", 1);
		case '-':
			return addByteCodeLineNumber("isub", 1);
		case '*':
			return addByteCodeLineNumber("imul", 1);
		case '/':
			return addByteCodeLineNumber("idiv", 1);
		default:
			return "";
		}
	}

	/**
	 * Function for handling if statements with different operators
	 * 
	 * @param op
	 *            An operator to check for
	 * @return An integer value for the position in code statements
	 */
	public static int addIfLine(String op) {
		int i = codePtr;
		switch (op) {
		case "<":
			gen(addByteCodeLineNumber("if_icmpge ", 3));
			break;
		case ">":
			gen(addByteCodeLineNumber("if_icmple ", 3));
			break;
		case "==":
			gen(addByteCodeLineNumber("if_icmpne ", 3));
			break;
		case "!=":
			gen(addByteCodeLineNumber("if_icmpeq ", 3));
			break;
		default:
			break;
		}
		return i;
	}

	/**
	 * Function to handle goto statements
	 * 
	 * @return An integer value for the position in code statements
	 */
	public static int addGotoLine() {
		int i = codePtr;
		gen(addByteCodeLineNumber("goto ", 3));
		return i;
	}

	/**
	 * Generates the final output byte code for a given set of instruction
	 */
	public static void output() {
		for (int i = 0; i < codePtr; i++)
			System.out.println(code[i]);
	}
}
