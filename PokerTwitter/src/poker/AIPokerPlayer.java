package poker;

public class AIPokerPlayer extends PokerPlayer {
	private int BAD_HAND_VALUE = HandOfCards.ONE_PAIR_DEFAULT;
	private int GOOD_HAND_VALUE = HandOfCards.TWO_PAIRS_DEFAULT;
	private int GREAT_HAND_VALUE = HandOfCards.FOUR_OF_A_KIND_DEFAULT;
	private int MIN_POT_FOLD = 100;

	public AIPokerPlayer(DeckOfCards deck, String mName) {
		super(deck, mName);
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
		int betAmount = (callBet <= funds ? callBet : 0);

		if (doFold(callBet)) {
			betAmount = BET_FOLD;
		} else if (doCheck(callBet)) {
			betAmount = BET_CHECK;
		} else if (doCall(callBet)) {
			betAmount = callBet;
		} else if (doRaise(callBet)) {
			betAmount = raiseBetBy(callBet) + callBet;
		} else if (doAllIn(callBet)) {
			betAmount = funds;
		} else {
			betAmount = (funds > callBet ? callBet : BET_FOLD);
		}

		funds -= betAmount;
		return betAmount;

	}

	// Bet Helper Functions
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
	 * Decides whether or not to check given your current hand and the call bet.
	 * If you don't have a good hand but want to stay in just case;
	 * 
	 * @param callBet
	 * @return True if hand is bad but the call bet is 0.
	 */
	private boolean doCheck(int callBet) {
		return callBet == 0 && hand.getGameValue() < BAD_HAND_VALUE;
	}

	/**
	 * Decides whether or not to call given your current hand and the call bet.
	 * 
	 * @param callBet
	 * @return
	 */
	private boolean doCall(int callBet) {
		return hand.getGameValue() > BAD_HAND_VALUE && hand.getGameValue() < GOOD_HAND_VALUE && callBet <= funds;
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

	/**
	 * Decides whether or not to go all in given your current hand and the call
	 * bet.
	 * 
	 * @param callBet
	 * @return
	 */
	private boolean doAllIn(int callBet) {
		return hand.getGameValue() > GREAT_HAND_VALUE && callBet < funds;
	}

	private int raiseBetBy(int callBet) {
		// Safety Statement
		if (funds < callBet) {
			// I guess it should go all in then

		}

		int raise = (funds - callBet) / 4;

		return raise;
	}
}
