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

	// Play the round
	public void playRound() {
		// Tell how many chips each player has
		for (int i = 0; i < players.size(); i++) {
			// printing players.get(i).getFunds();
		}
		// Deal cards for players
		for (int i = 0; i < players.size() * HandOfCards.HAND_SIZE; i++) {
			players.get(i % players.size()).addCard(deck.dealNext());
		}
		
		// Discarding cards
		for (int i = 0; i < players.size(); i++) {
			players.get(i).discard();
		}
		
		// Ask to fold
		for (int i = 0; i < players.size(); i++) {
			// Check if a player wants to fold
			if (true) {
				players.remove(i);
			}
		}
		
		
	}
}
