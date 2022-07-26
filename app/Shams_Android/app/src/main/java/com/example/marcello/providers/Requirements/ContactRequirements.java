package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class ContactRequirements {

    public static class CallContact{
        private static final String MESSAGE_CONTACT_NAME = "بمن تريد الاتصال؟";

        private static final String ENTITY_CONTACT_NAME = "displayName";
        public static final ArrayList<String> MESSAGES = new ArrayList<String>(){
            {
                add(MESSAGE_CONTACT_NAME);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>(){
            {
                add(ENTITY_CONTACT_NAME);
            }
        };

    }
    public static class RemoveContact{
        private static final String MESSAGE_CONTACT_NAME = "contact name";

        private static final String ENTITY_CONTACT_NAME = "displayName";
        public static final ArrayList<String> MESSAGES = new ArrayList<String>(){
            {
                add(MESSAGE_CONTACT_NAME);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>(){
            {
                add(ENTITY_CONTACT_NAME);
            }
        };

    }


    public static class AddContact{
        private static final String MESSAGE_CONTACT_NAME = "ماذا تريد الاسم؟";
        private static final String MESSAGE_CONTACT_NUMBER = "ما هو الرقم؟";

        private static final String ENTITY_CONTACT_NAME = "displayName";
        private static final String ENTITY_CONTACT_NUMBER = "phoneNumber";

        public static final ArrayList<String> MESSAGES = new ArrayList<String>(){
            {
                add(MESSAGE_CONTACT_NAME);
                add(MESSAGE_CONTACT_NUMBER);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>(){
            {
                add(ENTITY_CONTACT_NAME);
                add(ENTITY_CONTACT_NUMBER);
            }
        };
    }
}

