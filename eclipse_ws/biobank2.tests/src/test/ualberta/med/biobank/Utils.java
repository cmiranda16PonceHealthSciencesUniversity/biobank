package test.ualberta.med.biobank;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;

public class Utils {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private static final int ALPHABET_LEN = ALPHABET.length();

    private static Random r = new Random();

    public static String getRandomString(int maxlen) {
        String str = new String();
        for (int j = 0, n = r.nextInt(maxlen) + 1; j < n; ++j) {
            int begin = r.nextInt(ALPHABET_LEN - 1);
            str += ALPHABET.substring(begin, begin + 1);
        }
        return str;
    }

    public static Date getRandomDate() throws ParseException {
        String dateStr = String.format("%04d-%02d-%02d %02d:%02d", 2000 + r
            .nextInt(40), r.nextInt(12) + 1, r.nextInt(30) + 1,
            r.nextInt(24) + 1, r.nextInt(60) + 1);
        return DateFormatter.dateFormatter.parse(dateStr);
    }

}
