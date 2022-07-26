package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class WebSearchRequirements {
    public static class WebSearch{
        private static final String MESSAGE_WEB_QUERY = "ما الذى تريد البحث عنه؟";

        private static final String ENTITY_WEB_QUERY = "searchQuery";
        public static final ArrayList<String> MESSAGES = new ArrayList<String>(){
            {
                add(MESSAGE_WEB_QUERY);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>(){
            {
                add(ENTITY_WEB_QUERY);
            }
        };
    }
}
