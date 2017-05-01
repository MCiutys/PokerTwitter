
package poker;

import java.util.ArrayList;

import twitter4j.User;

public class GameOfPoker implements Runnable {
	private ArrayList<PokerPlayer> players;
	private RoundOfPoker round;
	private int gameId;
	private DeckOfCards deckOfCards;

	public GameOfPoker(int gameId) {
		players = new ArrayList<PokerPlayer>();
		round = createNewRound();
		deckOfCards = new DeckOfCards();
	}

	@Override
	public void run() {
		while (!isWinner()) {
			round = createNewRound();
			playRound();
			resetFolded();
			cleanHandsForPlayers();
			deckOfCards.reset();


			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

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

	public HumanPokerPlayer findPlayer(long userId) {
		for (PokerPlayer pokerPlayer : players) {
			if (pokerPlayer instanceof HumanPokerPlayer) {
				HumanPokerPlayer humanPokerPlayer = (HumanPokerPlayer) pokerPlayer;
				if (humanPokerPlayer.getUser().getId() == userId) {
					return humanPokerPlayer;
				}
			}
		}
		return null;
	}

	public void playRound() {
		round.playRound();
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
			System.out.println("PLAYER WHO WON: " + players.get(0).getName());
		}
		return isWinner;
	}

	private void cleanHandsForPlayers() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).cleanHand();
		}
	}

}
