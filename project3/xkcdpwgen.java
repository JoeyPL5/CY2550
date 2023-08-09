import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class xkcdpwgen {
    private static List<String> words; // List of all possible words for password
    // FLAGS with default values
    private static int numWords = 4;
    private static int caps = 0;
    private static int numbers = 0;
    private static int symbols = 0;

    /**
     * Rolls an outcome from the given probability.
     *
     * @param chance # outcomes that return true
     * @param bound # possible outcomes
     * @return the generated outcome
     */
    public static boolean roll(int chance, int bound) {
        Random rand = new Random();
        return (rand.nextInt(bound) < chance);
    }

    /**
     * Import 'words.txt' into a List of Strings
     */
    private static void importWords() {
        try {
            Path filepath = Paths.get("words.txt");
            words = Files.readAllLines(filepath);
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * Pull a random word from the words field
     * @return a String with one word
     */
    private static String getRandomWord() {
        Random rand = new Random();
        return words.get(rand.nextInt(words.size()));
    }

    /**
     * Returns a random symbol from the possible symbol
     *
     * @return the symbol
     */
    private static Character getRandomSymbol() {
        Random rand = new Random();
        String symbols = "~!@#$%^&*.:;";
        return symbols.charAt(rand.nextInt(12));
    }

    /**
     * Update the value of the given field based on the given arguments.
     *
     * @param field field to update the value of
     * @param args command line arguments
     * @param i current argument
     */
    private static void updateField(AtomicReference<Integer> field, String[] args, int i) {
        try {
            field.set(Integer.parseInt(args[i]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("An invalid parameter for an argument was given.");
        }
    }

    /**
     * Gets the help message for the password generator.
     *
     * @return the message as a string
     */
    private static String getHelp() {
        return "usage: xkcdpwgen [-h] [-w WORDS] [-c CAPS] [-n NUMBERS] [-s SYMBOLS]\n" +
                "                \n" +
                "Generate a secure, memorable password using the XKCD method\n" +
                "                \n" +
                "optional arguments:\n" +
                "    -h, --help            show this help message and exit\n" +
                "    -w WORDS, --words WORDS\n" +
                "                          include WORDS words in the password (default=4)\n" +
                "    -c CAPS, --caps CAPS  capitalize the first letter of CAPS random words\n" +
                "                          (default=0)\n" +
                "    -n NUMBERS, --numbers NUMBERS\n" +
                "                          insert NUMBERS random numbers in the password\n" +
                "                          (default=0)\n" +
                "    -s SYMBOLS, --symbols SYMBOLS\n" +
                "                          insert SYMBOLS random symbols in the password\n" +
                "                          (default=0)";
    }

    /**
     * Read in the given command-line arguments.
     * @param args command-line arguments
     */
    private static void readArgs(String[] args) {
        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help"))) {
            System.out.println(getHelp());
            return;
        }
        for (int i = 0; i < args.length; i++) {
            AtomicReference<Integer> field = new AtomicReference<Integer>();
            switch (args[i]) {
                case "--words":
                case "-w":
                    i++;
                    field.set(numWords);
                    updateField(field, args, i);
                    numWords = field.get();
                    break;
                case "--caps":
                case "-c":
                    i++;
                    field.set(caps);
                    updateField(field, args, i);
                    caps = field.get();
                    break;
                case "--numbers":
                case "-n":
                    i++;
                    field.set(numbers);
                    updateField(field, args, i);
                    numbers = field.get();
                    break;
                case "--symbols":
                case "-s":
                    i++;
                    field.set(symbols);
                    updateField(field, args, i);
                    symbols = field.get();
                    break;
                default:
                    throw new IllegalArgumentException("An invalid argument was given.");
            }
        }
        System.out.println(generatePassword());
    }

    /**
     * Capitalizes the given word.
     *
     * @param word the word to capitalize
     * @return the capitalized word
     */
    private static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    /**
     * Decides based on a counter and a roll whether the capitalization, number, or symbol can be added to the word.
     *
     * @param currentIdx current word index
     * @param count the number of times the field must be inserted before the end of the password
     * @return true if it can be inserted
     */
    private static boolean canInsert(int currentIdx, int count) {
        return (count > 0 && roll(count, numWords - currentIdx));
    }

    /**
     * Generates the password based on the fields using the XKCD method.
     *
     * @return the generated password
     */
    private static String generatePassword() {
        Random rand = new Random();
        String password = "";
        for (int i = 0; i < numWords; i++) {
            String currentWord = getRandomWord();
            if (canInsert(i, caps)) {
                caps--;
                currentWord = capitalize(currentWord);
            }
            while (canInsert(i, numbers)) {
                numbers--;
                currentWord += rand.nextInt(10);
            }
            while (canInsert(i, symbols)) {
                symbols--;
                currentWord += getRandomSymbol();
            }
            password += currentWord;
        }
        return password;
    }

    public static void main(String[] args) {
        importWords();
        readArgs(args);
    }

}
