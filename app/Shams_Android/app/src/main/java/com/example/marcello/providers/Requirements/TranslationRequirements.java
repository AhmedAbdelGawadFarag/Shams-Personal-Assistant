package com.example.marcello.providers.Requirements;

import java.util.ArrayList;

public class TranslationRequirements {
    public static class Translate{
        private static final String MESSAGE_SENTENCE = "ما الذى تريد ترجمته؟";
        private static final String MESSAGE_TARGET_LANGUAGE = "أى اللغات تريد الترجمه اليها؟";


        private static final String ENTITY_SENTENCE = "sentence";
        private static final String ENTITY_TARGET_LANGUAGE = "targetLanguage";
        public static final ArrayList<String> MESSAGES = new ArrayList<String>(){
            {
                add(MESSAGE_SENTENCE);
                add(MESSAGE_TARGET_LANGUAGE);
            }
        };
        public static final ArrayList<String> REQUIREMENTS = new ArrayList<String>(){
            {
                add(ENTITY_SENTENCE);
                add(ENTITY_TARGET_LANGUAGE);
            }
        };
    }
}
