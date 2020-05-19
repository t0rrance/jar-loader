package sample.jarloader.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static boolean hasSufix(String path, String sufix) {
        Pattern pattern = Pattern.compile("(?=" + sufix + ")");
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

}
