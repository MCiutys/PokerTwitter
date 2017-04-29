/*
 * A class that represents a poker player that will be used in a poker game.
 * This class contains only one method which discards cards from a HandOfCards
 * in hopes to improve it to win the game.
 * Logic is mainly based on randomness unless the probability is either 0 or 100.
 *
 */

package poker;

public abstract class PokerPlayer {
	// Constants
	public static final int MAX_CARDS_TO_DISCARD = 3;
	public static final int STARTING_FUNDS = 1000;

	// Betting Constants
	public static final int BET_ERROR = -2;
	public static final int BET_FOLD = -1;

	// Variables
	protected DeckOfCards deck;
	protected HandOfCards hand;
	protected String name;
	protected int funds;

	// Methods
	public PokerPlayer(DeckOfCards mDeck, String mName) {
		deck = mDeck;
		hand = new HandOfCards(deck);
		name = mName;
		funds = STARTING_FUNDS;
	}

	// List of abstract methods for subclasses to implement
	/**
	 * Discards a set of playing cards from the hand and replaces them with
	 * another set of playing cards.
	 * 
	 * @return The number of cards discarded from the hand.
	 */
	public abstract int discard();

	/**
	 * Calculates the amount of money the player is going to bet. All return
	 * values less than or equal to 0 indicate the players wish to fold, else
	 * the return value will be greater or equal to the callBet.
	 * 
	 * @return The amount of money player is going to bet.
	 */
	public abstract int bet(int callBet);

	// Default methods for subclasses
	/**
	 * A player can only start the betting if and only if their hand is a one
	 * pair or better.
	 * 
	 * @return
	 */
	public boolean canStartBetting() {
		return hand.getGameValue() > HandOfCards.ONE_PAIR_DEFAULT;
	}

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

	// Give a card for a player
	public boolean addCard() {
		return hand.addCard(deck.dealNext());
	}

	public int getFunds() {
		return funds;
	}

	public void addToFunds(int winningPot) {
		funds += winningPot;
	}

	@Override
	/**
	 * String representation of the Player. Consists of players name, hand and
	 * the gamevalue of the hand.
	 */
	public String toString() {
		try {
			return "Poker player " + name + "'s hand: " + hand + ", Game value: " + hand.getGameValue();
		} catch (NullPointerException e) {
			// Occurs when hand has null cards present
			return "Poker player " + name;
		}
	}

}
