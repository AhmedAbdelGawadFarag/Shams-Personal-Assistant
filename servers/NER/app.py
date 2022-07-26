import json

from flask import Flask, request, make_response
from assistant_functions.call_contact import get_caller_name
from helpers.colored_exception import logException
from helpers.arabic_date_time import DateTimeExtractor

app = Flask(__name__)


@app.route('/get_name', methods=['POST'])
def get_name_endpoint():
    try:
        text = request.get_json()['text']
        print("text is" + text)

        name = get_caller_name(text)
        print("name is " + name)
        return json.dumps({"data": name})

    except Exception as e:
        logException(e)
        return json.dumps({"Exception": str(e)})


@app.route('/', methods=['GET'])
def hello_world_endpoint():
    return json.dumps({"message": "hello world this is arabic virtual assistant team :D"})


@app.route('/get_date', methods=['POST'])
def test_date():
    try:
        text = request.get_json()['text']
        print(DateTimeExtractor().extract(text))
        return json.dumps(DateTimeExtractor().extract(text))
    except Exception as e:
        logException(e)
        return json.dumps({"Exception": str(e)})


if __name__ == '__main__':
    print("YES")
    app.run(host='0.0.0.0', port=5000)
