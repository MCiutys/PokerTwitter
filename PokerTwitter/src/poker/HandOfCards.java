/*
 * A class that represents hand of cards.
 * It contains an instance of a deck and array of size 5 which holds instanced of PlayingCard.
 *
 */

package poker;

import java.util.HashMap;
import java.util.Map;

public class HandOfCards {

	public static final int RATE_OF_CHANGE = 100000000;

	// Hand values.
	public static final int ROYAL_FLUSH_DEFAULT = 1000000000; 									// 1000M
	public static final int STRAIGHT_FLUSH_DEFAULT = ROYAL_FLUSH_DEFAULT - RATE_OF_CHANGE; 		// 900M
	public static final int FOUR_OF_A_KIND_DEFAULT = STRAIGHT_FLUSH_DEFAULT - RATE_OF_CHANGE; 	// 800M
	public static final int FULL_HOUSE_DEFAULT = FOUR_OF_A_KIND_DEFAULT - RATE_OF_CHANGE; 		// 700M
	public static final int FLUSH_DEFAULT = FULL_HOUSE_DEFAULT - RATE_OF_CHANGE; 				// 600M
	public static final int STRAIGHT_DEFAULT = FLUSH_DEFAULT - RATE_OF_CHANGE; 					// 500M
	public static final int THREE_OF_A_KIND_DEFAULT = STRAIGHT_DEFAULT - RATE_OF_CHANGE; 		// 400M
	public static final int TWO_PAIRS_DEFAULT = THREE_OF_A_KIND_DEFAULT - RATE_OF_CHANGE; 		// 300M
	public static final int ONE_PAIR_DEFAULT = TWO_PAIRS_DEFAULT - RATE_OF_CHANGE; 				// 200M
	private static final int POWER_OF_DEFAULT = 6;

	// Probability min/max.
	public static final float PROBABILITY_MINIMUM = 0;
	public static final float PROBABILITY_MAXIMUM = 100;

	// MISC.
	public static final int INVALID_POSITION = -1;
	public static final int HAND_SIZE = 5;
	
	private PlayingCard[] hand;
	private DeckOfCards deck;

	public HandOfCards(DeckOfCards deck) {
		this.deck = deck;
		hand = new PlayingCard[HAND_SIZE];
		/*for (int i = 0; i < HAND_SIZE; i++) {
			hand[i] = deck.dealNext();
		}
		sort();
		*/
	}
	
	public boolean addCard(PlayingCard card) {
		boolean anyNull = false, addedCard = false;
		
		// Replace the first null card with new card and determine
		// if any null card are left in the hand.
		if (card != null) {
			for (int i = 0; i < HAND_SIZE && !anyNull; i++) {
				if (hand[i] == null) {
					// If its the first null card encountered
					if (!addedCard) {
						hand[i] = card;
						addedCard = true;
					} else {
						anyNull = true;
					}
				}
			}
		}
		
		// If there are no null cards in the hand then sort
		if (!anyNull)
			sort();
		
		return addedCard;
	}

	// Replaces card at given index.
	// Return true or false depending if card was successfully replaced.
	public boolean replaceCard(int index) {
		// Check if index is in bounds of the array and check that new card is not null.
		if (index >= 0 && index < HAND_SIZE) {
			PlayingCard newCard = deck.dealNext();
			// Check if not null (if there are cards left in a deck)
			if (newCard != null) {
				// Check if card was successfully returned.
				if (deck.returnCard(hand[index])) {
					// Swap old card with new one.
					hand[index] = deck.dealNext();
					sort();
					return true;
				}
			}
		}
		return false;
	}

	// Returns probability of card to be discarded.
	public int getDiscardProbability(int cardPosition) {
		if (cardPosition < 0 || cardPosition > HAND_SIZE - 1) return (int) PROBABILITY_MINIMUM - 1;
		if (isRoyalFlush()) return (int) PROBABILITY_MINIMUM; // Cannot be improved.
		if (isStraightFlush()) return straightFlushImprove(cardPosition); // Upgrade to royal flush or higher straight flush.
		if (isFourOfAKind()) return quadsImprove(cardPosition); // Upgrade kicker card.
		if (isFullHouse()) return fullHouseImprove(cardPosition); // Upgrade to four of a kind or higher full house.
		if (isFlush()) return flushImprove(cardPosition); // Upgrade to a higher flush.
		if (isStraight()) return straightImprove(cardPosition); // Upgrade to a higher straight flush.
		if (isThreeOfAKind()) return threeOfAKindImprove(cardPosition); // Upgrade to either four of a kind or a full house.
		if (isTwoPair()) return twoPairImprove(cardPosition); // Upgrade to a full house.
		if (isBrokenHand()) return brokenHandImprove(cardPosition); // Upgrades high hand or one pair to royal flush, straight flush, flush or straight. Must be checked before isOnePair.
		if (isOnePair()) return onePairImprove(cardPosition); // Upgrade to either three of a kind or two pair.

		// At this point hand must be a high hand.
		// Higher card chosen = less probability.
		return Math.round((PROBABILITY_MAXIMUM
				- (PROBABILITY_MAXIMUM * ((float) (hand[cardPosition].getGameValue() - PlayingCard.GAME_VALUE_OFFSET)
						/ ((float) PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)))));
	}

	// Return probability of a card that may improve broken hand if discarded.
	private int brokenHandImprove(int cardPosition) {
		boolean isOnePair = isOnePair();

		float neededCardProbability = (1f / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE)) * PROBABILITY_MAXIMUM;
		int overallProbability = Math.round(((PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * ((float) hand[cardPosition].getGameValue()
				/ ((float) PlayingCard.ACE_GAME_VALUE + PlayingCard.GAME_VALUE_OFFSET))))) / neededCardProbability);

		int brokenHandKickerPosition = getBrokenRoyalFlushPosition();
		if (brokenHandKickerPosition != INVALID_POSITION) {
			return (int) ((brokenHandKickerPosition == cardPosition) ? overallProbability : PROBABILITY_MINIMUM);
		}

		brokenHandKickerPosition = getBrokenStraightFlushPosition();
		if (brokenHandKickerPosition != INVALID_POSITION) {
			return (int) ((brokenHandKickerPosition == cardPosition) ? overallProbability : PROBABILITY_MINIMUM);
		}

		brokenHandKickerPosition = getBrokenFlushPosition();
		if (brokenHandKickerPosition != INVALID_POSITION) {
			float noSameSuitInDeck = ((DeckOfCards.NUMBER_OF_CARDS / DeckOfCards.NUMBER_OF_SUITS) - (HAND_SIZE - 1));
			float flushFifthCardProbability = (noSameSuitInDeck / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE)) * PROBABILITY_MAXIMUM;
			return (int) ((brokenHandKickerPosition == cardPosition) ? flushFifthCardProbability : PROBABILITY_MINIMUM);
		}

		brokenHandKickerPosition = getBrokenStraightPosition();
		if (brokenHandKickerPosition != INVALID_POSITION) {
			float straightCardProbability = ((float) DeckOfCards.NUMBER_OF_SUITS / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE)) * PROBABILITY_MAXIMUM / 10f;
			float straightFifthCardProbability = straightCardProbability * (PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * (((float) (hand[cardPosition].getGameValue() - PlayingCard.GAME_VALUE_OFFSET)) / ((float) PlayingCard.ACE_GAME_VALUE + PlayingCard.GAME_VALUE_OFFSET))));
			return (int) ((brokenHandKickerPosition == cardPosition || ((isOnePair) ? brokenHandKickerPosition + 1 : INVALID_POSITION) == cardPosition) ? straightFifthCardProbability : PROBABILITY_MINIMUM);
		}

		return (int) PROBABILITY_MINIMUM;
	}

	// Return boolean if this hand contains any broken hands (one card needed to get royal flush, straight flush, straight or flush).
	private boolean isBrokenHand() {
		return (getBrokenRoyalFlushPosition() != INVALID_POSITION)
				|| (getBrokenStraightFlushPosition() != INVALID_POSITION)
				|| (getBrokenStraightPosition() != INVALID_POSITION) || (getBrokenFlushPosition() != INVALID_POSITION);
	}

	// Return position of a card which should be discarded for a possibility to get royal flush.
	private int getBrokenRoyalFlushPosition() {
		int brokenStraightPosition = getBrokenStraightPosition();
		int brokenFlushPosition = getBrokenFlushPosition();

		if (brokenFlushPosition == brokenStraightPosition) {
			if (brokenFlushPosition == 0) {
				if (hand[1].getGameValue() != PlayingCard.KING_VALUE) return INVALID_POSITION;
			} else {
				if (hand[0].getGameValue() == PlayingCard.ACE_GAME_VALUE) {
					return brokenFlushPosition;
				}
			}
		}

		return INVALID_POSITION;
	}

	// Returns if hand is broken straight flush
	private int getBrokenStraightFlushPosition() {
		int position = getBrokenStraightPosition();
		return (position == getBrokenFlushPosition() && getBrokenRoyalFlushPosition() == INVALID_POSITION) ? position : INVALID_POSITION;
	}

	// Return position of a card which should be discarded for a possibility to get straight.
	private int getBrokenStraightPosition() {
		boolean isOnePair = isOnePair();

		// Check if hand has a pattern of: X-X-X-X-Y
		boolean highLow = true;

		for (int i = 0; i < HAND_SIZE - 2; i++) {
			if (hand[i].getGameValue() - hand[i + 1].getGameValue() != 1) {
				highLow = false;
				break;
			}
		}

		// If X-X-X-(X)P-P
		// I return second last card since either of them can be replaced to get straight since they are the same.
		if (highLow) return (isOnePair) ? HAND_SIZE - 2 : HAND_SIZE - 1;

		// Check if hand has a pattern of: Y-X-X-X-X
		boolean lowHigh = true;
		for (int i = HAND_SIZE - 1; i > 1; i--) {
			if (hand[i - 1].getGameValue() - hand[i].getGameValue() != 1) {
				lowHigh = false;
				break;
			}
		}

		if (lowHigh) return 0;

		// 4 Patterns of one pair that we could upgrade to straight		
		// These two patterns are going to be recognized and dealt with, by highLow and lowHigh code above.
		// 10-10-9-8-7
		// 10-9-8-7-7

		// Remaining patterns will be recognized and dealt with by the code below.
		// 10-9-9-7-6
		// 10-9-7-7-6
		int position = INVALID_POSITION;
		if (isOnePair) {
			// Find the pair.

			// Subtract game values of one pair cards and get the sum. If sum is 3 or 2 then,
			// hand should contain broken straight.
			for (int i = 0; i < HAND_SIZE - 1; i++) {
				PlayingCard card = hand[i];
				PlayingCard nextCard = hand[i + 1];
				// Find pair.
				if (card.getGameValue() == nextCard.getGameValue()) {
					if (i > 0 && i < 3) {
						// Example: 
						// (10-9)-(9-7)-6
						// 1 + 2 = 3

						// (5-4)-(4-3)-2
						// 1 + 1 = 2

						// 10-(9-7)-(7-6)
						// 2 + 1 = 3
						int sum = hand[i - 1].getGameValue() - card.getGameValue() + nextCard.getGameValue() - hand[i + 2].getGameValue();
						if (sum == 2 || sum == 3) {
							// Just to make sure, check that first and last cards are apart by at most 4.
							if ((hand[0].getGameValue() - hand[HAND_SIZE - 1].getGameValue()) == HAND_SIZE - 1) {
								position = i;
							}
						}
						break;
					}
				}
			}
		}
		return position;
	}

	// Return position of a card which should be discarded for a possibility to get flush.
	private int getBrokenFlushPosition() {
		Map<Character, Integer> suitCount = new HashMap<Character, Integer>();

		// Count different suits.
		for (int i = 0; i < HAND_SIZE; i++) {
			char suit = hand[i].getSuit();
			if (suitCount.containsKey(suit)) {
				suitCount.put(suit, suitCount.get(suit) + 1);
			} else {
				suitCount.put(suit, 1);
			}
		}

		for (Map.Entry<Character, Integer> entry : suitCount.entrySet()) {
			// Find suit of which we have 4 cards.
			if (entry.getValue() == HAND_SIZE - 1) {
				char matchSuit = entry.getKey();
				// Find a card which is not of same suit and return it.
				for (int i = 0; i < HAND_SIZE; i++)
					if (hand[i].getSuit() != matchSuit) return i;
			}
		}
		return INVALID_POSITION;
	}

	// Returns probability of improving two pair if card at cardPosition would be discarded.
	private int twoPairImprove(int cardPosition) {
		final int firstCardGameValue = hand[0].getGameValue();
		final int middleCardGameValue = hand[2].getGameValue();
		final int lastCardGameValue = hand[HAND_SIZE - 1].getGameValue();

		boolean zeroOne = firstCardGameValue == hand[1].getGameValue();
		boolean twoThree = middleCardGameValue == hand[3].getGameValue();
		boolean oneTwo = hand[1].getGameValue() == middleCardGameValue;
		boolean threeFour = hand[3].getGameValue() == lastCardGameValue;

		int higherPairPosition = 0;
		int lowerPairPosition = HAND_SIZE;
		int kickerPosition = 2;

		// Find position of both pairs and a kicker card.
		if (zeroOne && twoThree) {
			lowerPairPosition = 2;
			kickerPosition = HAND_SIZE - 1;
		} else if (oneTwo && threeFour) {
			higherPairPosition = 2;
			kickerPosition = 0;
		}

		float probability = PROBABILITY_MAXIMUM
				- (PROBABILITY_MAXIMUM * ((hand[kickerPosition].getGameValue() - PlayingCard.GAME_VALUE_OFFSET)
						/ (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)));

		// Return PROBABILITY_MINIMUM if one of the pair cards are chosen,
		// there is some chance to get rid of one card from one of the pairs and
		// still obtain three of a kind,
		// but nobody in their right mind would do this, when they could just
		// get rid of the kicker card and end up with full house instead.
		// So I return minimum probability in this case.
		// No reason to calculate that probability.

		return Math.round((((cardPosition == lowerPairPosition || cardPosition == lowerPairPosition + 1) ||
				(cardPosition == higherPairPosition || cardPosition == higherPairPosition + 1))
				? PROBABILITY_MINIMUM : probability));
	}

	// Returns probability of improving one pair if card at cardPosition would be discarded.
	private int onePairImprove(int cardPosition) {
		int onePairPosition = INVALID_POSITION;
		int chosenCardGameValue = hand[cardPosition].getGameValue();

		// Find the pair.
		for (int i = 0; i < HAND_SIZE - 1; i++) {
			if (hand[i].getGameValue() == hand[i + 1].getGameValue()) {
				onePairPosition = i;
				break;
			}
		}

		// Can either turn the hand into three of a kind or two pair,
		// No matter which one we are going for, we are still going
		// to decide the probability of a card that may be discarded
		// by it's game value.
		float probability = PROBABILITY_MAXIMUM
				- (PROBABILITY_MAXIMUM * ((chosenCardGameValue - PlayingCard.GAME_VALUE_OFFSET)
						/ (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)));

		return Math.round(((chosenCardGameValue == hand[onePairPosition].getGameValue()
				|| chosenCardGameValue == hand[onePairPosition + 1].getGameValue())
				? PROBABILITY_MINIMUM : probability));
	}

	// Returns probability of improving three of a kind if card at cardPosition would be discarded.
	private int threeOfAKindImprove(int cardPosition) {
		PlayingCard chosenCard = hand[cardPosition];
		// Middle card in a hand is part of three of a kind.
		if (chosenCard.getGameValue() != hand[2].getGameValue()) {
			// Get rid one of the kickers without losing any hand game value since another player
			// cannot have same three of a kind.
			// This can either get us four of a kind or full house.
			return Math.round((PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * ((float) (chosenCard.getGameValue() - PlayingCard.GAME_VALUE_OFFSET) / (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)))));
		}
		// Else return min. probability since cardPosition is one of the three of a kind cards.
		return (int) PROBABILITY_MINIMUM;
	}

	// Returns probability of improving flush if card at cardPosition would be discarded.
	private int flushImprove(int cardPosition) {
		float probability = (PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * ((float) (hand[cardPosition].getGameValue() - PlayingCard.GAME_VALUE_OFFSET) / (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET))))
				- ((((float) (DeckOfCards.NUMBER_OF_CARDS / DeckOfCards.NUMBER_OF_SUITS) - HAND_SIZE) / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE)) * PROBABILITY_MAXIMUM + HAND_SIZE);
		// Take into account that we hold 5 of the 13 same suit cards.
		return Math.round(((probability < PROBABILITY_MINIMUM) ? PROBABILITY_MINIMUM : probability));
	}

	// Improves either the pair to the higher three of a kind or
	// upgrades the hand to a four of a kind.
	private int fullHouseImprove(int cardPosition) {
		final int firstCardGameValue = hand[0].getGameValue();
		final int middleCardGameValue = hand[2].getGameValue();
		final int lastCardGameValue = hand[HAND_SIZE - 1].getGameValue();

		int tripleGameValue = middleCardGameValue;
		int pairGameValue = (middleCardGameValue == firstCardGameValue) ? lastCardGameValue : firstCardGameValue;

		PlayingCard chosenCard = hand[cardPosition];

		int cardTypeInDeck = 0;

		// If chosen card is part of a two pair then we can try to upgrade full house to four of a kind.
		if (chosenCard.getGameValue() == pairGameValue) {
			// Since we have three cards out of four, there is only one left in the deck.
			cardTypeInDeck = 1;
		} else {
			// If pair has higher value than three of a kind we can upgrade
			// that pair to a higher three of a kind.
			// By changing X-X-X-Y-Y to X-X-Y-Y-Y
			if (pairGameValue > tripleGameValue) {
				// 2 cards of same type in hand, 2 in deck.
				cardTypeInDeck = 2;
			}
		}

		float probabilityOfACard = ((float) cardTypeInDeck / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE));
		float probabilityByCardGameValue = PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * ((float) (chosenCard.getGameValue() - PlayingCard.GAME_VALUE_OFFSET) / (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)));
		return Math.round(((cardTypeInDeck == 0) ? PROBABILITY_MINIMUM : HAND_SIZE * probabilityOfACard * probabilityByCardGameValue));
	}

	// If highest card is king, this might get upgraded to royal flush, otherwise a higher straight flush.
	// For both outcomes chance is the same.
	private int straightFlushImprove(int cardPosition) {
		float probabilityOfACard = (1f / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE));
		float probabilityByCardGameValue = PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * ((float) (hand[cardPosition].getGameValue() - PlayingCard.GAME_VALUE_OFFSET) / (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)));
		return Math.round(((cardPosition == HAND_SIZE - 1) ? (probabilityOfACard * probabilityByCardGameValue) : PROBABILITY_MINIMUM));
	}

	// Returns probability of improving straight if card at cardPosition would be discarded.
	private int straightImprove(int cardPosition) {
		int lowestCardPosition = cardPosition;

		MINMAXLAST: {
			if (hand[0].getGameValue() == PlayingCard.ACE_GAME_VALUE) {
				if (hand[1].getGameValue() == PlayingCard.FIVE) {
					// CASE A-5-4-3-2
					lowestCardPosition = 0;
					break MINMAXLAST;
				}
				return (int) PROBABILITY_MINIMUM; // CASE A-K-Q-J-10
			}
			// CASE When chosen card is not equal to the lowest card.
			if (lowestCardPosition != HAND_SIZE - 1) return (int) PROBABILITY_MINIMUM;
		}

		// Don't care about the suit so the chance is 4
		float probabilityOfACard = ((float) DeckOfCards.NUMBER_OF_SUITS / (DeckOfCards.NUMBER_OF_CARDS - HAND_SIZE));
		float probabilityByCardGameValue = PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * ((float) (hand[lowestCardPosition].getGameValue() - PlayingCard.GAME_VALUE_OFFSET) / (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET)));
		return Math.round(probabilityOfACard * probabilityByCardGameValue);
	}

	// Returns probability of improving kicker card.
	private int quadsImprove(int cardPosition) {
		int kickerPosition = getQuadsKicker();
		PlayingCard quads = hand[2];
		PlayingCard kicker = hand[kickerPosition];

		if (kickerPosition == cardPosition) {
			float probability = PROBABILITY_MAXIMUM - (PROBABILITY_MAXIMUM * (((float) (kicker.getGameValue() - PlayingCard.GAME_VALUE_OFFSET) / (PlayingCard.ACE_GAME_VALUE - PlayingCard.GAME_VALUE_OFFSET))));
			return Math.round(((quads.getGameValue() == PlayingCard.ACE_GAME_VALUE && kicker.getGameValue() == PlayingCard.KING_VALUE) ? PROBABILITY_MINIMUM : probability));
		}

		return (int) PROBABILITY_MINIMUM;
	}

	// Returns kicker / side card of the four of a kind.
	private int getQuadsKicker() {
		// patternOne = X-X-X-X-Y
		// patternTwo = Y-X-X-X-X
		return hand[0].getGameValue() == hand[3].getGameValue() ? HAND_SIZE - 1 : 0;
	}

	// Returns value of a hand so it can be compared to other hand.
	public int getGameValue() {
		final int firstCardGameValue = hand[0].getGameValue();
		final int middleCardGameValue = hand[2].getGameValue();
		final int lastCardGameValue = hand[HAND_SIZE - 1].getGameValue();

		// All of the royal flushes are the same so no need to do anything.
		if (isRoyalFlush()) return ROYAL_FLUSH_DEFAULT;

		if (isStraightFlush()) return STRAIGHT_FLUSH_DEFAULT + firstCardGameValue;

		// Add leading card game value, if straight is A-5-4-3-2, add 5 and not A.
		int leadingCard = (firstCardGameValue == PlayingCard.ACE_GAME_VALUE
				&& lastCardGameValue == PlayingCard.TWO) ? PlayingCard.FIVE : firstCardGameValue;

		if (isStraight()) return STRAIGHT_DEFAULT + leadingCard;

		int gameValue = 0;

		// Possible patterns: X-X-X-X-Y, Y-X-X-X-X
		if (isFourOfAKind()) {
			// If first two cards of the hand are of same type then the card
			// that is not part of four of a kind is last card in the hand.
			// else pick first card.
			gameValue = (int) Math.pow(middleCardGameValue, POWER_OF_DEFAULT)
					+ ((firstCardGameValue == hand[1].getGameValue()) ? lastCardGameValue : firstCardGameValue);
			return FOUR_OF_A_KIND_DEFAULT + gameValue;
		}

		// Obvious, raise three of a kind to power of 6,
		// and add pair card to the total value.
		// Possible patterns: X-X-X-Y-Y, Y-Y-X-X-X
		if (isFullHouse()) {
			int threeOfAKind = (int) Math.pow(middleCardGameValue, POWER_OF_DEFAULT);
			int pair = (middleCardGameValue == firstCardGameValue) ? lastCardGameValue : firstCardGameValue;
			return FULL_HOUSE_DEFAULT + threeOfAKind + pair;
		}

		if (isThreeOfAKind()) {
			// One of the three of a kind cards will be in the middle of the deck so
			// we raise its (game value + 6) to the power of 6.
			// Adding 6 in case it is low three of a kind like 2 or 3.
			// This has to be done because I'm raising to the power of 3 one of
			// the remaining cards that are not part of three of a kind.
			gameValue = (int) Math.pow(middleCardGameValue + POWER_OF_DEFAULT, POWER_OF_DEFAULT);

			// Possible patterns: X-X-X-Y-Y, Y-Y-X-X-X, Y-X-X-X-Y
			if (firstCardGameValue == middleCardGameValue) {
				// Cards that are not part of three of a kind: {3, 4}
				gameValue += (int) Math.pow(hand[3].getGameValue(), POWER_OF_DEFAULT / 2);
				gameValue += lastCardGameValue;
			} else if (lastCardGameValue == middleCardGameValue) {
				// Cards that are not part of three of a kind: {1, 3}
				gameValue += (int) Math.pow(hand[1].getGameValue(), POWER_OF_DEFAULT / 2);
				gameValue += hand[3].getGameValue();
			} else {
				// Cards that are not part of three of a kind: {0, 4}
				gameValue += (int) Math.pow(firstCardGameValue, POWER_OF_DEFAULT / 2);
				gameValue += lastCardGameValue;
			}
			return THREE_OF_A_KIND_DEFAULT + gameValue;
		}

		if (isTwoPair()) {
			boolean zeroOne = firstCardGameValue == hand[1].getGameValue();
			boolean twoThree = middleCardGameValue == hand[3].getGameValue();
			boolean oneTwo = hand[1].getGameValue() == middleCardGameValue;
			boolean threeFour = hand[3].getGameValue() == lastCardGameValue;

			// Find which cards are pairs, raise to the power of (6, 3)
			// depending on which is higher.
			// Finally, add game value of a remaining card in a hand.
			if (zeroOne && twoThree) {
				gameValue += Math.pow(firstCardGameValue, POWER_OF_DEFAULT)
						+ Math.pow(middleCardGameValue, POWER_OF_DEFAULT / 2) + lastCardGameValue;
			} else if (oneTwo && threeFour) {
				gameValue += Math.pow(middleCardGameValue, POWER_OF_DEFAULT)
						+ Math.pow(lastCardGameValue, POWER_OF_DEFAULT / 2) + firstCardGameValue;
			} else if (zeroOne && threeFour) {
				gameValue += Math.pow(firstCardGameValue, POWER_OF_DEFAULT)
						+ Math.pow(lastCardGameValue, POWER_OF_DEFAULT / 2) + middleCardGameValue;
			}

			return TWO_PAIRS_DEFAULT + gameValue;
		}

		if (isOnePair()) {
			int power = POWER_OF_DEFAULT / 2;

			for (int i = 0; i < HAND_SIZE - 1; i++) {
				// Find the pair.
				if (hand[i].getGameValue() == hand[i + 1].getGameValue()) {
					// Add six, raise to the power of 6.
					// Adding six in case the pair is low.
					gameValue += Math.pow(hand[i].getGameValue() + POWER_OF_DEFAULT, POWER_OF_DEFAULT);
					// Skip both cards of the pair.
					i++;
					continue;
				}
				// Raise to the power of (3, 2, 1) all of the remaining cards that are not pair.
				gameValue += Math.pow(hand[i].getGameValue(), power--);
			}

			return ONE_PAIR_DEFAULT + gameValue + lastCardGameValue;
		}

		// If execution reaches this point it means the hand is either high hand or a flush.
		for (int i = 0; i < HAND_SIZE; i++) {
			gameValue += Math.pow(hand[i].getGameValue(), HAND_SIZE - i);
		}

		if (isFlush()) {
			gameValue += FLUSH_DEFAULT;
		}

		return gameValue;
	}

	/*
	 * Sorts hand (array of PlayingCard objects) in descending order.
	 * Using insertion sort algorithm.
	 */
	private void sort() {
		for (int i = 0; i < HAND_SIZE; i++) {
			PlayingCard card = hand[i];
			for (int j = i + 1; j < HAND_SIZE; j++) {
				PlayingCard nextCard = hand[j];
				if (card.getGameValue() < nextCard.getGameValue()) {
					hand[i] = nextCard;
					hand[j] = card;
					card = nextCard;
				}
			}
		}
	}

	// Getter method for the deck that was used to deal this hand.
	public DeckOfCards getDeck() {
		return deck;
	}

	// Returns true or false depending on whether a hand contains royal flush or not.
	public boolean isRoyalFlush() {
		PlayingCard firstCard = hand[0];
		if (firstCard.getType().equals(PlayingCard.ACE)) {
			for (int i = 0; i < HAND_SIZE - 1; i++) {
				// Check if card[i] - card[i + 1] game value is not equal to one
				// or suits of the cards do not match.
				// If one of these two conditions are true, then this hand is
				// not royal flush.
				if ((hand[i].getGameValue() - hand[i + 1].getGameValue()) != 1
						|| firstCard.getSuit() != hand[i].getSuit()) {
					return false;
				}
			}
			// Check suit of a last card in a hand.
			// (Has to be checked here because for loop above goes up to the
			// HAND_SIZE - 1).
			return firstCard.getSuit() == hand[HAND_SIZE - 1].getSuit();
		}
		return false;
	}

	// Returns true or false depending on whether a hand contains straight flush or not.
	public boolean isStraightFlush() {
		PlayingCard firstCard = hand[0];
		for (int i = 0; i < HAND_SIZE - 1; i++) {
			// Same thing as in royal flush method, except instead of game value
			// I used face value
			// so if the first card is ace this if statement will be true.
			// Reason for doing this is so that I don't have to call
			// isRoyalFlush() method.
			if ((hand[i].getFaceValue() - hand[i + 1].getFaceValue()) != 1
					|| firstCard.getSuit() != hand[i].getSuit()) {
				return false;
			}
		}
		return firstCard.getSuit() == hand[HAND_SIZE - 1].getSuit();
	}

	// Returns true or false depending on whether a hand contains four of a kind or not.
	public boolean isFourOfAKind() {
		// patternOne = X-X-X-X-Y
		// patternTwo = Y-X-X-X-X
		boolean patternOne = hand[0].getGameValue() == hand[3].getGameValue();
		boolean patternTwo = hand[4].getGameValue() == hand[1].getGameValue();
		return patternOne || patternTwo;
	}

	// Returns true or false depending on whether a hand contains three of a kind or not.
	public boolean isThreeOfAKind() {
		return !isFourOfAKind() && !isFullHouse()
				&& (hand[0].getGameValue() == hand[2].getGameValue() || hand[1].getGameValue() == hand[3].getGameValue()
				|| hand[2].getGameValue() == hand[4].getGameValue());
	}

	// Returns true or false depending on whether a hand contains full house or not.
	public boolean isFullHouse() {
		// patternOne = X-X-X-Y-Y
		// patternTwo = Y-Y-X-X-X
		boolean patternOne = hand[0].getGameValue() == hand[2].getGameValue()
				&& hand[3].getGameValue() == hand[4].getGameValue();
		boolean patternTwo = hand[0].getGameValue() == hand[1].getGameValue()
				&& hand[2].getGameValue() == hand[4].getGameValue();
		return patternOne || patternTwo;
	}

	// Returns true or false depending on whether a hand contains straight or not.
	public boolean isStraight() {
		if (isStraightFlush()) {
			return false;
		}

		// A - 5 - 4 - 3 - 2
		patternOne: {
			if (hand[0].getType().equals(PlayingCard.ACE)) {
				// If left most card is ace then loop from right side of the hand.
				for (int i = HAND_SIZE - 1; i > 0; i--) {
					if (((i - HAND_SIZE) + hand[i].getFaceValue()) != hand[0].getFaceValue()) {
						break patternOne;
					}
				}
				return true;
			}
		}

		for (int i = 1; i < HAND_SIZE; i++) {
			if ((hand[0].getGameValue() - i) != hand[i].getGameValue()) {
				return false;
			}
		}

		return true;
	}

	// Returns true or false depending on whether a hand contains flush or not.
	public boolean isFlush() {
		if (isStraightFlush() || isRoyalFlush()) {
			return false;
		}

		char suit = hand[0].getSuit();
		// Loop through hand of cards and check if card suits match.
		for (int i = 1; i < HAND_SIZE; i++) {
			if (suit != hand[i].getSuit()) {
				return false;
			}
		}
		return true;
	}

	// Returns true or false depending on whether a hand contains two pair or
	// not.
	public boolean isTwoPair() {
		if (isOnePair() || isThreeOfAKind() || isFourOfAKind() || isFullHouse()) {
			return false;
		}

		// Two pair = 3 patterns
		// patternOne = X-X-Y-Y-Z
		// patternTwo = Z-Y-Y-X-X
		// patternThree = X-X-Z-Y-Y
		boolean zeroOne = hand[0].getGameValue() == hand[1].getGameValue();
		boolean oneTwo = hand[1].getGameValue() == hand[2].getGameValue();
		boolean twoThree = hand[2].getGameValue() == hand[3].getGameValue();
		boolean threeFour = hand[3].getGameValue() == hand[4].getGameValue();
		boolean patternOne = zeroOne && twoThree;
		boolean patternTwo = oneTwo && threeFour;
		boolean patternThree = zeroOne && threeFour;

		return patternOne || patternTwo || patternThree;
	}

	// Returns true or false depending on whether a hand contains one pair or not.
	public boolean isOnePair() {
		int count = 0;
		for (int i = 0; i < HAND_SIZE - 1; i++) {
			// Count same type of cards.
			// If hand contains three of a kind or two pair the count value will
			// be > 1.
			if (hand[i].getGameValue() == hand[i + 1].getGameValue()) {
				count++;
			}
		}
		return count == 1;
	}

	// Returns true or false depending on whether a hand contains high hand or not.
	public boolean isHighHand() {
		return !isRoyalFlush() && !isStraightFlush() && !isFourOfAKind() && !isFullHouse() && !isFlush()
				&& !isStraight() && !isThreeOfAKind() && !isTwoPair() && !isOnePair();
	}

	public String toString() {
		String string = "";
		for (PlayingCard card : hand) {
			string += card + " ";
		}
		return string;
	}

}