/*
 * A class that represents a deck of cards that will be used in a poker game.
 * This class contains methods which simulate dealing a card, returning card to a deck,
 * and shuffling the deck.
 * 
 * Cards are being dealt from last position of the array which would be the top card of the deck.
 *
 */

package poker;

import java.util.Random;

public class DeckOfCards {

	public static final int NUMBER_OF_CARDS = 52;
	private static final char[] SUITS = {PlayingCard.HEARTS, PlayingCard.SPADES, PlayingCard.DIAMONDS, PlayingCard.CLUBS};
	public static final int NUMBER_OF_SUITS = SUITS.length;

	private PlayingCard[] cards;
	// Keeps track of next card position that will be dealt.
	private int pointer;
	// Keeps track of position where next returned card will be stored.
	private int returnPointer;

	public DeckOfCards() {
		reset();
	}

	// Resets deck by reinitializing array and shuffling it.
	public void reset() {
		cards = new PlayingCard[NUMBER_OF_CARDS];

		int arrayCounter = 0;

		// Loop through 4 different suits.
		for (char suit : SUITS) {
			// Loop to create the cards for each suit. 52 / 4 = 13 cards in each suit.
			for (int card = 1; card <= NUMBER_OF_CARDS / SUITS.length; card++) {
				String cardType = "";
				// Decide the card type based on the value that variable 'card' holds.
				switch (card) {
				case PlayingCard.ACE_FACE_VALUE:
					cardType = PlayingCard.ACE;
					break;
				case PlayingCard.JACK_VALUE:
					cardType = PlayingCard.JACK;
					break;
				case PlayingCard.QUEEN_VALUE:
					cardType = PlayingCard.QUEEN;
					break;
				case PlayingCard.KING_VALUE:
					cardType = PlayingCard.KING;
					break;
				default:
					cardType = String.valueOf(card);
				}

				cards[arrayCounter++] = new PlayingCard(cardType, suit, card, (card == PlayingCard.ACE_FACE_VALUE) ? PlayingCard.ACE_GAME_VALUE : card);
			}
		}
		shuffle();
	}

	// Shuffles cards contained in the deck.
	public synchronized void shuffle() {
		Random random = new Random();
		for (int i = 0; i <= Math.round(Math.pow(NUMBER_OF_CARDS, 2)); i++) {
			// Generate two different positions.
			int locationOne = random.nextInt(NUMBER_OF_CARDS);
			int locationTwo;

			do {
				locationTwo = random.nextInt(NUMBER_OF_CARDS);
			} while (locationOne == locationTwo);

			// Swap cards contained in these positions.
			PlayingCard temporary = cards[locationOne];
			cards[locationOne] = cards[locationTwo];
			cards[locationTwo] = temporary;
		}
		// Reset pointers.
		pointer = returnPointer = NUMBER_OF_CARDS;
	}

	/*
	 * Deals a card if there are cards that were not dealt before.
	 * Returns null if all cards were dealt.
	 */
	public synchronized PlayingCard dealNext() {
		PlayingCard returnCard = null;
		if (pointer > 0) {
			returnCard = cards[--pointer];
			cards[pointer] = null;
		}
		return returnCard;
	}

	// Puts back a card into the deck.
	public synchronized boolean returnCard(PlayingCard discarded) {
		// If pointer is less than returnPointer it means that we have an empty
		// space which we can use to put discarded card back into the deck.
		if (discarded != null) {
			if (pointer < returnPointer) {
				cards[--returnPointer] = discarded;
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String string = "";
		for (PlayingCard card : cards) {
			string += card + " ";
		}
		return string;
	}

}