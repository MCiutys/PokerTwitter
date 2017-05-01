package poker.player;

import java.util.LinkedList;
import java.util.Queue;

import poker.Constants;
import poker.DeckOfCards;
import poker.twitter.TwitterBot;
import twitter4j.User;

public class HumanPokerPlayer extends PokerPlayer {

	private User user;
	private Queue<String[]> inputQueue;

	public HumanPokerPlayer(DeckOfCards deck, User user) {
		super(deck, user.getScreenName());
		this.user = user;
		inputQueue = new LinkedList<String[]>();
	}

	public void insertInput(String[] input) {
		inputQueue.add(input);
	}

	private void waitForInput() {
		while (inputQueue.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int discard() {
		int disCount = 0;

		TwitterBot.updateStatus(Constants.DISCARD_QUESTION + Constants.NEW_LINE + Constants.NEW_LINE + "@" + name);

		String[] discardInput;

		while (true) {
			waitForInput();

			discardInput = inputQueue.remove();

			if (discardInput[1].equals("discard")) {
				// If more than 3 card entered, just discard the first 3 cards in
				// the string and ignore the rest.
				for (int i = 2; i - 2 < MAX_CARDS_TO_DISCARD && i < discardInput.length; i++) {
					if (discardInput[i].matches(Constants.INTEGER_REGEX)) {
						// Goes from [1-5] instead of [0-4] so minus 1
						int disPos = Integer.valueOf(discardInput[i]) - 1;
						hand.replaceCard(disPos);
						disCount++;
					}
				}
			}
			return disCount;
		}
	}

	@Override
	public int bet(int callBet) {
		// If player can't call the bet, auto fold
		if (funds < callBet) {
			TwitterBot.updateStatus("You are unable to call the current bet of " + callBet
					+ ", which means you have to fold this round!" + Constants.NEW_LINE + "@" + name);
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

			TwitterBot.updateStatus(Constants.BET_QUESTION + Constants.NEW_LINE + "@" + name);

			waitForInput();

			String[] input = inputQueue.remove();

			switch (input.length) {
			case 0:
			case 1:
				if (funds > callBet) {
					TwitterBot.directMessage(user.getId(), Constants.FAILED_BET_1);
					betAmount = callBet;
				} else {
					TwitterBot.directMessage(user.getId(), Constants.FAILED_BET_2);
					betAmount = BET_FOLD;
				}
				break;
			case 2:
				if (input[1].equalsIgnoreCase("fold")) {
					betAmount = BET_FOLD;
				} else if (input[1].equalsIgnoreCase("call")) {
					betAmount = callBet;
				} else {
					validBet = false;
				}
				break;
			case 3:
				if (input[1].equalsIgnoreCase("bet") && ((input[2].matches(Constants.INTEGER_REGEX)) || input[2].equalsIgnoreCase("all"))) {
					if (input[2].equalsIgnoreCase("all")) {
						betAmount = funds;
					} else {
						betAmount = Integer.valueOf(input[2]);
					}
				} else if (input[1].equalsIgnoreCase("raise") && (input[1].matches(Constants.INTEGER_REGEX))) {
					betAmount = callBet + Integer.valueOf(input[1]);
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
					TwitterBot.directMessage(user.getId(), Constants.NO_FUNDS);
				} else if (!grEqCallBet) {
					// Bet amount is less than the call bet; error message
					TwitterBot.directMessage(user.getId(), Constants.LESS_THAN_CALLBET);
				}
			}
		}

		if (betAmount != BET_FOLD)
			funds -= betAmount;
		return betAmount;
	}

	public User getUser() {
		return user;
	}

}