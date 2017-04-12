package poker;

public class HumanPokerPlayer extends PokerPlayer {
	private String twitterHandle;

	public HumanPokerPlayer(DeckOfCards deck, String mName, String mTwitterHandle) {
		super(deck, mName);
		twitterHandle = mTwitterHandle;
	}

	@Override
	public int discard() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int bet(int callBet) {
		boolean validBet = false;
		int betAmount = -1;

		// while (!validBet) {
		/*
		 * Possible formats of the bet string: 'fold', 'check', 'call', 'bet
		 * 100', 'raise 100'
		 */
		validBet = true;
		String betStr = "Bet 200"; // Get this from twitter bot
		String[] betStrSplit = betStr.split(" ");

		if ((betStrSplit[0].matches("\\d+$")))
			betAmount = Integer.valueOf(betStrSplit[0]);
		else if (betStrSplit[0].equalsIgnoreCase("fold")) {
		} else if (betStrSplit[0].equalsIgnoreCase("check")) {
			betAmount = 0;
		} else if (betStrSplit[0].equalsIgnoreCase("call")) {
			betAmount = callBet;
		} else if (betStrSplit[0].equalsIgnoreCase("bet") && (betStrSplit[1].matches("\\d+$"))) {
			betAmount = Integer.valueOf(betStrSplit[1]);
		} else if (betStrSplit[0].equalsIgnoreCase("raise") && (betStrSplit[1].matches("\\d+$"))) {
			betAmount = callBet + Integer.valueOf(betStrSplit[1]);
		} else {
			// Unknown type of string; error message
			validBet = false;
		}

		// Boolean that hold important values to proceed
		boolean lsEqFunds = betAmount < funds, grEqCallBet = betAmount >= callBet;

		if (validBet && (!lsEqFunds || !grEqCallBet)) {
			// Something isnt right
			validBet = false;
			betAmount = 0;

			if (!lsEqFunds) {
				// Don't have enough funds in your account; error message
				System.out.println("Not enough funds in your account");
			} else if (!grEqCallBet) {
				// Bet amout is less than the call bet; error message
				System.out.println("Bet amount is less than the call bet");
			}
		}
		// }

		funds -= betAmount;
		return betAmount;
	}

}
