import os
import traceback

import tensorflow as tf
from tensorflow import keras
from transformers import BertTokenizer, TFBertModel
from colored_exception import logException
from trained_model.preprocess import ArabertPreprocessor


class classifier:
    def __init__(self):
        try:
            # model_path = "./trained_model/intent-model"
            # /home/ahmed/Desktop/ArabicIntentClassification/Arabic-Virutal-Assistant-Server/trained_model/intent-model



            self.arabert_prep = ArabertPreprocessor(model_name='aubmindlab/bert-base-arabertv02-twitter')

            model_path = os.getenv('intent_model_path')

            print(model_path)
            self.MAX_LENGHT = 32

            self.classes = ['Question', 'Search', 'New Calendar', 'Read Calendar', 'Send Emails', 'Read Emails',
                            'Call contact', 'new contact', 'weather', 'open app', 'Read notification', 'Translation',
                            'Rejection', 'Acceptance', 'greetings', 'alarm']

            self.model = keras.models.load_model(model_path, custom_objects={"TFBertModel": TFBertModel})
            self.tokenizer = BertTokenizer.from_pretrained("aubmindlab/bert-base-arabertv02-twitter")
        except Exception as e:
            print(traceback.format_exc())
            logException(e)

    def predict(self, text):
        pre = self.arabert_prep.preprocess(text)
        ids = self.tokenizer(pre, return_tensors="tf", padding='max_length', max_length=self.MAX_LENGHT)['input_ids']
        result = self.model.predict(ids)
        return self.classes[tf.math.argmax(result[0])]
