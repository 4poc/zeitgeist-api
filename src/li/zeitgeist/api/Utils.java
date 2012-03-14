package li.zeitgeist.api;

/**
 * Utility functions. (package protected)
 */
class Utils {
    /**
     * Join a list of strings together.
     * 
     * Uses the seperator to join a array of strings together
     * into one that is returned.
     * 
     * @param strings array of strings
     * @param sep used as a delimiter string.
     * @return joined string
     */
    public static String join(String[] strings, String sep) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                stringBuilder.append(sep);
            }
            stringBuilder.append(strings[i]);
        }

        return stringBuilder.toString();
    }
}

