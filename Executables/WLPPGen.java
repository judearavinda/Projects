	import java.util.*;

	 */
	public class WLPPGen {
	    Scanner in = new Scanner(System.in);

	    // The set of terminal symbols in the WLPP grammar.
	    Set<String> terminals = new HashSet<String>(Arrays.asList("BOF", "BECOMES",
	         "COMMA", "ELSE", "EOF", "EQ", "GE", "GT", "ID", "IF", "INT", "LBRACE",
	         "LE", "LPAREN", "LT", "MINUS", "NE", "NUM", "PCT", "PLUS", "PRINTLN",
	         "RBRACE", "RETURN", "RPAREN", "SEMI", "SLASH", "STAR", "WAIN", "WHILE",
	         "AMP", "LBRACK", "RBRACK", "NEW", "DELETE", "NULL"));

	    List<String> symbols;

		private HashMap<String, String> variables;
		private ArrayList<String> variablesValues = new ArrayList<String>();
		int initialValue = 0;
		int secondStack =-4;
		int loop = 0;

	    // Data structure for storing the parse tree.
	    public class Tree {
	        List<String> rule;

	        ArrayList<Tree> children = new ArrayList<Tree>();

	        // Does this node's rule match otherRule?
	        boolean matches(String otherRule) {
	            return tokenize(otherRule).equals(rule);
	        }
	    }

	    // Divide a string into a list of tokens.
	    List<String> tokenize(String line) {
	        List<String> ret = new ArrayList<String>();
	        Scanner sc = new Scanner(line);
	        while (sc.hasNext()) {
	            ret.add(sc.next());
	        }
	        return ret;
	    }

	    // output leftmost derivation of tree t with indentation d
	    public void traverse(Tree t, Map<String, String> variablesMap ) {

	        for(Tree c : t.children) {  // print all subtrees
	            String identifier = "int";
	           if (c.matches("dcl type ID"))
	           {
	        	   if (c.children.get(0).matches("type INT STAR"))
	        	   {
	        		   identifier = "int*";
	        	   }
	        	 if (!variablesMap.containsKey(c.children.get(1).rule.get(1)))
	      		 {
	      			variablesMap.put(c.children.get(1).rule.get(1),identifier);
	      		 }
	        	 else
	        	 {
	        		 System.err.println("ERROR due to duplicate");
	        		 System.exit(0);
	        		 return;
	        	 }
	           }
	           if (c.matches("factor ID")||c.matches("lvalue ID"))
	           {
	        	   if (!variablesMap.containsKey(c.children.get(0).rule.get(1)))
	        		 {
	        		   System.err.println("ERROR due to not being defined");
	        		   System.exit(0);
	        		   return;
	        		 }
	           }
	           if (c.matches("factor ID")||c.matches("lvalue ID"))
	           {
	        	   if (!variablesMap.containsKey(c.children.get(0).rule.get(1)))
	        		 {
	        		   System.err.println("ERROR due to not being defined");
	        		   System.exit(0);
	        		   return;
	        		 }
	           }
	       	traverse(c,variablesMap);
	        }
	    }
	    // output leftmost derivation of tree t with indentation d
	    public void genCode(Tree t) {
	    	if (t.matches("procedure INT WAIN LPAREN dcl COMMA dcl RPAREN LBRACE dcls statements RETURN expr SEMI RBRACE"))
	    	{
	    		System.out.println(".import print");
	    		System.out.println(".import init");
	    		System.out.println(".import new");
	    		System.out.println(".import delete");

	    		System.out.println("lis $22\n.word new\nlis$23\n.word delete");
	    		System.out.println("lis $25\n.word init");


	    		System.out.println("add $29,$30,$0");
	    		System.out.println("lis $4");
	    		System.out.println(".word 4");
	    		System.out.println("lis $10");
	    		System.out.println(".word print");


	    		System.out.println("lis $11");
	    		System.out.println(".word 1");
	    		System.out.println("sw $1,-4($29)");
	    		System.out.println("sw $2,-8($29)");
	    		variablesValues.add(t.children.get(3).children.get(1).rule.get(1));
	    		variablesValues.add(t.children.get(5).children.get(1).rule.get(1));
	    		secondStack = - (1 +variables.size())*4;// should be - 8 or above

	    		if(t.children.get(3).children.get(0).matches("type INT STAR")){
	    			System.out.println("sw $31, " + secondStack + "($30)");
					secondStack -= 4;
					System.out.println("jalr $25");
					secondStack += 4;
					System.out.println("lw $31, "+ secondStack + "($30)");
	    		}else{
	    			System.out.println("add $2, $0, $0");

					System.out.println("sw $31, " + secondStack + "($30)");
					secondStack -= 4;
					System.out.println("jalr $25");
					secondStack += 4;
					System.out.println("lw $31, "+ secondStack + "($30)");
	    		}

	    		genCode(t.children.get(8));
	    		genCode(t.children.get(9));
	    		genCode(t.children.get(11));
	    		System.out.println("jr $31");
	    	}
	    	else if (t.matches("dcls") || t.matches("statements")){
	            return;
	            //do nothing
	    	}
	    	else if (t.matches("factor LPAREN expr RPAREN"))
	    	{
	    		genCode(t.children.get(1));
	    	}
	    	else if (t.matches("dcl type ID"))
	    	{
	    		variablesValues.add(t.children.get(1).rule.get(1));

	    		String var = t.children.get(1).rule.get(1);
	    		 initialValue = (lookup(var)+1)*4;

	    		 System.out.println("lis $3");
	    		 System.out.println(".word "+ initialValue);
	    		 System.out.println("sub $3, $29, $3");

	    		return;
	    	}
	    	else if (t.matches("factor ID"))
	    	{
	    		 String var = t.children.get(0).rule.get(1);
	    		 initialValue = lookup(var);
	    		 System.out.println("lw $3, "+ (1+initialValue)*-4 + "($29)");
	    		 return;
	    	}
	    	else if(t.matches("lvalue ID")){
	    		String var = t.children.get(0).rule.get(1);
	    		 initialValue = (lookup(var)+1)*4;

	    		 System.out.println("lis $3");
	    		 System.out.println(".word "+ initialValue);
	    		 System.out.println("sub $3, $29, $3");
	    	}

	    	else if (t.matches("term factor"))
	    	{
	    		genCode(t.children.get(0));
	    	}
	    	else if (t.matches("expr term"))
	    	{
	    		genCode(t.children.get(0));
	    	}
	    	else if (t.matches("expr expr MINUS term")){

	            genCode(t.children.get(0));
	            System.out.println("sw $3, " + secondStack + "($30)");
	            secondStack-=4;
	            genCode(t.children.get(2));
	            secondStack +=4;
	           System.out.println("lw $5, "+ secondStack +"($30)");
	            System.out.println("sub $3, $5, $3");
	        }
	    	else if (t.matches("expr expr PLUS term")){

	            genCode(t.children.get(0));
	            System.out.println("sw $3, " + secondStack + "($30)");
	            secondStack-=4;
	            genCode(t.children.get(2));
	            secondStack +=4;
	           System.out.println("lw $5, "+ secondStack +"($30)");
	            System.out.println("add $3, $5, $3");
	        }
	    	else if (t.matches("term term STAR factor")){

	    	    genCode(t.children.get(0));
		        System.out.println("sw $3, " + secondStack + "($30)");
		        secondStack-=4;
		        genCode(t.children.get(2));
		        secondStack +=4;
		        System.out.println("lw $5, "+ secondStack +"($30)");
	            System.out.println("mult $5, $3");
	            System.out.println("mflo $3");

	        }
	    	else if (t.matches("term term PCT factor")){

	    	    genCode(t.children.get(0));
		        System.out.println("sw $3, " + secondStack + "($30)");
		        secondStack-=4;
		        genCode(t.children.get(2));
		        secondStack +=4;
		        System.out.println("lw $5, "+ secondStack +"($30)");
	            System.out.println("div $5, $3");
	            System.out.println("mfhi $3");
	        }
	    	else if (t.matches("term term SLASH factor")){

	    	    genCode(t.children.get(0));
		        System.out.println("sw $3, " + secondStack + "($30)");
		        secondStack-=4;
		        genCode(t.children.get(2));
		        secondStack +=4;
		        System.out.println("lw $5, "+ secondStack +"($30)");
	            System.out.println("div $5, $3");
	            System.out.println("mflo $3");
	        }
	    	else if (t.matches("factor NUM"))
	    	{
	    		System.out.println("lis $3");
	    		System.out.println(".word " + t.children.get(0).rule.get(1));
	    	}
	    	else if (t.matches("factor NULL"))
	    	{
	    		System.out.println("add $3,$0,$0");
	    	}
	    	else if (t.matches("factor STAR factor"))
	    	{
	    		genCode(t.children.get(1));
	    		System.out.println("lw $3,0($3)");
	    	}
	    	else if (t.matches("factor AMP lvalue"))
	    	{
	    		genCode(t.children.get(1));
	    	}
	    	else if (t.matches("lvalue STAR factor"))
	    	{
	    		genCode(t.children.get(1));
	    	}
	    	else if (t.matches("statement PRINTLN LPAREN expr RPAREN SEMI")){
	            genCode(t.children.get(2));
	            System.out.println("add $1, $0, $3");
	            System.out.println("sw $31, "+secondStack+"($30)");
	            System.out.println("lis $3");
	            System.out.println(".word "+secondStack);
	            System.out.println("add $30, $30, $3");
	            System.out.println("jalr $10");
	            System.out.println( "sub $30, $30, $3");
	            System.out.println( "lw $31, " +secondStack +"($30)");
	        }
	    	else if (t.matches("lvalue LPAREN lvalue RPAREN"))
	    	{
	    		genCode(t.children.get(1));
	    	}
	    	else if (t.matches("dcls dcls dcl BECOMES NUM SEMI"))
	    	{
	    		genCode(t.children.get(0));
	    		genCode(t.children.get(1));

	    		System.out.println("sw $3, "+ secondStack +"($30)");

	    		 System.out.println("lis $3");
	    		 System.out.println(".word " + t.children.get(3).rule.get(1));
	    		 //initialValue = lookup(t.children.get(1).children.get(1).rule.get(1));
	    		 System.out.println("lw $5, " + secondStack + "($30)");
	    		System.out.println("sw $3, 0($5)");
	    		 // System.out.println("sw $3, " + ((initialValue+1)*-4) + "($29)" );
	    	}
	    	else if (t.matches("dcls dcls dcl BECOMES NULL SEMI"))
	    	{
	    		genCode(t.children.get(0));
	    		genCode(t.children.get(1));
	    		System.out.println("sw $0,0($3)");
	    	}
	    	else if (t.matches("statement lvalue BECOMES expr SEMI")){
	            genCode(t.children.get(0));
	            System.out.println("sw $3, "+ secondStack +"($30)");
	            secondStack-=4;
	            int secondValue = initialValue;
	            genCode(t.children.get(2));
	            secondStack+=4;
	            System.out.println("lw $5, "+ secondStack +"($30)");

	            System.out.println("sw $3, 0($5)");
	            //System.out.println("lw $5, "+ secondStack +"($30)");
	           //System.out.println("sw $3, " +((secondValue+1)*-4) + "($29)");
	        }
	    	else if (t.matches("test expr LT expr")){
	            genCode(t.children.get(0));
	            System.out.println( "sw $3, "+secondStack+"($30)");
	            secondStack-=4;
	            genCode(t.children.get(2));
	            secondStack +=4;
	            System.out.println("lw $5, "+secondStack +"($30)");
	            System.out.println( "slt $3, $5, $3");

	        }
	    	else if (t.matches("statement WHILE LPAREN test RPAREN LBRACE statements RBRACE")){
	            int start = loop;
	            System.out.println("loop" +start +":");
	            loop+=1;

	            genCode(t.children.get(2));
	            System.out.println( "beq $3, $0, endLoop"+ start);

	            genCode(t.children.get(5));

	            System.out.println("beq $0, $0, loop"+ start);
	            System.out.println("endLoop"+ start + ":");

	        }
	    	else if (t.matches("test expr NE expr")){
	            genCode(t.children.get(0));
	            System.out.println( "sw $3, "+secondStack +"($30)");
	            secondStack -= 4;
	            genCode(t.children.get(2));
	            secondStack += 4;
	           System.out.println("lw $5, "+secondStack+"($30)");

	            System.out.println( "bne $3, $5, 2");
	            System.out.println("add $3, $0, $0");
	             System.out.println("beq $0, $0, 1");
	             System.out.println("add $3, $0, $11");
	        }
	    	else if (t.matches("test expr LE expr")){
	            genCode(t.children.get(0));
	            System.out.println( "sw $3, "+secondStack +"($30)");
	            secondStack -= 4;
	            genCode(t.children.get(2));
	            secondStack += 4;
	           System.out.println("lw $5, "+secondStack+"($30)");

	           System.out.println("add $3, $3, $11");

	           System.out.println("slt $3, $5, $3");
	        }
	    	else if (t.matches("test expr GE expr")){
	            genCode(t.children.get(0));
	            System.out.println( "sw $3, "+secondStack +"($30)");
	            secondStack -= 4;
	            genCode(t.children.get(2));
	            secondStack += 4;
	           System.out.println("lw $5, "+secondStack+"($30)");

	           System.out.println("add $5, $5, $11");

	           System.out.println("slt $3, $3, $5");
	        }
	    	else if (t.matches("test expr GT expr")){
	            genCode(t.children.get(0));
	            System.out.println( "sw $3, "+secondStack +"($30)");
	            secondStack -= 4;
	            genCode(t.children.get(2));
	            secondStack += 4;
	           System.out.println("lw $5, "+secondStack+"($30)");

	           System.out.println("slt $3, $3, $5");
	        }
	    	else if (t.matches("test expr EQ expr")){
	            genCode(t.children.get(0));
	            System.out.println( "sw $3, "+secondStack +"($30)");
	            secondStack -= 4;
	            genCode(t.children.get(2));
	            secondStack += 4;
	           System.out.println("lw $5, "+secondStack+"($30)");

	            System.out.println( "beq $3, $5, 2");
	            System.out.println("add $3, $0, $0");
	             System.out.println("beq $0, $0, 1");
	             System.out.println("add $3, $0, $11");
	        }
	    	else if (t.matches("statement IF LPAREN test RPAREN LBRACE statements RBRACE ELSE LBRACE statements RBRACE")){

	            int start = loop;
	            loop +=1;
	            genCode(t.children.get(2));

	            System.out.println("beq $3, $0, else" + start);
	            genCode(t.children.get(5));
	            System.out.println("beq $0, $0, endLoop" + start);
	            System.out.println("else" + start +":");

	            genCode(t.children.get(9));
	            System.out.println("endLoop" + start + ":");

	        }
	    	else
			{
				for (int k = 0; k < t.children.size();k++)
				{
					genCode(t.children.get(k));
				}
			}
	    	return;
	        }

	    private int lookup(String string) {
	    	for (int i = 0;i < variablesValues.size();i++)
	    	{
	    		if (string.equals(variablesValues.get(i)))
	    			return i; //return the offset
	    	}
	    	return -1; //this is an error (SHOULD NOT HAPPEN)
		}

		// Read and return wlppi parse tree
	    Tree readParse(String lhs) {
	        String line = in.nextLine();
	        List<String> tokens = tokenize(line);
	        Tree ret = new Tree();
	        ret.rule = tokens;
	        if (!terminals.contains(lhs)) {
	            Scanner sc = new Scanner(line);
	            sc.next(); // discard lhs
	            while (sc.hasNext()) {
	                String s = sc.next();
	                ret.children.add(readParse(s));
	            }
	        }
	        return ret;
	    }


	    // Compute symbols defined in t
	    List<String> genSymbols(Tree t) {
	        return null;
	    }

	    // Generate the code for the parse tree t.
	    String traverse(Tree t) {
	        return null;
	    }

	    // Main program
	    public static final void main(String args[]) {
	        new WLPPGen().go();
	    }


	    public void go() {
	        Tree parseTree = readParse("S");
	        variables = new HashMap<String, String>();
	        traverse(parseTree,variables);
	        semanticScan(parseTree);
	        genCode(parseTree);
	    }

		private void semanticScan(Tree parseTree) {
			if (parseTree.rule.get(0).equals("expr")||parseTree.rule.get(0).equals("term"))
			{
				 getExpression(parseTree,2) ;
			}
			else if (parseTree.rule.get(0).equals("factor")||parseTree.rule.get(0).equals("lvalue"))
			{
				  getExpression(parseTree,2) ;
			}
			else if (parseTree.rule.get(0).equals("test"))
			{
				getExpression(parseTree,2) ;
			}
			else if (parseTree.rule.get(0).equals("statements")||parseTree.rule.get(0).equals("statement"))
			{
				getExpression(parseTree,2) ;
			}
			else if (parseTree.rule.get(0).equals("dcl")||parseTree.rule.get(0).equals("dcls"))
			{
				getExpression(parseTree,2) ;
			}
			else if (parseTree.rule.get(0).equals("procedure"))
			{
				getExpression(parseTree,2) ;
			}
			else
			{
				for (int k = 0; k < parseTree.children.size();k++)
				{
					semanticScan(parseTree.children.get(k));
				}
			}
		}

		private int getExpression(Tree t, int i) {
			int initial;
			int finalRule;
			int thirdRule;
			 //return 1 if int and returns 0 if int*
			 if (t.rule.get(0).equals("expr")||t.rule.get(0).equals("term"))
				{
				  if (t.matches("expr expr PLUS term"))
				  {
					  initial = getExpression(t.children.get(0),i);
					  finalRule = getExpression(t.children.get(2),i);
					  if (Math.abs(initial - finalRule) == 1)
					  {
						 return 0;
					  }
					  else if (initial+finalRule == 2)
					  {
						  return 1;
					  }
					  else
					  {
						  System.err.println("ERROR at PLUSING");
						  System.exit(0);
					  }
				  }
				  else if (t.matches("expr term")||t.matches("term factor"))
				  {
					  return getExpression(t.children.get(0),i);
				  }
				  else if (t.matches("expr expr MINUS term"))
				  {
					  initial = getExpression(t.children.get(0),i);
					  finalRule = getExpression(t.children.get(2),i);

					  if (initial == 0 && finalRule ==1)
					  {
						 return 0;
					  }
					  else if (initial+finalRule == 0||initial+finalRule ==2)
					  {
						  return 1;
					  }
					  else
					  {
						  System.err.println("ERROR at MINUSING");
						  System.exit(0);
					  }
				  }
				  else if (t.matches("term term STAR factor")||t.matches("term term SLASH factor")||t.matches("term term PCT factor"))
				  {
					  initial = getExpression(t.children.get(0),i);
					  finalRule = getExpression(t.children.get(2),i);
					  if (initial+finalRule ==2)
					  {
						  return 1;
					  }
					  else
					  {
						  System.err.println("ERROR at MULTIPLYING /DIVIDING/PCTING");
						  System.exit(0);
					  }
				  }
				}
			 else if (t.rule.get(0).equals("test"))
			 {
				 if (t.matches("test expr EQ expr")||t.matches("test expr NE expr")||t.matches("test expr LT expr")||t.matches("test expr LE expr")||t.matches("test expr GE expr")||t.matches("test expr GT expr"))
				 {
					 initial =getExpression(t.children.get(0),0);
					 finalRule = getExpression(t.children.get(2),0);
					 if (initial ==1 && finalRule==1)
					 {
						 return -1;
					 }
					 else if (initial ==0 && finalRule==0)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at comparison");
						 System.exit(0);
					 }
				 }
			 }
			 else if (t.rule.get(0).equals("statement"))
			 {
				 if (t.matches("statement lvalue BECOMES expr SEMI"))
				 {
					 initial = getExpression(t.children.get(0),0);
					 finalRule = getExpression(t.children.get(2),0);
					 if (initial ==0 && finalRule == 0)
					 {
						 return -1;
					 }
					 if (initial ==1 && finalRule == 1)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at assignment");
						 System.exit(0);
					 }
				 }
				 else if (t.matches("statement IF LPAREN test RPAREN LBRACE statements RBRACE ELSE LBRACE statements RBRACE"))
				 {
					 initial = getExpression(t.children.get(2),0);
					 finalRule = getExpression(t.children.get(5),0);
					 thirdRule =  getExpression(t.children.get(9),0);
					 if (initial ==-1 && finalRule ==-1 && thirdRule==-1 )
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at if statements");
						 System.exit(0);
					 }
				 }
				 else if (t.matches("statement WHILE LPAREN test RPAREN LBRACE statements RBRACE"))
				 {
					 initial = getExpression(t.children.get(2),0);
					 finalRule = getExpression(t.children.get(5),0);
					 if (initial ==-1 && finalRule ==-1)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at while statements");
						 System.exit(0);
					 }
				 }
				 else if (t.matches("statement PRINTLN LPAREN expr RPAREN SEMI"))
				 {
					 initial = getExpression(t.children.get(2),0);
					 if (initial ==1)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at println statements");
						 System.exit(0);
					 }
				 }
				 else if (t.matches("statement DELETE LBRACK RBRACK expr SEMI"))
				 {
					 initial = getExpression(t.children.get(3),0);
					 if (initial ==0)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at delete statements");
						 System.exit(0);
					 }
				 }
			 }
			 else if (t.rule.get(0).equals("dcls"))
			 {
				  if (t.matches("dcls"))
				 {
					 return -1;
				 }
				  else if (t.matches(" dcls dcls dcl BECOMES NUM SEMI"))
					{
						 initial = getExpression(t.children.get(0),0);
						 finalRule= getExpression(t.children.get(1),0);
						 if (initial ==-1 && finalRule ==1)
						 {
							 return -1;
						 }
						 else
						 {
							 System.err.println("ERROR at become num statements");
							 System.exit(0);
						 }
					}
				  else if (t.matches(" dcls dcls dcl BECOMES NULL SEMI"))
					{
						 initial = getExpression(t.children.get(0),0);
						 finalRule= getExpression(t.children.get(1),0);
						 if (initial ==-1 && finalRule ==0)
						 {
							 return -1;
						 }
						 else
						 {
							 System.err.println("ERROR at become null statements");
							 System.exit(0);
						 }
					}
			 }
			 else if (t.rule.get(0).equals("dcl"))
			 {
				 if (t.matches("dcl type ID"))
				 {

				 String ruleType = variables.get(t.children.get(1).rule.get(1)) ;
					if (ruleType.equals("int"))
					{
						return 1;
					}
					else if (ruleType.equals("int*"))
					{
						return 0;
					}
					else
					{
						System.err.println("ERROR at dcl type ID");
						System.exit(0);
					}
				}

			 }
			 else if (t.rule.get(0).equals("procedure"))
			 {
				 if (t.matches("procedure INT WAIN LPAREN dcl COMMA dcl RPAREN LBRACE dcls statements RETURN expr SEMI RBRACE"))
				 {
					 initial = getExpression(t.children.get(3),0);
					 finalRule= getExpression(t.children.get(5),0);
					 thirdRule= getExpression(t.children.get(8),0);
					 int fourthRule = getExpression(t.children.get(9),0);
					 int fifthRule = getExpression(t.children.get(11),0);
					 if ((initial == 0 ||initial == 1) && finalRule == 1 && thirdRule == -1 && fourthRule ==-1 && fifthRule == 1)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at procedure call");
						 System.exit(0);
					 }
				 }
			 }
			 else if (t.rule.get(0).equals("statements"))
			 {
				if (t.matches("statements"))
				{
					return -1;
				}
				if (t.matches("statements statements statement"))
				{
					 initial = getExpression(t.children.get(0),0);
					 finalRule= getExpression(t.children.get(1),0);
					 if (initial ==-1 && finalRule ==-1)
					 {
						 return -1;
					 }
					 else
					 {
						 System.err.println("ERROR at statement statements");
						 System.exit(0);
					 }
				}
			 }

			else if(t.rule.get(0).equals("lvalue") ||t.rule.get(0).equals("factor")){
			if (t.matches("factor NUM"))
			{
				return 1;
			}
			else if (t.matches("factor NULL"))
			{
				return 0;
			}
			else if (t.matches("lvalue STAR factor")){
	            initial = getExpression(t.children.get(1),0);
	            if (initial ==0)
	            {
	            	return 1;
	            }
	            else
	            {
	            	System.err.println("ERROR at starring");
					System.exit(0);
	    		}
			}
			else if (t.matches("factor STAR factor")){
				 initial = getExpression(t.children.get(1),0);
		            if (initial ==0)
		            {
		            	return 1;
		            }
		            else
		            {
		            	System.err.println("ERROR at starring");
						System.exit(0);
		    		}
			}
			else if (t.matches("factor NEW INT LBRACK expr RBRACK")){
				 initial = getExpression(t.children.get(3),1);
		            if (initial ==1)
		            {
		            	return 0;
		            }
		            else
		            {
		            	System.err.println("ERROR at newing");
						System.exit(0);
		    		}
			}
			else if (t.matches("factor AMP lvalue")){
				 initial = getExpression(t.children.get(1),1);
		            if (initial ==1)
		            {
		            	return 0;
		            }
		            else
		            {
		            	System.err.println("ERROR at amping");
						System.exit(0);
		    		}
			}
			else if (t.matches("factor LPAREN expr RPAREN")){
				return getExpression(t.children.get(1),i);
	        }
			else if (t.matches("lvalue ID")||t.matches("factor ID"))
			{
				String ruleType = variables.get(t.children.get(0).rule.get(1)) ;
				if (ruleType.equals("int"))
				{
					return 1;
				}
				else if (ruleType.equals("int*"))
				{
					return 0;
				}
				else
				{
					System.err.println("ERROR at ID");
					System.exit(0);
				}
			}
			else if (t.matches("lvalue LPAREN lvalue RPAREN")){
	            return getExpression(t.children.get(1),i);
	        }
			else if(t.matches("factor NEW INT LBRACK expr RBRACK")){
				genCode(t.children.get(3));
				System.out.println("add $1, $0, $3");
				System.out.println("sw $31, " + secondStack + "($30)");
				secondStack -= 4;
				System.out.println("jalr $22");
				secondStack += 4;
				System.out.println("lw $31, "+ secondStack + "($30)");

			}
			else if(t.matches("statement DELETE LBRACK RBRACK expr SEMI")){
				genCode(t.children.get(3));
				System.out.println("add $1, $0, $3");
				System.out.println("sw $31, " + secondStack + "($30)");
				secondStack -= 4;
				System.out.println("jalr $23");
				secondStack += 4;
				System.out.println("lw $31, "+ secondStack + "($30)");
			}

			else
			{
				System.err.println("ERROR Invalid Error rule");
				System.exit(0);
			}
		}
			 return 0;

		}
		}
