package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class OpenAppRequirements {

    public static class OpenApp{
        private static final String MESSAGE_APP_NAME = "ما هو اسم الابلكيشن الذى تريد فتحه؟";

        private static final String ENTITY_APP_NAME = "appName";

        public static final ArrayList<String> MESSAGES = new ArrayList<String>() {
            {
                add(MESSAGE_APP_NAME);

            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>() {
            {
                add(ENTITY_APP_NAME);

            }
        };
    }
}
