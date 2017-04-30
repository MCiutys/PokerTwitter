/*
 * PokerManager.java
 * 
 */

package poker;

import java.util.HashMap;
import java.util.Map;

import twitter4j.User;

public class PokerManager {

	private static PokerManager pokerManager;
	private static int gameCounter;
	private Map<Thread, GameOfPoker> pokerGames;

	private PokerManager() {
		gameCounter = 0;
		pokerGames = new HashMap<Thread, GameOfPoker>();
	}

	public boolean processInput(User user, String input) {
		System.out.println("Input received!\nFrom: " + user.getName() + ", ID: " + user.getId() + "\nInput: " + input);
		String[] in = input.split(" ");

		switch (in[1]) {
		case "play":
			createNewGame(user);
			break;
		default:
			System.err.println("INVALID COMMAND");
		}

		return true;
	}

	private void createNewGame(User user) {
		System.out.println("CREATE NEW GAME");
		//
		//
		//

		GameOfPoker gameOfPoker = new GameOfPoker(gameCounter);
		Thread thread = new Thread(gameOfPoker);
		pokerGames.put(thread, gameOfPoker);

		gameOfPoker.addPlayer("Vadim");
		gameOfPoker.addPlayer("Maxim");
		gameOfPoker.addPlayer("Pavel");
		gameOfPoker.addPlayer(user.getName(), user.getId());

		//
		//
		//
		thread.start();
	}

	public static PokerManager getInstance() {
		if (pokerManager == null) {
			pokerManager = new PokerManager();
		}
		return pokerManager;
	}

}
