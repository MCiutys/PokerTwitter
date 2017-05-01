/*
 * PokerManager.java
 * 
 */

package poker;

import java.util.ArrayList;
import java.util.List;

import twitter4j.User;

public class PokerManager {

	private static PokerManager pokerManager;
	private static int gameCounter;
	private List<GameOfPoker> pokerGames;

	private PokerManager() {
		gameCounter = 0;
		pokerGames = new ArrayList<GameOfPoker>();
	}

	public boolean processInput(User user, String input) {
		System.out.println("Input received!\nFrom: " + user.getName() + ", ID: " + user.getId() + "\nInput: " + input);
		String[] in = input.split(" ");

		switch (in[1]) {
		case "play":
			createNewGame(user);
			break;
		case "bet":
		case "fold":
		case "raise":
		case "discard":
			HumanPokerPlayer humanPokerPlayer = findPlayer(user.getId());
			if (humanPokerPlayer != null) {
				humanPokerPlayer.insertInput(in);
			}
			break;
		default:
			System.err.println("INVALID COMMAND");
		}

		return true;
	}
	
	private HumanPokerPlayer findPlayer(long userId) {
		for (GameOfPoker gameOfPoker : pokerGames) {
			HumanPokerPlayer humanPokerPlayer = gameOfPoker.findPlayer(userId);
			if (humanPokerPlayer != null) {
				return humanPokerPlayer;
			}
		}
		return null;
	}

	private void createNewGame(User user) {
		System.out.println("CREATE NEW GAME");
		//
		//
		//

		GameOfPoker gameOfPoker = new GameOfPoker(gameCounter);
		Thread thread = new Thread(gameOfPoker);
		pokerGames.add(gameOfPoker);

		gameOfPoker.addPlayer("Vadim");
		gameOfPoker.addPlayer("Maxim");
		gameOfPoker.addPlayer("Pavel");
		gameOfPoker.addPlayer(user);

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
