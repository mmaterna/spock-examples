package starter.spock;

public class AddingMachine {

	public int add(int a, int b) {
		return a + b;
	}
	
	public int multiply(int a, int b ) {
		return a * b;
	}
	
	public boolean same(int a, int b) {
		return a == b;
	}
	
	public void execute(int a, String command) {
		// no op
	}
}
