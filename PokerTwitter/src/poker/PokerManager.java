/*
 * PokerManager.java
 * 
 */

package poker;

public class PokerManager {

	private static PokerManager pokerManager;

	private PokerManager() {}

	public boolean processInput(String input) {
		System.out.println("process input: " + input);
		return false;
	}

	public static PokerManager getInstance() {
		if (pokerManager == null) {
			pokerManager = new PokerManager();
		}
		return pokerManager;
	}

}
