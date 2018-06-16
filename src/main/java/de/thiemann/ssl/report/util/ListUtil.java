package de.thiemann.ssl.report.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtil {

    public static String stringListToString(List<String> stringArray) {
        if (stringArray == null)
            return new String();

        StringBuffer sb = new StringBuffer();
        for (String entry : stringArray) {
            sb.append(' ').append(entry).append(',');
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
