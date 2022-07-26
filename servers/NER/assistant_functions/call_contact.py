from ner_model import tagger
from transformers import AutoModelForTokenClassification, AutoTokenizer
import pyarabic.araby as araby

tokenizer = AutoTokenizer.from_pretrained('hatmimoha/arabic-ner', do_lower_case=False)
model = AutoModelForTokenClassification.from_pretrained("hatmimoha/arabic-ner")


def get_caller_name(text):
    tags = tagger.tag(text, tokenizer, model)
    print(tags)
    person_name = ""

    for i in tags['entities']:
        if i["type"] == 'PERSON':
            person_name = i["entity"]
            break

    if len(person_name) > 0 and person_name[0] == araby.BEH:
        person_name = person_name[1:]
    print("person name : " + person_name)
    return person_name

