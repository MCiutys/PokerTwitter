
package poker;

import java.util.ArrayList;

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

			// print info
			// reset folded
			// Mantelio methods

			deckOfCards.reset();
			

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPlayer(String name) {
		addPlayer(name, 0);
	}

	// If userId == 0 then the player is bot.
	public void addPlayer(String name, long userId) {
		PokerPlayer pokerPlayer;
		if (userId == 0) {
			pokerPlayer = new AIPokerPlayer(deckOfCards, name);
		} else {
			pokerPlayer = new HumanPokerPlayer(deckOfCards, name, userId);
		}
		players.add(pokerPlayer);
	}

	public PokerPlayer removePlayer(PokerPlayer player) {
		return players.remove(players.indexOf(player));
	}

	public ArrayList<PokerPlayer> getPlayers() {
		return players;
	}

	public RoundOfPoker createNewRound() {
		round = new RoundOfPoker(players);
		return round;
	}

	public void playRound() {
		round.playRound();
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
		if (players.size() == 1) {
			isWinner = true;
			System.out.println("PLAYER WHO WON: " + players.get(0).getName());
		}
		return isWinner;
	}

}
