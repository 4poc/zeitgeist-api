package li.zeitgeist.api;

class Utils { // package protected
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

