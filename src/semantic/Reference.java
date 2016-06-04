package semantic;

/**
 * Class to hold a Variable References
 * @author ND88
 *
 */
public class Reference {
	
	private String name, declaration;;
	private int line, block;
	
	public Reference(String name, int line, int block) {
		this.name = name;
		this.line = line;
		this.block = block;
		declaration = "undeclared";
	}
	
	public void setDeclarationLine(int line) {
		declaration = String.valueOf(line);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name + ":\n");
		builder.append("ref line: " + line + "\n");
		builder.append("ref block: " + block + "\n");
		builder.append("dec line: " + declaration + "\n\n");
		return builder.toString();
	}
	
	
	

}
