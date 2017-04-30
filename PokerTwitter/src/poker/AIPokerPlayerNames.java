package poker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AIPokerPlayerNames {
	private ArrayList<String> listOfNames;
	private static AIPokerPlayerNames aiPokerPlayerNames;
	public static final int DEFAULT_NUMBER_NAMES = 4;
	private static final String FILE_NAME = "list_of_names.txt";

	/**
	 * Constructor that reads the list of bot names in from a file.
	 * 
	 * @param fileName
	 *            The file from which the strings of bot names are read.
	 */
	private AIPokerPlayerNames() {
		listOfNames = new ArrayList<String>();
		File file = new File(FILE_NAME);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null)
				listOfNames.add(line);
		} catch (IOException e) {
			System.out.println("An io exception has been produced when trying to read the file '" + FILE_NAME + "'.");
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {}
		}
	}

	/**
	 * Get a list of random names, meant to be used for generation of bot names.
	 * If input argument is greater than MAX_NAMES_PER_CALL, return only
	 * MAX_NAMES_PER_CALL string. Else if input argument is less than or equal
	 * to 0, return null.
	 * 
	 * @param numberOfNames
	 *            Number of names to be returned in the array of strings.
	 * @return Array string containing numberOfNames names
	 */
	public String[] getNames(int numberOfNames) {
		// Invalid Input or Empty list
		if (numberOfNames <= 0 || listOfNames.isEmpty())
			return null;

		// Greater than the number of names in the list
		if (numberOfNames > listOfNames.size())
			numberOfNames = listOfNames.size();

		// Generate random set of strings from listOfNames
		Random r = new Random();
		ArrayList<String> names = new ArrayList<String>(numberOfNames);

		for (int i = 0; i < numberOfNames; i++) {
			String name = listOfNames.get(r.nextInt(listOfNames.size()));
			if (!names.contains(name))
				names.add(name);
			else
				i--;
		}

		// Turn arrayList into String[] manually
		String[] strNames = new String[numberOfNames];
		for (int i = 0; !names.isEmpty(); i++)
			strNames[i] = names.remove(0);

		return strNames;
	}

	/**
	 * Default way to get the list of bot names. It return a set of 4 bot names
	 * 
	 * @return
	 */
	public String[] getNames() {
		return this.getNames(DEFAULT_NUMBER_NAMES);
	}

	/**
	 * Return a instance of AIPokerPlayerNames. If one has been created then
	 * return that instance else create a new instance.
	 * 
	 * @return Instance of AIPokerPlayerNames
	 */
	public static AIPokerPlayerNames getInstance() {
		if (aiPokerPlayerNames == null)
			aiPokerPlayerNames = new AIPokerPlayerNames();
		return aiPokerPlayerNames;
	}
}
