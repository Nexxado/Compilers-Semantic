package semantic;

import java.util.ArrayList;

public class Analyzed {
	
	ArrayList<Block> blocks;
	ArrayList<Reference> references;
	
	public Analyzed(ArrayList<Block> blocks, ArrayList<Reference> references) {
		this.blocks = new ArrayList<Block>(blocks);
		this.references = new ArrayList<Reference>(references);
	}
	
	
	public ArrayList<Block> getBlocks() {
		return blocks;
	}
	
	public ArrayList<Reference> getReferences() {
		return references;
	}

}
