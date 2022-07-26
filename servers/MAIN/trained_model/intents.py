import json
import os

import requests

from translation import translation

ner_ip = os.getenv('ner_server_ip')


def call_contact(text):
    r = requests.post(ner_ip + '/get_name', json={"text": text})
    print("response is : " + r.text)

    r = json.loads(r.text)
    print(r['data'])

    return json.dumps({'intent': "call contact", "displayName": r['data'], "userSTT": text}), 200, {
        'ContentType': 'application/json'}


def new_calendar(text):
    r = requests.post(ner_ip + '/get_date', json={"text": text})
    print(r)

    print("response is : " + r.text)

    r = json.loads(r.text)

    startDate = str(r['year']) + '-' + str(r['month']) + '-' + str(r['day']) + ' ' + str(r['time'])

    mp = {
        'intent': 'new calendar',
        'startDate': startDate,
        'endDate': startDate,
        "userSTT": text
    }

    print(r)
    print(mp)

    return json.dumps(mp)


def read_calendar(text):
    r = requests.post(ner_ip + '/get_date', json={"text": text})

    print(r)

    print("response is : " + r.text)

    r = json.loads(r.text)

    startDate = str(r['year']) + '-' + str(r['month']) + '-' + str(r['day']) + ' ' + str(r['time'])

    return json.dumps({
        'intent': 'read calendar',
        'startDate': startDate,
        'endDate': startDate,
        "userSTT": text
    })


def search(text):
    words = text.split(' ')

    search_result = ''

    indx = -1

    for i in range(0, len(words)):
        if words[i] == 'عن' or words[i] == 'على':
            indx = i + 1
            break

    for i in range(indx, len(words)):
        search_result += words[i] + ' '

    print(search_result)

    return json.dumps({
        "intent": "web search",
        "searchQuery": search_result,
        "userSTT": text
    })


def new_contact(text):
    r = requests.post(ner_ip + '/get_name', json={"text": text})
    print(r)
    r = json.loads(r.text)
    print(r)
    print(r['data'])

    return json.dumps({'intent': "add contact", "displayName": r['data'], "userSTT": text}), 200, {
        'ContentType': 'application/json'}


def new_alarm(text):
    r = requests.post(ner_ip + '/get_date', json={"text": text})

    r = json.loads(r.text)

    print(r)

    hour = r['time'][0] + r['time'][1]
    minute = r['time'][3] + r['time'][4]

    mp = {
        'intent': 'set alarm',
        'hour': int(hour),
        'minute': int(minute),
        'day': r['day'],
        'month': r['month'],
        'year': r['year'],
        "userSTT": text
    }

    print(mp)

    return json.dumps(mp)


def read_notifications(text):
    return json.dumps({
        "intent": 'read notification'
        , "userSTT": text
    }
    )


def read_emails(text):
    return json.dumps({
        "intent": "read email"
        , "userSTT": text
    })


def send_emails(text):
    return json.dumps(
        {
            "intent": "send email"
            , "userSTT": text
        }
    )


def open_apps(text):
    return json.dumps({
        "intent": "open app"
        , "userSTT": text
    })


def translate(text):
    lan, sentence = translation.process(text)
    print(len)
    print(sentence)
    return json.dumps({
        "intent": 'translate',
        "targetLanguage": lan,
        "sentence": sentence
        , "userSTT": text
    })


def greetings(text):
    return json.dumps(
        {
            "intent": "greetings"
            , "userSTT": text
        }
    )
