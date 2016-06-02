package syntax;

import java.util.ArrayList;

import lexcial.TokenInfo;

public class TreeNode {

	private String name;
	private ArrayList<TreeNode> children;
	private TokenInfo token;
	
	public TreeNode(String name) {
		this.name = name;
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
		return name;
	}
	
	public void setToken(TokenInfo token) {
		this.token = token;
	}
	
	public TokenInfo getToken() {
		return token;
	}

}
