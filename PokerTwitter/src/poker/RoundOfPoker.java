package poker;

import java.util.ArrayList;

public class RoundOfPoker {

	private int pot;
	private ArrayList<PokerPlayer> players;
	private DeckOfCards deck;

	public RoundOfPoker(ArrayList<PokerPlayer> players) {
		pot = 0;
		players = new ArrayList<PokerPlayer>();
		deck = new DeckOfCards();
	}

	// Increase pot by getting player's bet
	private void increasePot(PokerPlayer player) {
		pot += player.bet();
	}

	// Deal one card for a given player
	private void dealCard(PokerPlayer player) {
		player.addCard(deck.dealNext());
	}

	// Play the game
	public void play() {
		// Deal cards for players
		for (int i = 0; i < players.size() * HandOfCards.HAND_SIZE; i++) {
			dealCard(players.get(i % players.size()));
		}
	}
}
