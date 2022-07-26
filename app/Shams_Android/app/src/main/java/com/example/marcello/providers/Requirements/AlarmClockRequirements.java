package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class AlarmClockRequirements {
    public static class SetAlarm{
        private static final String MESSAGE_MINUTE = "minute";
        private static final String MESSAGE_HOUR = "hour";
        private static final String MESSAGE_DAY = "day";
        private static final String MESSAGE_MONTH = "month";
        private static final String MESSAGE_YEAR = "year";

        private static final String ENTITY_MINUTE = "minute";
        private static final String ENTITY_HOUR = "hour";
        private static final String ENTITY_DAY = "day";
        private static final String ENTITY_MONTH = "month";
        private static final String ENTITY_YEAR = "year";

        public static final ArrayList<String> MESSAGES = new ArrayList<String>() {
            {
                add(MESSAGE_MINUTE);
                add(MESSAGE_HOUR);
                add(MESSAGE_DAY);
                add(MESSAGE_MONTH);
                add(MESSAGE_YEAR);

            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>() {
            {
                add(ENTITY_MINUTE);
                add(ENTITY_HOUR);
                add(ENTITY_DAY);
                add(ENTITY_MONTH);
                add(ENTITY_YEAR);

            }
        };
    }
}
