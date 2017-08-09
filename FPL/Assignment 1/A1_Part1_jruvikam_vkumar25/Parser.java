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
 * @author Vipin Kumar(vkumar25) and Jruvika Bhimani(jruvikam) 
 *
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
 * A sub-class for handling : stmt -> assign ';'| cmpd
 */
class Stmt {
	Assign assign;
	Cmpd cmpd;

	public Stmt() {
		switch (Lexer.nextToken) {
		case Token.ID:
			assign = new Assign();
			Lexer.lex(); // Skip over ';'
			break;
		case Token.LEFT_BRACE:
			cmpd = new Cmpd();
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
				Code.gen(Code.storeCode(id));
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
 * A sub-class for handling : expr -> term [ ('+' | '-') expr ]
 */
class Expr {
	Term term;
	Expr expr;
	char operator;

	public Expr() {
		term = new Term();
		{
			if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
				operator = Lexer.nextChar;
				Lexer.lex();
				expr = new Expr();
				Code.gen(Code.opCode(operator));
			}
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
		{
			if (Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
				operator = Lexer.nextChar;
				Lexer.lex();
				term = new Term();
				Code.gen(Code.opCode(operator));
			}
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
			Code.gen(Code.intCode(value));
			Lexer.lex();
			break;
		case Token.ID:
			Code.gen(Code.loadCode(Lexer.ident));
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
	static int codeptr = 0;

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
		code[codeptr] = str;
		codeptr++;
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
	public static String loadCode(String ident) {
		int found = 0;

		if (idHashMap.containsKey(ident)) {
			found = idHashMap.get(ident);
		}

		if (found > 3) {
			return addByteCodeLineNumber("iload " + found, 2);
		} else {
			return addByteCodeLineNumber("iload_" + found, 1);
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
	public static String storeCode(String ident) {
		int found = 0;

		if (idHashMap.containsKey(ident)) {
			found = idHashMap.get(ident);
		}

		if (found > 3) {
			return addByteCodeLineNumber("istore " + found, 2);
		} else {
			return addByteCodeLineNumber("istore_" + found, 1);
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
	 * @param i
	 *            The value of the identifier
	 * 
	 * @return A string with the right byte code string for a given number i.e
	 *         if the number can be stored in a byte length and how many bytes
	 *         to leave
	 */
	public static String intCode(int i) {
		if (i > 127) {
			return addByteCodeLineNumber("sipush " + i, 3);
		} else if (i > 5) {
			return addByteCodeLineNumber("bipush " + i, 2);
		} else {
			return addByteCodeLineNumber("iconst_" + i, 1);
		}
	}

	/**
	 * Function for checking which operator is used
	 * 
	 * @param op
	 *            An operator to be checked
	 * 
	 * @return: A string with the right byte code string for the given operator
	 */
	public static String opCode(char op) {
		switch (op) {
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
	 * Generates the final output byte code for a given set of instruction
	 */
	public static void output() {
		for (int i = 0; i < codeptr; i++)
			System.out.println(code[i]);
	}
}
