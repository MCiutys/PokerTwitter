/*
 * PokerManager.java
 * 
 */

package poker;

import java.util.ArrayList;
import java.util.List;

import poker.player.AIPokerPlayerNames;
import poker.player.HumanPokerPlayer;
import poker.twitter.TwitterBot;
import twitter4j.TwitterException;
import twitter4j.User;

public class PokerManager {

	private static PokerManager pokerManager;
	private static int gameCounter;
	private List<GameOfPoker> pokerGames;

	private PokerManager() {
		gameCounter = 0;
		pokerGames = new ArrayList<GameOfPoker>();
	}

	private boolean isNumber(String input) {
		return input.matches(Constants.NATURAL_NUMBER_REGEX);
	}

	public boolean processInput(User user, String input) throws TwitterException {
		System.out.println("Input received!\nFrom: " + user.getName() + ", ID: " + user.getId() + "\nInput: " + input);
		String[] in = input.split(" ");

		switch (in[1]) {
		case Constants.COMMAND_PLAY: {
			if (findPlayerPokerGame(user.getId()) == null) {
				GameOfPoker gameOfPoker = createNewGame(user);
				TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.GAME_CREATED + gameOfPoker.getGameId() + "\n" + Constants.YOUR_TWEET + input);
				if (in.length == 3 && in[2].equalsIgnoreCase(Constants.COMMAND_START)) {
					gameOfPoker.startGame();
					TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.GAME_STARTED + "\n" + Constants.YOUR_TWEET + input);
				}
			} else {
				TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.ALREADY_IN_A_GAME + "\n" + Constants.YOUR_TWEET + input);
			}
		} break;
		case Constants.COMMAND_START: {
			GameOfPoker gameOfPoker = findPlayerPokerGame(user.getId());
			if (gameOfPoker != null) {
				if (gameOfPoker.startGame()) {
					TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.GAME_STARTED + "\n" + Constants.YOUR_TWEET + input);
				} else {
					TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.GAME_ALREADY_STARTED + "\n" + Constants.YOUR_TWEET + input);
				}
			} else {
				TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.NOT_PART_OF_ROOM + "\n" + Constants.YOUR_TWEET + input);
			}
		} break;
		case Constants.COMMAND_JOIN: {
			if (isNumber(in[2])) {
				int gameId = Integer.parseInt(in[2]);
				findPokerGame(gameId).addPlayer(user);
				TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.JOINED_ROOM + "\n" + Constants.YOUR_TWEET + input);
			} else {
				TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.INCORRECT_JOIN_NUMBER + "\n" + Constants.YOUR_TWEET + input);
			}
		} break;
		case Constants.COMMAND_BET:
		case Constants.COMMAND_FOLD:
		case Constants.COMMAND_CALL:
		case Constants.COMMAND_RAISE:
		case Constants.COMMAND_DISCARD:
			HumanPokerPlayer humanPokerPlayer = findPlayer(user.getId());
			if (humanPokerPlayer != null) {
				humanPokerPlayer.insertInput(in);
			}
			break;
		default:
			TwitterBot.getAPI().sendDirectMessage(user.getId(), Constants.UNKNOWN_COMMAND + "\n" + Constants.YOUR_TWEET + input);
		}

		return true;
	}

	private GameOfPoker findPlayerPokerGame(long userId) {
		for (GameOfPoker gameOfPoker : pokerGames) {
			HumanPokerPlayer humanPokerPlayer = gameOfPoker.getPlayer(userId);
			if (humanPokerPlayer != null) {
				return gameOfPoker;
			}
		}
		return null;
	}

	private GameOfPoker findPokerGame(int gameId) {
		for (GameOfPoker gameOfPoker : pokerGames) {
			if (gameOfPoker.getGameId() == gameId) {
				return gameOfPoker;
			}
		}
		return null;
	}

	private HumanPokerPlayer findPlayer(long userId) {
		for (GameOfPoker gameOfPoker : pokerGames) {
			HumanPokerPlayer humanPokerPlayer = gameOfPoker.getPlayer(userId);
			if (humanPokerPlayer != null) {
				return humanPokerPlayer;
			}
		}
		return null;
	}

	private GameOfPoker createNewGame(User user) {
		System.out.println("CREATE NEW GAME");
		GameOfPoker gameOfPoker = new GameOfPoker(gameCounter);
		Thread thread = new Thread(gameOfPoker);
		pokerGames.add(gameOfPoker);

		String[] botNames = AIPokerPlayerNames.getInstance().getNames(Constants.MAX_PLAYERS - 1);

		for (int i = 0; i < Constants.MAX_PLAYERS - 1; i++) {
			gameOfPoker.addPlayer(botNames[i]);
		}

		gameOfPoker.addPlayer(user);
		thread.start();
		return gameOfPoker;
	}

	public static PokerManager getInstance() {
		if (pokerManager == null) {
			pokerManager = new PokerManager();
		}
		return pokerManager;
	}

}