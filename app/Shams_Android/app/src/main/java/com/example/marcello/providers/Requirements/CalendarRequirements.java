package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class CalendarRequirements {

    public static class InsertCalendar{
        private static final String MESSAGE_TITLE = "ما هو اسم الايفينت.";
        private static final String MESSAGE_DESCRIPTION = "ما هو موضوع الايفينت.";
        private static final String MESSAGE_BEGIN_TIME = "متى موعد بدايه الايفينت.";
        private static final String MESSAGE_END_TIME = "متى موعد نهايه الايفينت.";

        private static final String ENTITY_TITLE = "title";
        private static final String ENTITY_DESCRIPTION = "description";
        private static final String ENTITY_BEGIN_TIME = "startDate";
        private static final String ENTITY_END_TIME = "endDate";

        public static final ArrayList<String> MESSAGES = new ArrayList<String>() {
            {
                add(MESSAGE_TITLE);
                add(MESSAGE_DESCRIPTION);
                add(MESSAGE_BEGIN_TIME);
                add(MESSAGE_END_TIME);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>() {
            {
                add(ENTITY_TITLE);
                add(ENTITY_DESCRIPTION);
                add(ENTITY_BEGIN_TIME);
                add(ENTITY_END_TIME);
            }
        };
    }
    public static class ReadCalendar{
        private static final String MESSAGE_BEGIN_TIME = "متى موعد بدايه الايفينت.";
        private static final String MESSAGE_END_TIME = "متى موعد نهايه الايفينت.";

        private static final String ENTITY_BEGIN_TIME = "startDate";
        private static final String ENTITY_END_TIME = "endDate";
        public static final ArrayList<String> MESSAGES = new ArrayList<String>() {
            {
                add(MESSAGE_BEGIN_TIME);
                add(MESSAGE_END_TIME);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>() {
            {
                add(ENTITY_BEGIN_TIME);
                add(ENTITY_END_TIME);
            }
        };
    }
}
