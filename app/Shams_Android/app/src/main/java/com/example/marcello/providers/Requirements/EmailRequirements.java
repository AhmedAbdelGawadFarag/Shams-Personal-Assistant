package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class EmailRequirements {
    private static final String TAG = "EmailRequirements";

    public static class ComposeEmail {
        private static final String MESSAGE_SEND_TO = "ما هو ايميل المرسل اليه؟";
        private static final String MESSAGE_SUBJECT = "ما هو موضوع الايميل؟";
        private static final String MESSAGE_BODY = "ما محتوى الايميل؟";


        private static final String ENTITY_SEND_TO = "sendTo";
        private static final String ENTITY_SUBJECT = "subject";
        private static final String ENTITY_BODY = "body";

        public static final ArrayList<String> MESSAGES = new ArrayList<String>() {
            {
                add(MESSAGE_SEND_TO);
                add(MESSAGE_SUBJECT);
                add(MESSAGE_BODY);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>() {
            {
                add(ENTITY_SEND_TO);
                add(ENTITY_SUBJECT);
                add(ENTITY_BODY);
            }
        };
    }
}
