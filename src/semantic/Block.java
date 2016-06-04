package semantic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lexcial.TokenInfo;
import lexcial.TokenTypeEnum;


/**
 * Class to hold a Block's Variables Declarations
 * @author ND88
 *
 */
public class Block {
	
	int id;
	Block parent;
	LinkedHashMap<String, ArrayList<TokenInfo>> declarations;
	
	public Block(int id, Block parent) {
		this.id = id;
		this.parent = parent;
		declarations = new LinkedHashMap<String, ArrayList<TokenInfo>>();
	}
	
	public int getId() {
		return id;
	}
	
	public Block getParent() {
		return parent;
	}
	
	public LinkedHashMap<String, ArrayList<TokenInfo>> getDeclarations() {
		return declarations;
	}
	

	
	public void addDeclaration(TokenInfo token) {
		if(token.getType() != TokenTypeEnum.ID) {
			return;			
		}
		
		String name = token.getAttribute();
		
		if(declarations.get(name) == null)
			declarations.put(name, new ArrayList<TokenInfo>());
		
		declarations.get(name).add(token);
	}

}
