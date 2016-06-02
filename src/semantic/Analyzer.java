package semantic;

import syntax.Parser;
import syntax.TreeNode;

public class Analyzer {
	
	//Filenames
	private String input;
	private final String config = "src/semantic/config.txt";
	
	
	private Parser parser;
	
	
	
	public Analyzer(String inputFilename) {
		input = inputFilename;
		parser = new Parser(config, input);
	}
	
	public TreeNode analyze() {
		
		TreeNode root = parser.yyLL1parse();
		
		return root;
		
	}

}
