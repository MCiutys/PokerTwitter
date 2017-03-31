/*
 * A class that represents a poker player that will be used in a poker game.
 * This class contains methods only one method which discards cards from a HandOfCards
 * in hopes to improve it to win the game.
 * Logic is mainly based on randomness unless the probability is either 0 or 100.
 *
 * @author Edgaras Lagunovas (15204377)
 */

package poker;

public class PokerPlayer {

	private static final int MAX_CARDS_TO_DISCARD = 3;

	private HandOfCards hand;

	public PokerPlayer(DeckOfCards deck) {
		hand = new HandOfCards(deck);
	}

	// TODO 
	public int discard() {

		return 0;
	}

	// Should not be here, used only for testing in the Main class.
	public int getGameValue() {
		return hand.getGameValue();
	}

	@Override
	public String toString() {
		return "Poker player's hand: " + hand + ", Game value: " + hand.getGameValue();
	}

}
