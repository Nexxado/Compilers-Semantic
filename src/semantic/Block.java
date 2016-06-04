package semantic;

import java.util.ArrayList;
import java.util.HashMap;

import lexcial.TokenInfo;
import lexcial.TokenTypeEnum;

public class Block {
	
	int id;
	Block parent;
	HashMap<String, ArrayList<TokenInfo>> declarations;
	HashMap<String, ArrayList<TokenInfo>> references;
	
	public Block(int id, Block parent) {
		this.id = id;
		this.parent = parent;
		declarations = new HashMap<String, ArrayList<TokenInfo>>();
		references = new HashMap<String, ArrayList<TokenInfo>>();
	}
	
	public int getId() {
		return id;
	}
	
	public Block getParent() {
		return parent;
	}
	
	public HashMap<String, ArrayList<TokenInfo>> getReferences() {
		return references;
	}
	
	public HashMap<String, ArrayList<TokenInfo>> getDeclarations() {
		return declarations;
	}
	
	
	public void addReference(TokenInfo token) {
		if(token.getType() != TokenTypeEnum.ID) {
			System.err.println("Trying to addReference of non-ID"); //TODO DEBUG
			return;			
		}
		
		String name = token.getAttribute();
		
		if(references.get(name) == null)
			references.put(name, new ArrayList<TokenInfo>());
			
		references.get(name).add(token);
	}
	
	public void addDeclaration(TokenInfo token) {
		if(token.getType() != TokenTypeEnum.ID) {
			System.err.println("Trying to addDeclaration of non-ID"); //TODO DEBUG
			return;			
		}
		
		String name = token.getAttribute();
		
		if(declarations.get(name) == null)
			declarations.put(name, new ArrayList<TokenInfo>());
		
		declarations.get(name).add(token);
	}

}
