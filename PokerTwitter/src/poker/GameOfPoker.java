
package poker;

import java.util.ArrayList;
import java.util.List;

import poker.player.AIPokerPlayer;
import poker.player.HumanPokerPlayer;
import poker.player.PokerPlayer;
import poker.twitter.TwitterBot;
import twitter4j.User;

public class GameOfPoker implements Runnable {
	private ArrayList<PokerPlayer> players;
	private RoundOfPoker round;
	private int gameId;
	private DeckOfCards deckOfCards;
	private volatile boolean started;

	public GameOfPoker(int gameId) {
		players = new ArrayList<PokerPlayer>();
		round = createNewRound();
		deckOfCards = new DeckOfCards();
		started = false;
	}

	@Override
	public void run() {
		// Wait till started.
		while (!started) {
			try {
				Thread.sleep(Constants.THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while (!isWinner()) {
			round = createNewRound();
			round.playRound();
			resetFolded();
			cleanHandsForPlayers();
			deckOfCards.reset();
			try {
				Thread.sleep(Constants.THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean startGame() {
		if (!started) {
			started = !started;
			return started;
		}
		return false;
	}

	// AI Player
	public void addPlayer(String name) {
		players.add(new AIPokerPlayer(deckOfCards, name));
	}

	// Human player
	public void addPlayer(User user) {
		players.add(new HumanPokerPlayer(deckOfCards, user));
	}

	public PokerPlayer removePlayer(PokerPlayer player) {
		return players.remove(players.indexOf(player));
	}

	public ArrayList<PokerPlayer> getPlayers() {
		return players;
	}

	public List<HumanPokerPlayer> getHumanPlayers() {
		List<HumanPokerPlayer> humanPokerPlayers = new ArrayList<HumanPokerPlayer>();
		for (PokerPlayer pokerPlayer : players) {
			if (pokerPlayer instanceof HumanPokerPlayer) {
				humanPokerPlayers.add((HumanPokerPlayer) pokerPlayer);
			}
		}
		return humanPokerPlayers;
	}

	public HumanPokerPlayer getPlayer(long userId) {
		for (HumanPokerPlayer humanPokerPlayer : getHumanPlayers()) {
			if (humanPokerPlayer.getUser().getId() == userId) {
				return humanPokerPlayer;
			}
		}
		return null;
	}

	public RoundOfPoker createNewRound() {
		round = new RoundOfPoker(players, deckOfCards);
		return round;
	}

	public int getGameId() {
		return gameId;
	}

	public void resetFolded() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setFolded(false);
		}
	}

	private boolean isWinner() {
		boolean isWinner = false;
		System.out.println("Amount of players: " + players.size());
		if (players.size() == 1) {
			isWinner = true;
			PokerPlayer pokerPlayer = players.get(0);
			TwitterBot.updateStatus(pokerPlayer.getName() + Constants.WINNER + Constants.NEW_LINE + "@" + pokerPlayer.getName());
		}
		return isWinner;
	}

	private void cleanHandsForPlayers() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).cleanHand();
		}
	}

}
