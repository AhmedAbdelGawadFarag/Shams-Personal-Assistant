from datetime import datetime, timedelta
from dateparser import parse
from pytz import all_timezones, timezone


tz = timezone('Egypt')

DAYS = {
    'الأحد': 'sunday',
    'الاثنين': 'monday',
    'الثلاثاء': 'tuesday',
    'الاربعاء': 'wednesday',
    'الخميس': 'thursday',
    'الجمعة': 'friday',
    'الجمعه': 'friday',
    'السبت': 'saturday'
}

TIMES = {
    'واحده': 1,
    'اثنين': 2,
    'اتنين': 2,
    'اثنان': 2,
    'ثلاثه': 3,
    'الثالثه': 3,
    'اربعه': 4,
    'الرابعه': 4,
    'خمسه': 5,
    'الخامسه': 5,
    'سته': 6,
    'السادسه': 6,
    'سبعه': 7,
    'السابعه': 7,
    'ثمانيه': 8,
    'الثامنه': 8,
    'تسعه': 9,
    'التاسعه': 9,
    'عشره': 10,
    'العاشره': 10,
    'احدا عشر': 11,
    'احد عشر': 11,
    'الحاديه عشر': 11,
    'اثنا عشر': 12,
    'الثانيه عشر': 12
}

PERIODS = {
    'صباحا': 'AM',
    'صباح': 'AM',
    'الصبح': 'AM',
    'مسائا': 'PM',
    'مساء': 'PM',
    'بالليل': 'PM'
}

MONTHS = {
    'يناير': 'january',
    'فبراير': 'february',
    'مارس': 'march',
    'ابريل': 'april',
    'مايو': 'may',
    'يونيو': 'june',
    'يوليو': 'july',
    'اغسطس': 'august',
    'سبتمبر': 'september',
    'اكتوبر': 'october',
    'نوفمبر': 'november',
    'ديسمبر': 'december'
}


class DateTimeExtractor:

    def __init__(self):
        pass

    def convert_to_day(self, word):
        day = None

        if DAYS.get(word) != None:
            day = parse(DAYS.get(word)+" EET", settings={
                        'PREFER_DATES_FROM': 'future'}).day

        if word.isdigit() and int(word) <= 31 and int(word) >= 1:
            return int(word)

        return day

    def search_for_day(self, words):

        currday = datetime.now(tz).day

        for i in range(0, len(words)):
            if words[i] == 'يوم':
                if i+1 < len(words) and self.convert_to_day(words[i+1]) != None:
                    return self.convert_to_day(words[i+1])

        cnt = 0
        for word in words:
            if word == 'بعد':
                cnt += 1
            if word == 'غدا' or word == 'بكرا' or word == 'غد' or word=='بكره':
                tmp = datetime.now(tz) + timedelta(days=cnt+1)
                return tmp.day

        return currday

    def convert_to_time(self, word, period="AM"):

        time = None

        if TIMES.get(word) != None or (word.isdigit() and int(word) <= 12 and int(word) >= 1):

            temp = TIMES.get(word)

            if temp == None:
                temp = word

            fr_time = '0{}:00'.format(
                temp) if int(temp) < 10 else '{}:00'.format(temp)
            time = parse(fr_time+' {}'.format(period) + " EET").time()
            time = str(time)

        elif word.find(':') != -1:
            time = parse(word+' {}'.format(period)+" EET").time()
            time = str(time)

        return time

    def search_for_time(self, words, period="AM"):

        date = datetime.now(tz) + timedelta(hours=1)
        time = str(date.time())

        for i in range(0, len(words)):
            if words[i] == 'الساعة' or words[i] == 'الساعه':

                # pass oly the first word after elsa3a
                if i+1 < len(words) and self.convert_to_time(words[i+1], period) != None:

                    return self.convert_to_time(words[i+1], period)

                elif i+2 < len(words) and self.convert_to_time(words[i+1] + " " + words[i+2], period) != None:

                    return self.convert_to_time(words[i+1]+" "+words[i+2], period)

        return time

    def search_for_period(self, words):

        period = "AM"
        for word in words:
            if PERIODS.get(word) != None:
                return PERIODS.get(word)

        return period

    def convert_to_month(self, word):

        month = None

        if MONTHS.get(word) != None:
            month = parse(MONTHS.get(word)+" EET").month
        elif word.isdigit() and int(word) >= 1 and int(word) <= 12:
            month = int(word)

        return month

    def search_for_month(self, words):
        month = datetime.now(tz).month

        for i in range(0, len(words)):
            if words[i] == 'شهر':
                if i+1 < len(words) and self.convert_to_month(words[i+1]) != None:
                    return self.convert_to_month(words[i+1])

        return month

    def search_for_year(self, words, month):

        year = datetime.now(tz).year

        if month < datetime.now(tz).month: #check if the month is already passed or not
            year += 1

        for i in range(0, len(words)):
            if words[i] == 'سنة' or words[i] == 'سنه':
                if i+1 < len(words) and words[i+1].isdigit():
                    year = words[i+1]

        return year

    def extract(self, sentence):
        words = sentence.split()

        for i in range(0, len(words)):
            words[i] = words[i].replace('.', '')

        day = self.search_for_day(words)
        period = self.search_for_period(words)
        time = self.search_for_time(words, period)
        month = self.search_for_month(words)
        year = self.search_for_year(words, month)

        return {
            'day': day,
            'time': time,
            'month': month,
            'year': year
        }

# examples 

# print(DateTimeExtractor().extract('حطلى ايفنت يوم 23 شهر مايو الساعه عشره'))


# print(DateTimeExtractor().extract('حطلى ايفنت بعد بكرا الساعه اثنين'))

# print(DateTimeExtractor().extract('حطلى منبه بعد بكره'))

# print(DateTimeExtractor().extract(' مساء اظبطلى المنبه الساعه سته بكره.'))


# print(DateTimeExtractor().extract('حطلى منبه بعد بعد بكرا الساعه 9 مساء سنه 2023 شهر مايو'))
