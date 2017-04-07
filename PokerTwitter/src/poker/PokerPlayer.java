/*
 * A class that represents a poker player that will be used in a poker game.
 * This class contains only one method which discards cards from a HandOfCards
 * in hopes to improve it to win the game.
 * Logic is mainly based on randomness unless the probability is either 0 or 100.
 *
 */

package poker;

public abstract class PokerPlayer {
	public static final int MAX_CARDS_TO_DISCARD = 3;
	protected HandOfCards hand;
	protected String name;

	public PokerPlayer(DeckOfCards deck, String mName) {
		hand = new HandOfCards(deck);
		name = mName;
	}

	// List of abstract methods for subclasses to implement
	/**
	 * Discards set of cards from the hand
	 * 
	 * @return The number of cards discarded from the hand
	 */
	public abstract int discard();

	/**
	 *
	 * @return
	 */
	public abstract int bet();

	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HandOfCards getHand() {
		return hand;
	}

	@Override
	/**
	 * String representation of the Player. Consists of players name, hand and
	 * the gamevalue of the hand.
	 */
	public String toString() {
		return "Poker player " + name + "'s" + "hand: " + hand + ", Game value: " + hand.getGameValue();
	}

}
