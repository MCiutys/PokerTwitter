package poker;

import java.util.Random;

public class Constants {

	public static final int MAX_PLAYERS = 6;
	public static final String NATURAL_NUMBER_REGEX = "//^\\d+$//";
	public static final int THREAD_SLEEP_TIME = 100;

	// Misc.
	public static final String HASH_TAG = "#ECMXPoker";
	public static final String NEW_LINE = "\n";

	// Game messages/questions
	public static final String WINNER = " have won the game! Game ID: ";
	public static final String HAND_DEALT = "You have been dealt a hand: ";
	public static final String DISCARD_QUESTION = "Please enter the cards you want to discard? ";
	public static final String BET_QUESTION = "Please enter the your bet? ";
	public static final String FAILED_BET_1 = "Failed to produce valid bet, so we are going to call for you.";
	public static final String FAILED_BET_2 = "Failed to produce valid bet, so we are going to fold for you.";
	public static final String DISCARDED_COUNT = "You have discarded ";
	public static final String CURRENT_HAND = "Your current hand: ";
	public static final String NO_OPEN = "No one can open, starting a new round...";
	public static final String OPEN_INFORM = " is able to open, get ready to bet!";

	// Ack. messages
	public static final String JOINED_ROOM = "You have joined a game of poker!";
	public static final String GAME_STARTED = "You have started the poker game!";
	public static final String GAME_CREATED = "You have created a game of poker. Game ID: ";

	// Error messages
	public static final String INCORRECT_JOIN_NUMBER = "You have entered incorrect game number!";
	public static final String YOUR_TWEET = "Your tweet: ";
	public static final String GAME_ALREADY_STARTED = "Game is already started!";
	public static final String UNKNOWN_COMMAND = "Unknown command entered!";
	public static final String NOT_PART_OF_ROOM = "You are not part of any game of poker, therefore you can not start the game!";
	public static final String ALREADY_IN_A_GAME = "You are already playing in another game, please finish that game before joining another one!";
	public static final String NO_FUNDS = "Not enough funds in your account";
	public static final String LESS_THAN_CALLBET = "Bet amount is less than the call bet";

	// Commands
	public static final String COMMAND_START = "start";
	public static final String COMMAND_PLAY = "play";
	public static final String COMMAND_JOIN = "join";
	public static final String COMMAND_BET = "bet";
	public static final String COMMAND_FOLD = "fold";
	public static final String COMMAND_CALL = "call";
	public static final String COMMAND_RAISE = "raise";
	public static final String COMMAND_DISCARD = "discard";

	public static final Random RANDOM = new Random(System.currentTimeMillis());

}
