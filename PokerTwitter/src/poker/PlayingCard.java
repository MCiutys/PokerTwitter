/*
 * A class that represents a card that can be played in a poker game.
 * This class stores the card type, suit face and game values.
 *
 * @author Edgaras Lagunovas (15204377)
 */

package poker;

public class PlayingCard {

	// Card types
	public static final String ACE = "A";
	public static final String KING = "K";
	public static final String QUEEN = "Q";
	public static final String JACK = "J";
	// Used as type, face, game values.
	public static final int TWO = 2;
	public static final int FIVE = 5;

	// Card face/game values.
	public static final int ACE_FACE_VALUE = 1;
	public static final int ACE_GAME_VALUE = 14;
	public static final int KING_VALUE = 13;
	public static final int QUEEN_VALUE = 12;
	public static final int JACK_VALUE = 11;
	
	// Card suits
	public static final char HEARTS = 'H';
	public static final char SPADES = 'S';
	public static final char DIAMONDS = 'D';
	public static final char CLUBS = 'C';
	
	public static final int GAME_VALUE_OFFSET = 2;

	private String type;
	private char suit;
	private int faceValue;
	private int gameValue;

	public PlayingCard(String type, char suit, int faceValue, int gameValue) {
		this.type = type;
		this.suit = suit;
		this.faceValue = faceValue;
		this.gameValue = gameValue;
	}

	// Getter method for card type.
	public String getType() {
		return type;
	}

	// Getter method for cards suit.
	public char getSuit() {
		return suit;
	}

	// Getter method for cards face value.
	public int getFaceValue() {
		return faceValue;
	}

	// Getter method for cards game value.
	public int getGameValue() {
		return gameValue;
	}

	public String toString() {
		return type + suit;
	}

}