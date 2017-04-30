package poker;

import java.util.Scanner;

public class HumanPokerPlayer extends PokerPlayer {
	// Constants
	// private static final String DISCARD_REGEX = "";
	private static final String BET_REGEX = "^\\w+$|^\\w+ \\w+$";
	private static final String INTEGER_REGEX = "^\\d+";
	private static final int MAX_TRIES = 3;

	// Question String
	private static final String DISCARD_QUESTION = "Please enter the cards you want to discard? ";
	private static final String BET_QUESTION = "Please enter the your bet? ";
	private static final String TRY_AGAIN_QUESTION = "\nInvalid input, please try again? ";

	// Error String
	private static final String NO_FUNDS_STR = "Not enough funds in your account";
	private static final String LESS_THAN_CALLBET_STR = "Bet amount is less than the call bet";

	// Variables
	private long userId;

	public HumanPokerPlayer(DeckOfCards deck, String mName, long userId) {
		super(deck, mName);
		this.userId = userId;
	}

	@Override
	public int discard() {
		int disCount = 0;
		// ******************* Get this from twitter bot *******************
		String disStr = getDisStr();

		if (!disStr.isEmpty()) {
			String[] disStrSplit = disStr.split(" ");

			// If more than 3 card entered, just discard the first 3 cards in
			// the string and ignore the rest.
			for (int i = 0; i < MAX_CARDS_TO_DISCARD && i < disStrSplit.length; i++) {
				if (disStrSplit[i].matches(INTEGER_REGEX)) {
					// Goes from [1-5] instead of [0-4] so minus 1
					int disPos = Integer.valueOf(disStrSplit[i]) - 1;
					hand.replaceCard(disPos);
					disCount++;
				}
			}
		}
		return disCount;
	}

	@Override
	public int bet(int callBet) {
		// If player can't call the bet, auto fold
		if (funds < callBet) {
			System.out.println("You are unable to call the current bet of " + callBet
					+ ", which means you have to fold this round");
			return BET_FOLD;
		}

		boolean validBet = false;
		int betAmount = BET_ERROR;

		while (!validBet) {
			/*
			 * Possible formats of the bet string: '100', 'fold', 'check',
			 * 'call', 'bet 100', 'raise 100', 'all'.
			 */
			validBet = true;
			// ******************* Get this from twitter bot *******************
			String betStr = getBetStr(callBet);
			String[] betStrSplit = betStr.split(" ");

			switch (betStrSplit.length) {
			case 0:
				if (funds > callBet) {
					System.out.println("Failed to produce valid bet, so we are going to call for you.");
					betAmount = callBet;
				} else {
					System.out.println("Failed to produce valid bet, so we are going to fold for you.");
					betAmount = BET_FOLD;
				}
				break;
			case 1:
				if ((betStrSplit[0].matches(INTEGER_REGEX)))
					betAmount = Integer.valueOf(betStrSplit[0]);
				else if (betStrSplit[0].equalsIgnoreCase("fold")) {
					betAmount = BET_FOLD;
				} else if (betStrSplit[0].equalsIgnoreCase("call")) {
					betAmount = callBet;
				} else if (betStrSplit[0].equalsIgnoreCase("all")) {
					betAmount = funds;
				} else {
					validBet = false;
				}
				break;
			case 2:
				if (betStrSplit[0].equalsIgnoreCase("bet") && (betStrSplit[1].matches(INTEGER_REGEX))) {
					betAmount = Integer.valueOf(betStrSplit[1]);
				} else if (betStrSplit[0].equalsIgnoreCase("raise") && (betStrSplit[1].matches(INTEGER_REGEX))) {
					betAmount = callBet + Integer.valueOf(betStrSplit[1]);
				} else if (betStrSplit[0].equalsIgnoreCase("all") && betStrSplit[1].equalsIgnoreCase("in")) {
					betAmount = funds;
				} else {
					validBet = false;
				}
				break;
			default:
				// Unknown type of string; error message
				validBet = false;
			}

			// Boolean that hold boolean values to proceed with
			boolean lsEqFunds = betAmount <= funds, grEqCallBet = betAmount >= callBet;

			if (validBet && (!lsEqFunds || !grEqCallBet)) {
				// Something isn't right
				validBet = false;
				betAmount = BET_ERROR;

				if (!lsEqFunds) {
					// Don't have enough funds in your account; error message
					System.out.println(NO_FUNDS_STR);
				} else if (!grEqCallBet) {
					// Bet amount is less than the call bet; error message
					System.out.println(LESS_THAN_CALLBET_STR);
				}
			}
		}

		if (betAmount != BET_FOLD)
			funds -= betAmount;
		return betAmount;
	}

	private String getDisStr() {
		int tries = 1;
		String disStr = getInput(DISCARD_QUESTION).trim();

		// for (; !disStr.isEmpty() && !disStr.matches(DISCARD_REGEX) && tries <
		// MAX_TRIES; tries++) {
		// disStr = getInput(TRY_AGAIN_QUESTION);
		// }

		// if (!disStr.matches(BET_REGEX) && tries == MAX_TRIES) {
		// System.out.println("Max number of tries has been reached, therefore
		// not going to discard any cards.");
		// }

		return disStr;
	}

	private String getBetStr(int callBet) {
		int tries = 1;
		String betStr = getInput(BET_QUESTION).trim();

		for (; !betStr.matches(BET_REGEX) && tries < MAX_TRIES; tries++) {
			betStr = getInput(TRY_AGAIN_QUESTION);
		}

		if (!betStr.matches(BET_REGEX) && tries == MAX_TRIES) {
			System.out.println("Max number of tries has been reached, going to choose for you");
			betStr = (funds > callBet && hand.getGameValue() > HandOfCards.ONE_PAIR_DEFAULT ? "call" : "fold");
		}

		return betStr;
	}

	// TEMPORARY FUNCTION
	// We do NOT close the scanner as it will close System.in forever
	@SuppressWarnings("resource")
	private String getInput(String question) {
		System.out.print(question);
		Scanner s = new Scanner(System.in);
		String input = s.nextLine();
		return input;
	}
}
