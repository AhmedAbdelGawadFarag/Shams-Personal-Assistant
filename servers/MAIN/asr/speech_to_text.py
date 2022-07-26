from google.cloud import speech_v1 as speech
from google.cloud.speech import RecognitionAudio
from colored_exception import logException
import os

config = dict(language_code="ar-EG")

# config = speech.types.RecognitionConfig(
#   language_code='ar-EG'
# ),

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = './asr/asr-api-key.json'


def get_text(filename):
    try:
        with open(filename, 'rb') as f1:
            audio = f1.read()
            audio = RecognitionAudio(content=audio)
            client = speech.SpeechClient()
            response = client.recognize(config=config, audio=audio)
            return print_sentences(response)

    except Exception as e:
        logException(e)
        return None


def print_sentences(response):
    for result in response.results:
        best_alternative = result.alternatives[0]
        transcript = best_alternative.transcript
        confidence = best_alternative.confidence
        print("-" * 80)
        print(f"Transcript: {transcript}")
        print(f"Confidence: {confidence:.0%}")
        return transcript
