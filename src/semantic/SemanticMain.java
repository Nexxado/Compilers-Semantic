package semantic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import syntax.LL1Exception;
import lexcial.TokenInfo;

public class SemanticMain {
	
	public static void main(String[] args) {
		
		if(args == null || args.length < 1 || args[0] == null) {
			System.err.println("[ERROR] Must pass input file");
			return;
		}
		
		String inputFilename = args[0];
		
		try {
			
			Analyzed analyzed = new Analyzer(inputFilename).analyze();

			//Generate redeclaration list and reference list
			StringBuilder builder = new StringBuilder();
			builder.append(buildRedeclarationsList(analyzed.getBlocks()));
			builder.append(buildReferencesList(analyzed.getReferences()));
		
			//Write redeclaration list and reference list to output file
			String outputFilename = inputFilename.substring(0, inputFilename.lastIndexOf('.')) + ".sem";
			File outputFile = new File(outputFilename);
			
			FileWriter writer = new FileWriter(outputFile);
			writer.write(builder.toString());
			writer.close();

		} catch (IOException e) {
			System.err.println("[ERROR] Failed writing to output file: " + e.toString());
			return;
		} catch (LL1Exception e) {
			System.err.println("Syntax Mismatch");
			return;
		}
		
	}
	
	
	
	/************************************/
	/***** Build Redeclaration List *****/
	/************************************/
	private static String buildRedeclarationsList(ArrayList<Block> blocks) {
		
		StringBuilder redeclareBuilder = new StringBuilder();
		redeclareBuilder.append("redeclarations\n\n");
		int noRedeclareCounter = 0;


		for(int i = 0; i < blocks.size(); i++) {

			Block block = blocks.get(i);

			redeclareBuilder.append("block: " + block.getId() + "\n");
			LinkedHashMap<String, ArrayList<TokenInfo>> declarations = block.getDeclarations();
			Iterator<Entry<String, ArrayList<TokenInfo>>> it = declarations.entrySet().iterator();
			int blockNoRedeclareCounter = 0;

			//Iterate over block's declarations to check for redeclarations
			while(it.hasNext()) {

				Map.Entry<String, ArrayList<TokenInfo>> entry = it.next();
				ArrayList<TokenInfo> tokens = entry.getValue();

				//Check if variable was only declared once.
				if(tokens.size() <= 1) {
					blockNoRedeclareCounter++;
					continue;
				}

				//Add redeclaration entry
				redeclareBuilder.append(entry.getKey() + ": ");
				for(int j = 0; j < tokens.size(); j++) {

					redeclareBuilder.append(tokens.get(j).getLine());

					if(j != tokens.size() - 1)
						redeclareBuilder.append(", ");
				}
				redeclareBuilder.append("\n");
			}

			//If block has no redeclarations, delete block redeclaration entry
			if(blockNoRedeclareCounter == block.getDeclarations().size()) {
				noRedeclareCounter++;
				redeclareBuilder.delete(redeclareBuilder.lastIndexOf("\n"), redeclareBuilder.length());
				redeclareBuilder.delete(redeclareBuilder.lastIndexOf("\n"), redeclareBuilder.length());
			}
			redeclareBuilder.append("\n");
		}

		//If all blocks have no redeclaration entry, add "no redeclarations" title.
		if(noRedeclareCounter == blocks.size())
			redeclareBuilder.replace(0, redeclareBuilder.length(), "no redeclarations\n\n");

		return redeclareBuilder.toString();
	}
	
	
	/********************************/
	/***** Build Reference List *****/
	/********************************/
	private static String buildReferencesList(ArrayList<Reference> references) {
		
		StringBuilder referenceBuilder = new StringBuilder();
		
		if(references.isEmpty()) 
			referenceBuilder.append("no references\n\n");
		else {
		
			referenceBuilder.append("references\n\n");
			for(int i = 0; i < references.size(); i++) {
				referenceBuilder.append(references.get(i) + "\n");
			}
		
		}
		
		return referenceBuilder.toString();
	}
}
