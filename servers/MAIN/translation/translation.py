from tashaphyne.stemming import ArabicLightStemmer

languages = {
    "عربى": "ar",
    "انجليزى": "en",
    "فرنساوى": "fr",
    "بولندى": "pl",
    "المانى": "de",
    'روسى': 'ru',
    'يابانى': 'ja',
    'برتغالى': 'pt',
    'تركى': 'tr',
    'سويدى': 'sv',
    'ايطالى': 'it',
    'هولندى': 'nl'

}

useless_words = {
    'ترجملى': True,
    'ترجملي': True,
    'ترجم': True,
    'ترجمه': True,
    'بترجمه': True,
    'قم': True,
    'حول': True,
    'ترجمه': True,
    'معناها': True,
    'معنى': True,
    'ازاى': True,
    'الى': True,
    'الي': True,
    'اللغه': True,
    'اللغة': True,
    'لغه': True,
    'لى': True,
    'غه': True,
    'رجم': True,
    'ترجمل': True,
    'زاى': True,
}


def similar_characters(s1, s2):  # get similar characters between two strings
    mp = {}

    for i in s1:

        if i == 'ى' or i == 'ي':
            mp['ى'] = True
            mp['ي'] = True

        mp[i] = True

    cnt = 0

    for i in s2:
        if mp.get(i) == True:
            cnt += 1

    return cnt


def get_closer_language(lan):  # get the closest language to passed lan paramter

    ArListem = ArabicLightStemmer()

    lan = ArListem.light_stem(lan)

    maxi = 0
    closer_lan = 0

    for i in languages:
        cnt = similar_characters(lan, i)
        if maxi < cnt:
            maxi = cnt
            closer_lan = i

    return closer_lan, cnt


def process(sentence):
    words = sentence.split(' ')

    maxi = 0
    closest_lan = -1

    lan_in_sentece = ''

    for word in words:
        lan, cnt = get_closer_language(word)

        if maxi < cnt:
            maxi = cnt
            closest_lan = languages.get(lan)
            lan_in_sentece = word

    desired_sentence = ''

    for word in words:
        stem = ArabicLightStemmer().light_stem(word)
        if useless_words.get(stem) != True and useless_words.get(word) != True and word != lan_in_sentece:
            desired_sentence += word + " "

    return closest_lan, desired_sentence
