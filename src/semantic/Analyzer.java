package semantic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lexcial.TokenInfo;
import lexcial.TokenTypeEnum;
import syntax.LL1Exception;
import syntax.Parser;
import syntax.TreeNode;

public class Analyzer {

	//Filenames
	private final String CONFIG = "src/semantic/config.txt";

	//Identifiers
	private final String DECLARATION_ID = "DECLARATION";
	private final String ASSIGNMENT_ID = "ASSIGNMENT";
	private final String BLOCK_ID = "BLOCK";

	//Variables
	private Parser parser;
	private String input;
	private int blockIdCounter;
	private ArrayList<Block> blocks;
	private ArrayList<Reference> references;


	/**
	 * Constructor 
	 * @param inputFilename
	 */
	public Analyzer(String inputFilename) {
		input = inputFilename;
		parser = new Parser(CONFIG, input);

		blocks = new ArrayList<Block>();
		references = new ArrayList<Reference>();
	}

	
	/**
	 * Analyze Grammar Tree
	 * @return Analyzed
	 * @throws LL1Exception 
	 */
	public Analyzed analyze() throws LL1Exception {

		TreeNode root = parser.yyLL1parse();

		blockIdCounter = 0;
		analyzeTree(root);
		
		return new Analyzed(blocks, references);
	}


	
	
	
	
	/**
	 * Analyze Grammer tree returned from parser
	 * @param root : Grammar Tree's root node.
	 */
	private void analyzeTree(TreeNode root) {
		Block block = new Block(blockIdCounter++, null);
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
			block = new Block(blockIdCounter++, block);
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

			block.addDeclaration(node.getToken());
		}

		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeDeclaration(children.get(i), block);
	}


	/**
	 * Analyze Assignment Subtree
	 * @param node : Initially - root of the assignment subtree
	 * @param block : block object for current block
	 */
	private void analyzeAssignment(TreeNode node, Block block) {

		if(node.getToken() != null && node.getToken().getType() == TokenTypeEnum.ID) {

			//Create a new Reference object
			TokenInfo token = node.getToken();
			Reference ref = new Reference(token.getAttribute(), token.getLine(), block.getId());

			Block temp = block;
			
			//Find reference declaration line by travling up the blocks ancestry
			while(temp != null) {
				LinkedHashMap<String, ArrayList<TokenInfo>> declarations = temp.getDeclarations();

				if(declarations.containsKey(token.getAttribute())) {
					ArrayList<TokenInfo> tokens = declarations.get(token.getAttribute());
					ref.setDeclarationLine(tokens.get(0).getLine());
					break;
				}

				temp = temp.getParent();
			}

			references.add(ref);
		}

		ArrayList<TreeNode> children = node.getChildren();
		for(int i = children.size() - 1; i >= 0; i--)
			analyzeAssignment(children.get(i), block);
	}

}
