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

	// Method which discards up to 3 cards from the hand.
	public int discard() {
		int counter = 0;
		for (int i = 0; i < HandOfCards.HAND_SIZE; i++)  {
			int discardProbability = hand.getDiscardProbability(i);
			// Generate random number between 1 and 99.
			int random = (int) (Math.random() * HandOfCards.PROBABILITY_MAXIMUM - 1) + 1;
			// If generated random number is in the range between 0 and discardProbability, discard the card.
			if (random <= discardProbability && counter < MAX_CARDS_TO_DISCARD) {
				hand.replaceCard(i);
				counter++;
			}
		}
		return counter;
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
