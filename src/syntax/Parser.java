package syntax;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import lexcial.Scanner;
import lexcial.TokenInfo;
import lexcial.TokenTypeEnum;

public class Parser {
	
	/**
	 * LL(1) Parser
	 * Input: Config file representing LL1 Table & Input file.
	 * Output: Grammar Tree root node.
	 */
	
	//Filenames
	private String input;
	private String config;
	
	//Identifiers
	private final String TERMINALS_ID = "terminals";
	private final String EPSILON_ID = "eps";
	private final String END_OF_INPUT_ID = "$";
	
	//Variables
	private HashMap<String, HashMap<String, String>> parse_table; //holds LL1 table
	private ArrayList<String> terminals; //holds Terminals from config file
	private ArrayList<String> non_terminals; //holds non-Terminals from config file
	private Stack<TreeNode> parse_stack; //Parsing Stack
	
	
	//Constructor
	public Parser(String configFilename, String inputFilename) {
		config = configFilename;
		input = inputFilename;
		non_terminals = new ArrayList<String>();
		parse_stack = new Stack<TreeNode>();
	}
	
	
	/**
	 * Parse Config file into LL(1) Table
	 * Read Input file and parse grammar tree 
	 * @return Grammer Tree root node.
	 */
	public TreeNode yyLL1parse() {
		
		
		parse_table = parseConfigFile(config);
		if(parse_table == null)
			return null;
		
//		System.out.println("Parsing input..."); //TODO DEBUG
		
		//Initialize Stack
		int nodeId = 0;
		TreeNode root = new TreeNode(non_terminals.get(0), nodeId++);
		parse_stack.push(new TreeNode(END_OF_INPUT_ID, -1));
		parse_stack.push(root);
		
		
		Scanner sc;
		TokenInfo token;

		
		try {
			
			sc = new Scanner(input);
			
			do {

				//Get next token
				token = sc.yylex();
				String tokenTypeString = token.getType().toString();
				
				//Ignore whitespace and comments
				if(token.getType() == TokenTypeEnum.WHITE || token.getType() == TokenTypeEnum.CMMNT) 
					continue;
				
				//Check if token type isn't part of LL(1) table
				if(!terminals.contains(tokenTypeString))
					throw new LL1Exception();
				
				TreeNode check = parse_stack.pop();
				String check_noid = check.getName();
				
				//While stack top is non_terminal
				while(non_terminals.contains(check_noid)) {
					String tab = parse_table.get(check_noid).get(terminals.get(terminals.indexOf(tokenTypeString)));
					
					//Check if empty tab
					if(tab == null || tab.isEmpty() || tab.equals(""))
						throw new LL1Exception();
					
					String[] tab_tokens = tab.split(";");
					
					
					//Insert Grammar rule to stack (in reverse order)
					for(int i = tab_tokens.length - 1; i >= 0; i--) {
						TreeNode node = new TreeNode(tab_tokens[i], nodeId++);
						parse_stack.push(node);
						check.addChild(node);
					}
					
					
					check = parse_stack.pop();
					check_noid = check.getName();
					
					//If Epsilon, discard it and continue
					if(check_noid.equals(EPSILON_ID)) {
						check = parse_stack.pop();
						check_noid = check.getName();
						continue;
					}
				}
				
				
				//Terminal from stack isn't equal to Terminal from input
				if(!check_noid.equals(tokenTypeString))
					throw new LL1Exception();
				
				// Insert Tokens to Tree
				check.setToken(token);

			} while(token.getType() != TokenTypeEnum.EOF);
			
			
			if(parse_stack.empty() || !parse_stack.peek().getName().equals(END_OF_INPUT_ID))
				throw new LL1Exception();

//			System.out.println("Done."); //TODO DEBUG
			return root;
			
			
		} catch (FileNotFoundException e) {
			System.err.println("[ERROR] Input File not Found:\n" + e.toString());
			return null;
		} catch (LL1Exception e) {
			System.err.println("input is wrong according to LL(1) table");
			return null;
		}
	}
	
	
	/**
	 * @param configFilename : Name of config file describing LL(1) table.
	 * @return HashMap of HashMaps defining each tab in the LL(1) table.
	 */
	private HashMap<String, HashMap<String, String>> parseConfigFile(String configFilename) {
		
//		System.out.println("Parsing Config File..."); //TODO DEBUG
		HashMap<String, HashMap<String, String>> table = new HashMap<String, HashMap<String,String>>();
		BufferedReader reader;
		String line;
		
		try {
			
			reader = new BufferedReader(new FileReader(configFilename));
			
			//Handle Terminals - must be first row
			line = reader.readLine();
			
			if(line == null || !line.substring(0, line.indexOf('=')).equals(TERMINALS_ID)) {
				System.err.println("[ERROR] Invalid config file");
				reader.close();
				return null;
			}
			
			terminals = new ArrayList<String>(Arrays.asList(line.substring(line.indexOf('=') + 1, line.length()).split(",")));
			
			
			while((line = reader.readLine()) != null) {
				
				//Ignore comments in config file
				if(line.charAt(0) == '#')
					continue; 
				
				String non_terminal = line.substring(0, line.indexOf('='));
				non_terminals.add(non_terminal);
				
				
				String[] products = line.substring(line.indexOf('=') + 1, line.length()).split(",", -1);
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				for(int i = 0; i < products.length; i++) {
					map.put(terminals.get(i), products[i]);
				}
				
				
				table.put(non_terminal, map);
			}

			reader.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("[ERROR] Config File not Found: " + e.toString());
			return null;
		} catch (IOException e) {
			System.err.println("[ERROR] Failed reading from config file" + e.toString());
			return null;
		}
		
//		System.out.println("Successfuly Generated LL1 Table."); //TODO DEBUG
		return table;
	}

}
