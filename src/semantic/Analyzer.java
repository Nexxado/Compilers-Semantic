package semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import lexcial.TokenInfo;
import lexcial.TokenTypeEnum;
import syntax.Parser;
import syntax.TreeNode;

public class Analyzer {
	
	//Filenames
	private final String CONFIG = "src/semantic/config.txt";
	private String input;
	private int blockCounter;
	
	private Parser parser;

	private final String DECLARATION_ID = "DECLARATION";
	private final String ASSIGNMENT_ID = "ASSIGNMENT";
	private final String BLOCK_ID = "BLOCK";
	
	private ArrayList<String> variables;
	private ArrayList<Block> blocks;
	
	
	
	public Analyzer(String inputFilename) {
		input = inputFilename;
		parser = new Parser(CONFIG, input);
		
		variables = new ArrayList<String>();
		blocks = new ArrayList<Block>();
	}
	
	public TreeNode analyze() {
		
		TreeNode root = parser.yyLL1parse();

		blockCounter = 0;
		analyzeTree(root);
		System.out.println("Done Analyzing Tree"); //TODO DEBUG
		
		/**************************************************************/
		/**************************************************************/
		/**************************************************************/
		
		//Remove single declarations from blocks, leave only variables that have been redeclared more than once. 
		for(int i = 0; i < blocks.size(); i++) {
			
			Block block = blocks.get(i);
			HashMap<String, ArrayList<TokenInfo>> declarations = block.getDeclarations();
			
			Iterator<Entry<String, ArrayList<TokenInfo>>> it = declarations.entrySet().iterator();
			while(it.hasNext()) {
				
				Map.Entry<String, ArrayList<TokenInfo>> entry = it.next();
				ArrayList<TokenInfo> tokens = entry.getValue();
				if(tokens.size() <= 1)
					it.remove();
			}
		}
		
		
		StringBuilder redeclareBuilder = new StringBuilder();
		redeclareBuilder.append("redeclarations\n\n");
		int noRedeclareCounter = 0;
		
		for(int i = 0; i < blocks.size(); i++) {
			
			Block block = blocks.get(i);
			if(block.getDeclarations().isEmpty()) {
				noRedeclareCounter++;
				continue;
			}
			
			redeclareBuilder.append("block: " + block.getId() + "\n");
			HashMap<String, ArrayList<TokenInfo>> declarations = block.getDeclarations();
			Iterator<Entry<String, ArrayList<TokenInfo>>> it = declarations.entrySet().iterator();
			
			while(it.hasNext()) {
				
				Map.Entry<String, ArrayList<TokenInfo>> entry = it.next();
				ArrayList<TokenInfo> tokens = entry.getValue();
				redeclareBuilder.append(entry.getKey() + ": ");
				for(int j = 0; j < tokens.size(); j++) {
					redeclareBuilder.append(tokens.get(j).getLine());
					
					if(j != tokens.size() - 1)
						redeclareBuilder.append(", ");
				}
				redeclareBuilder.append("\n");
			}
			redeclareBuilder.append("\n");
		}
		
		if(noRedeclareCounter == blocks.size())
			redeclareBuilder.replace(0, redeclareBuilder.length(), "no redeclarations\n\n");

		System.out.println(redeclareBuilder.toString());
		
		/**************************************************************/
		
		StringBuilder referenceBuilder = new StringBuilder();
		referenceBuilder.append("references\n\n");
		int noReferenceCounter = 0;
		
		for(int i = 0; i < blocks.size(); i++) {
			
			Block block = blocks.get(i);
			if(block.getReferences().isEmpty()) {
				noReferenceCounter++;
				continue;
			}
			
			HashMap<String, ArrayList<TokenInfo>> references = block.getReferences();
			Iterator<Entry<String, ArrayList<TokenInfo>>> it = references.entrySet().iterator();
			
		}
		
		
		
		
		return null;
	}
	
	
	/**
	 * Analyze Grammer tree returned from parser
	 * @param root : Grammar Tree's root node.
	 */
	private void analyzeTree(TreeNode root) {
		Block block = new Block(blockCounter++, null);
		blocks.add(block);
		analyzeBlock(root, block);
	}
	
	/**
	 * Analyze Block Subtree
	 * @param node : initially - root of block subtree
	 * @param block : block object for current block
	 */
	private void analyzeBlock(TreeNode node, Block block) {
		
		switch(node.getName()) {
		
		case BLOCK_ID:
			block = new Block(blockCounter++, block);
			blocks.add(block);
			break;
			
		case DECLARATION_ID:
			analyzeDeclaration(node, block);
			break;
			
		case ASSIGNMENT_ID:
			analyzeAssignment(node, block);
			break;
		
		}
		
		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeBlock(children.get(i), block);
		
	}
	
	
	/**
	 * Analyze Declaration Subtree
	 * @param node : Initially - root of the declaration subtree
	 * @param block : block object for current block
	 */
	private void analyzeDeclaration(TreeNode node, Block block) {
		
		if(node.getToken() != null && node.getToken().getType() == TokenTypeEnum.ID) {
			if(!variables.contains(node.getToken().getAttribute()))
				variables.add(node.getToken().getAttribute());
			
			block.addDeclaration(node.getToken());
		}
		
		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeDeclaration(children.get(i), block);
	}
	
	
	/**
	 * Analyze Assignment Subtree
	 * @param node : Initially - root of the declaration subtree
	 * @param block : block object for current block
	 */
	private void analyzeAssignment(TreeNode node, Block block) {
		
		if(node.getToken() != null && node.getToken().getType() == TokenTypeEnum.ID) {
			if(!variables.contains(node.getToken().getAttribute()))
				variables.add(node.getToken().getAttribute());
			
			block.addReference(node.getToken());
		}
		
		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeAssignment(children.get(i), block);
	}

}
