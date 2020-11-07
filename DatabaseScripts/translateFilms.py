import threading
from time import sleep

from imdb import IMDb
import sqlite3
from googletrans import Translator

_saved = 1
_all = 0
_counter = 0
ia = None
translator = None


def save(plot, title, film):
    global _saved, _all
    try:
        cur = conn.cursor()
        insertQuery = "INSERT INTO `films_translations`(`title_id`, `title`, `plot`, `lang_id`) VALUES (?"",?"",?"", 3)"
        cur.execute(insertQuery, (film[0], title, plot))
        conn.commit()
        print("saved " + str(_saved) + "/" + str(_all))
        _saved += 1
    except:
        print("saving error " + film[0])


def thread_function(film):
    global ia, translator

    title_id = film[0]
    title_new_id = title_id.replace('tt', '')

    info = ia.get_movie_akas(title_new_id)

    try:
        titles = info['data']['akas']
        title = titles[0]
        for _title in titles:
            if _title.find('Russia') != -1:
                title = _title
                break
    except:
        title = film[1]

    title = title.replace(' Russia', '')
    title = title.replace(' Soviet Union', '')
    title = title.replace(' (original title)', '')
    title = title.replace(' (new title)', '')
    title = title.replace(' (Russian title)', '')

    textToTranslate = film[2]
    if textToTranslate is None: textToTranslate = " "
    while True:
        try:
            # translator = Translator()
            translated_text = translator.translate(textToTranslate, dest="ru")
            break
        except Exception as e:
            translator = Translator()

    save(translated_text.text, title, film)


if __name__ == '__main__':
    translator = Translator()
    ia = IMDb()

    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\wimdb.db")
    except sqlite3.Error as e:
        print(e)

    cur = conn.cursor()
    cur.execute("SELECT * from films_translations where films_translations.lang_id = 1")
    films = cur.fetchall()

    _all = len(films)
    _saved = 20224

    for i in range(20224, _all + 1):
        thread_function(films[i])
