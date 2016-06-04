package semantic;

import java.util.ArrayList;

import lexcial.TokenTypeEnum;
import syntax.Parser;
import syntax.TreeNode;

public class Analyzer {
	
	//Filenames
	private String input;
	private final String CONFIG = "src/semantic/config.txt";
	
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
//		return root;

		analyzeTree(root);
		System.out.println("Done Analying Tree"); //TODO DEBUG
		
		
		return null;
	}
	
	
	private void analyzeTree(TreeNode root) {
		Block block = new Block(0, null);
		blocks.add(block);
		analyzeBlock(root, block);
	}
	
	private void analyzeBlock(TreeNode node, Block block) {
		
		if(node == null)
			return;
		
		Block newBlock = null;
		
		if(node.getName().equals(BLOCK_ID)) {
			newBlock = new Block(block.getId() + 1, block);
			blocks.add(newBlock);
		}
		
		else if(node.getName().equals(DECLARATION_ID)) {
			analyzeDeclaration(node, block);
		}
		
		else if(node.getName().equals(ASSIGNMENT_ID)) {
			analyzeAssignment(node, block);
		}
		
		ArrayList<TreeNode> children = node.getChildren();
		if(newBlock == null)
			for(int i = children.size() - 1; i >= 0; i--)
				analyzeBlock(children.get(i), block);
		else
			for(int i = children.size() - 1; i >= 0; i--)
				analyzeBlock(children.get(i), newBlock);
		
	}
	
	
	private void analyzeDeclaration(TreeNode node, Block block) {
		
		if(node == null)
			return;
		
		else if(node.getToken() != null && node.getToken().getType() == TokenTypeEnum.ID) {
			if(!variables.contains(node.getToken().getAttribute()))
				variables.add(node.getToken().getAttribute());
			
			block.addDeclaration(node.getToken());
		}
		
		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeDeclaration(children.get(i), block);
	}
	
	private void analyzeAssignment(TreeNode node, Block block) {
		
		if(node == null)
			return;
		
		else if(node.getToken() != null && node.getToken().getType() == TokenTypeEnum.ID) {
			if(!variables.contains(node.getToken().getAttribute()))
				variables.add(node.getToken().getAttribute());
			
			block.addReference(node.getToken());
		}
		
		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeAssignment(children.get(i), block);
	}

}
