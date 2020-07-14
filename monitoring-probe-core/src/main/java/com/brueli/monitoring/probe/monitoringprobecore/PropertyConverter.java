package com.brueli.monitoring.probe.monitoringprobecore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Use the PropertyConverter class to convert application.properties. 
 */
public class PropertyConverter {
    /**
     * convert a timespan string to a long value representing the number of milliseconds.
     * @param timespanString A timespan string. number + ms|s|m|h|d
     * @return number of milliseconds
     */
    Long convertTimespanString(String timespanString, String hint) throws RuntimeException {
        Pattern pattern = Pattern.compile("^(\\d+)(d|h|m|s|ms)?$", Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(timespanString);
        if (!match.matches()) {
            throw new RuntimeException("invalid " + hint);
        }
        double multiplier = 1;
        if (match.groupCount() == 2 && match.group(2) != null) {
            String multiplierName = match.group(2);
            if (multiplierName.equalsIgnoreCase("d")) {
                multiplier = 24 * 60 * 60 * 1000;
            } else if (multiplierName.equalsIgnoreCase("h")) {
                multiplier = 60 * 60 * 1000;
            } else if (multiplierName.equalsIgnoreCase("m")) {
                multiplier = 60 * 1000;
            } else if (multiplierName.equalsIgnoreCase("s")) {
                multiplier = 1000;
            } else if (multiplierName.equalsIgnoreCase("ms")) {
                multiplier = 1;
            } else {
                multiplier = 1;
            }
        }
        Double timespanNumber = Double.parseDouble(match.group(1));
        Double timespanValue = timespanNumber * multiplier;
        return (long)Math.round(timespanValue);
        
    }
}