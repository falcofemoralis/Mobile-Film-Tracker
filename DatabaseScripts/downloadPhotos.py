from imdb import IMDb
import os
import threading
from time import sleep
import requests
import sqlite3

_saved = 1
_counter = 0
_all = 0
ia = None

def _download_images(photo_url, filename, path='.'):
    global _saved, _counter, _all
    try:
        r = requests.get(photo_url)
        filepath = os.path.join(path, filename + ".jpeg")
        with open(filepath, 'wb') as w:
            w.write(r.content)
            _saved += 1
        print("saved " + str(_saved) + "/" + str(_all))
    except:
        print("downloading error " + filename)


def thread_function(pid, count=None, outpath='.'):
    global _counter, ia
    _counter += 1

    person_id = pid
    person_new_id = person_id.replace('nm', '')
    # for high quality
    # gr = ia.get_person_filmography(person_new_id)\
    try:
        gr = ia.get_person_biography(person_new_id)
        _download_images(gr['data']['headshot'], pid, outpath)
    except:
        print("no photo")
    _counter -= 1


if __name__ == '__main__':
    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\imdb.db")
    except sqlite3.Error as e:
        print(e)

    # create an instance of the IMDb class
    ia = IMDb()

    cur = conn.cursor()
    cur.execute("SELECT people.person_id FROM people")

    rows = cur.fetchall()
    _all = len(rows)

    for row in rows:
        if (_saved % 2000) == 0:
            print("start sleeping...")
            sleep(35)
        else:
            while _counter > 8:
                sleep(0)
            x = threading.Thread(target=thread_function, args=(row[0],))
            x.start()
