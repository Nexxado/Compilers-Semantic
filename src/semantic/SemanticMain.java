package semantic;

import syntax.GraphNode;
import syntax.TreeNode;

public class SemanticMain {
	
	public static void main(String[] args) {
		
		if(args == null || args.length < 1 || args[0] == null) {
			System.err.println("[ERROR] Must pass input file");
			return;
		}
		
		String inputFilename = args[0];
		
		Analyzer analyzer = new Analyzer(inputFilename);

		System.out.println(generateGraph(analyzer.analyze()));
	}
	
	
	/*************************/
	/***** DEBUG METHODS *****/
	/*************************/
	//TODO Delete once done.
	
	/**
	 * @param root : TreeNode representing the Parse Tree's root node
	 * @return A String representing the parse tree as a graph, according to http://www.webgraphviz.com/
	 */
	private static String generateGraph(TreeNode root) {
		if(root == null)
			return null;
		
		StringBuilder builder = new StringBuilder();
		builder.append("digraph G {" + System.lineSeparator());
		generateTree(root, builder);
		
		builder.append("}");
		return builder.toString();
	}

	
	private static void generateTree(TreeNode node, StringBuilder builder) {
		if(node == null)
			return;
			
//		System.err.println(node);
		for(int i = node.getChildren().size() - 1; i >= 0; i--) {
			String childName = node.getChildren().get(i).toString();
			builder.append(new GraphNode(node.toString(), childName) + System.lineSeparator());
		}
			
		for(int i = 0; i < node.getChildren().size(); i++) {
			generateTree(node.getChildren().get(i), builder);
		}
	}

}
