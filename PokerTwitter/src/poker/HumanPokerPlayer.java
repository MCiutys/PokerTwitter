package poker;

public class HumanPokerPlayer extends PokerPlayer {
	// Constants
	private static final String INTEGER_REGEX = "\\d+$";
	private static final int BET_ERROR = -2;
	private static final int BET_FOLD = -1;
	private static final int BET_CHECK = 0;

	// Variables
	private String twitterHandle;

	public HumanPokerPlayer(DeckOfCards deck, String mName, String mTwitterHandle) {
		super(deck, mName);
		twitterHandle = mTwitterHandle;
	}

	@Override
	public int discard() {
		int disCount = 0;
		String disStr = "1 3 5"; // Get this from twitter bot

		if (!disStr.isEmpty()) {
			String[] disStrSplit = disStr.trim().split(" ");

			// If more than 3 card entered, just discard the first 3 cards in
			// the
			// string and ignore the rest.
			for (int i = 0; i < MAX_CARDS_TO_DISCARD; i++) {
				if (disStrSplit[i].matches(INTEGER_REGEX)) {
					int disPos = Integer.valueOf(disStrSplit[i]);
					hand.replaceCard(disPos);
					disCount++;
				}
			}
		}
		return disCount;
	}

	@Override
	public int bet(int callBet) {
		// If player cant call the bet, auto fold
		if (funds < callBet) {
			System.out.println("You are unable to call the current bet of " + callBet
					+ ", which means you have to fold this round");
			return BET_ERROR;
		}

		boolean validBet = false;
		int betAmount = BET_ERROR;

		// while (!validBet) {
		/*
		 * Possible formats of the bet string: '100', 'fold', 'check', 'call',
		 * 'bet 100', 'raise 100', 'all'.
		 */
		validBet = true;
		String betStr = "Bet 200"; // Get this from twitter bot
		String[] betStrSplit = betStr.trim().split(" ");

		switch (betStrSplit.length) {
		case 1:
			if ((betStrSplit[0].matches(INTEGER_REGEX)))
				betAmount = Integer.valueOf(betStrSplit[0]);
			else if (betStrSplit[0].equalsIgnoreCase("fold")) {
				betAmount = BET_FOLD;
			} else if (betStrSplit[0].equalsIgnoreCase("check")) {
				betAmount = BET_CHECK;
			} else if (betStrSplit[0].equalsIgnoreCase("call")) {
				betAmount = callBet;
			} else if (betStrSplit[0].equalsIgnoreCase("all")) {
				betAmount = funds;
			} else if (betStrSplit[0].equalsIgnoreCase(INTEGER_REGEX)) {
				betAmount = Integer.valueOf(betStrSplit[0]);
			} else {
				validBet = false;
			}
			break;
		case 2:
			if (betStrSplit[0].equalsIgnoreCase("bet") && (betStrSplit[1].matches(INTEGER_REGEX))) {
				betAmount = Integer.valueOf(betStrSplit[1]);
			} else if (betStrSplit[0].equalsIgnoreCase("raise") && (betStrSplit[1].matches(INTEGER_REGEX))) {
				betAmount = callBet + Integer.valueOf(betStrSplit[1]);
			} else {
				validBet = false;
			}
			break;
		default:
			// Unknown type of string; error message
			validBet = false;
		}

		// Boolean that hold important values to proceed
		boolean lsEqFunds = betAmount < funds, grEqCallBet = betAmount >= callBet;

		if (validBet && (!lsEqFunds || !grEqCallBet)) {
			// Something isnt right
			validBet = false;
			betAmount = BET_FOLD;

			if (!lsEqFunds) {
				// Don't have enough funds in your account; error message
				System.out.println("Not enough funds in your account");
			} else if (!grEqCallBet) {
				// Bet amount is less than the call bet; error message
				System.out.println("Bet amount is less than the call bet");
			}
		}
		// }

		funds -= betAmount;
		return betAmount;
	}

	// Getters and Setters
	public String getTwitterHandle() {
		return twitterHandle;
	}
}
