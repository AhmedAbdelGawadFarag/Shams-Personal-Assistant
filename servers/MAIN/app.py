import traceback
from flask import Flask, request, send_file
import json
import trained_model.intent_classifier as intent_classifier
from helpers import handleUpload, stt
from colored_exception import logException
from asr.text_to_speech import generate_voice_file

app = Flask(__name__)

# load intent classification model
classifier = intent_classifier.classifier()


@app.route('/', methods=['GET', 'POST'])
def hello():
    return json.dumps({"data": "hello world"})


@app.route('/upload', methods=['POST'])
def upload_file():
    try:
        return handleUpload(classifier)
    except Exception as e:
        logException(e)
        return json.dumps({"exception": str(e)})


@app.route('/home', methods=['GET'])
def sayHi():
    return json.dumps({"data": "hello world"})


@app.route('/intent_test', methods=['POST'])
def testIntent():
    try:
        return json.dumps({"intent": classifier.predict(request.json.get('text'))})
    except Exception as e:
        print(traceback.format_exc())
        logException(e)
        return json.dumps({"exception": str(e)})


@app.route('/tts', methods=['POST'])
def text_to_speech():
    try:
        generate_voice_file(request.get_json()['text'])
        return send_file('./tts.mp3', as_attachment=True)
    except Exception as e:
        logException(e)
        return json.dumps({'exception': str(e)})


@app.route('/stt', methods=['POST'])
def speech_to_text():
    try:
        text = stt()
        return json.dumps({'userSTT': text})
    except Exception as e:
        logException(e)
        return json.dumps({'exception': str(e)})


if __name__ == '__main__':
    print("YES")
    app.run(host='0.0.0.0', port=5001)
