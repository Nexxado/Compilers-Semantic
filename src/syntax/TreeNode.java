package syntax;

import java.util.ArrayList;

import lexcial.TokenInfo;

public class TreeNode {

	private String name;
	private int id;
	private ArrayList<TreeNode> children;
	private TokenInfo token;
	
	public TreeNode(String name, int id) {
		this.name = name;
		this.id = id;
		children = new ArrayList<TreeNode>();
		token = null;
	}
	
	public void addChild(TreeNode child) {
		children.add(child);
	}
	
	public ArrayList<TreeNode> getChildren() {
		return children;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		if(token == null)
			return name + "_" + id;
		
		return "\"" + token.toString() + "_" + id + "\"";
	}
	
	public void setToken(TokenInfo token) {
		this.token = token;
	}
	
	public TokenInfo getToken() {
		return token;
	}

}
