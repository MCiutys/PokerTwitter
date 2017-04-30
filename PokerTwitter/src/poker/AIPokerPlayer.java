package poker;

public class AIPokerPlayer extends PokerPlayer {
	private static int BAD_HAND_VALUE = HandOfCards.ONE_PAIR_DEFAULT;
	private static int GOOD_HAND_VALUE = HandOfCards.TWO_PAIRS_DEFAULT;
	private static int GREAT_HAND_VALUE = HandOfCards.FOUR_OF_A_KIND_DEFAULT;

	// Variables
	private static int MAX_RAISE = 2000;
	private static int MIN_POT_FOLD = 30;
	private static int MIN_BET = 10;

	/**
	 * Default constructor of the AIPokerPlayer class. Just set the deck and
	 * name of the bot, all other values are set to be defaults.
	 * 
	 * @param deck
	 *            The deck from which the bot collect its cards.
	 * @param mName
	 *            The name of the bot.
	 */
	public AIPokerPlayer(DeckOfCards deck, String mName) {
		this(deck, mName, BAD_HAND_VALUE, GOOD_HAND_VALUE, GREAT_HAND_VALUE, MIN_POT_FOLD, MIN_BET);
	}

	/**
	 * Customizable constructor of the AIPokerPlayer class.
	 * 
	 * @param deck
	 *            The deck from which the bot collect its cards.
	 * @param mName
	 *            The name of the bot.
	 * @param bad_hand_value
	 *            The smallest hand gamevalue for which the bot folds.
	 * @param good_hand_value
	 *            The smallest hand gamevalue for which the bot considers
	 *            raising the bet.
	 * @param great_hand_value
	 *            The smallest hand gamevalue for which the bot will go all in.
	 * @param min_pot_fold
	 *            The smallest callBet for which the bot will consider folding.
	 */
	public AIPokerPlayer(DeckOfCards deck, String mName, int bad_hand_value, int good_hand_value, int great_hand_value,
			int min_pot_fold, int min_bet) {
		super(deck, mName);

		// BAD_HAND_VALUE options
		if (bad_hand_value >= 0 && bad_hand_value < HandOfCards.FOUR_OF_A_KIND_DEFAULT) {
			BAD_HAND_VALUE = bad_hand_value;
		}
		// GOOD_HAND_VALUE options
		if (good_hand_value > BAD_HAND_VALUE && good_hand_value < HandOfCards.STRAIGHT_FLUSH_DEFAULT) {
			GOOD_HAND_VALUE = good_hand_value;
		}
		// GREAT_HAND_VALUE options
		if (great_hand_value > GOOD_HAND_VALUE) {
			GREAT_HAND_VALUE = great_hand_value;
		}
		// MIN_POT_FOLD options
		if (MIN_POT_FOLD >= 0) {
			MIN_POT_FOLD = min_pot_fold;
		}

		// MIN_BET options
		if (MIN_BET > 0) {
			MIN_BET = min_bet;
		}
	}

	@Override
	public int discard() {
		int counter = 0;
		for (int i = 0; i < HandOfCards.HAND_SIZE; i++) {
			int discardProbability = hand.getDiscardProbability(i);
			// Generate random number between 1 and 99.
			int random = (int) (Math.random() * HandOfCards.PROBABILITY_MAXIMUM - 1) + 1;
			// If generated random number is in the range between 0 and
			// discardProbability, discard the card.
			if (random <= discardProbability && counter < MAX_CARDS_TO_DISCARD) {
				hand.replaceCard(i);
				counter++;
			}
		}

		return counter;
	}

	@Override
	public int bet(int callBet) {
		// Default bet if it doesn't enter an if statement
		int betAmount = funds > callBet ? callBet : BET_FOLD;

		if (doFold(callBet)) {
			betAmount = BET_FOLD;
		} else if (doAllIn(callBet)) {
			betAmount = funds;
		} else if (doRaise(callBet)) {
			betAmount = raiseBetBy(callBet) + callBet;
		} else if (doCall(callBet)) {
			betAmount = (callBet < MIN_BET ? MIN_BET : callBet);
		}

		// If the bot wants to bet anyway but doesn't have the funds, go all in
		if (funds < betAmount)
			betAmount = funds;

		// Remove the bet amount from the funds if not folding
		if (betAmount != BET_FOLD)
			funds -= betAmount;

		return betAmount;

	}

	// Bet Helper Functions: Ordered in the way you call them in bet()
	/**
	 * Decides whether or not to fold given your current hand and the call bet.
	 * True if you don't have enough funds in your account or the call bet if
	 * greater than 0 and you have a bad hand.
	 * 
	 * @param callBet
	 * @return True if you should fold else false
	 */
	private boolean doFold(int callBet) {
		return funds < callBet || (callBet > MIN_POT_FOLD && hand.getGameValue() < BAD_HAND_VALUE);
	}

	/**
	 * Decides whether or not to go all in given your current hand and the call
	 * bet.
	 * 
	 * @param callBet
	 * @return
	 */
	private boolean doAllIn(int callBet) {
		return hand.getGameValue() > GREAT_HAND_VALUE;
	}

	/**
	 * Decides whether or not to raise given your current hand and the call bet.
	 * 
	 * @param callBet
	 * @return
	 */
	private boolean doRaise(int callBet) {
		return hand.getGameValue() > GOOD_HAND_VALUE && callBet < funds;
	}

	private int raiseBetBy(int callBet) {
		double howGoodRatio = hand.getGameValue() / HandOfCards.ROYAL_FLUSH_DEFAULT;
		int raise = (int) (((funds - callBet) / 2) * howGoodRatio);
		return raise > MAX_RAISE ? MAX_RAISE : raise;
	}

	/**
	 * Decides whether or not to call given your current hand and the call bet.
	 * 
	 * @param callBet
	 * @return
	 */
	private boolean doCall(int callBet) {
		return (hand.getGameValue() > BAD_HAND_VALUE || callBet < MIN_POT_FOLD) && hand.getGameValue() < GOOD_HAND_VALUE
				&& callBet <= funds;
	}
}
