import os
import threading
from time import sleep

import requests
import sqlite3
#import imdb_poster_downloader as imdb

CONFIG_PATTERN = 'http://api.themoviedb.org/3/configuration?api_key={key}'
IMG_PATTERN = 'http://api.themoviedb.org/3/movie/{imdbid}/images?api_key={key}'
KEY = 'b3796ecc20d451f99ed543c4c813d071'


def _get_json(url):
    r = requests.get(url)
    return r.json()


def _download_images(filename, urls, path='.'):
    global _downloaded
    global _saved
    try:
        for nr, url in enumerate(urls):
            r = requests.get(url)
            filepath = os.path.join(path, filename + ".jpeg")
            _downloaded += 1
            with open(filepath, 'wb') as w:
                w.write(r.content)
                _saved += 1
            print("downloaded" + str(_downloaded) + " saved " + str(_saved))
    except:
        print("downloading error" + str(_missing_poster) + "poster " + filename)


def get_poster_urls(imdbid):
    global _missing_poster

    try:
        posters = _get_json(IMG_PATTERN.format(key=KEY, imdbid=imdbid))['posters']
    except:
        _missing_poster += 1
        print("error in " + str(_missing_poster) + "poster " + str(imdbid))
        return

    poster_urls = []
    # for poster in posters:

    if posters:
        rel_path = posters[0]['file_path']
        url = "{0}{1}{2}".format(base_url, 'w342', rel_path)
        poster_urls.append(url)
        if poster_urls is None:
            rel_path = posters[0]['file_path']
            url = "{0}{1}{2}".format(base_url, 'w500', rel_path)
            poster_urls.append(url)
            if poster_urls is None:
                rel_path = posters[0]['file_path']
                url = "{0}{1}{2}".format(base_url, 'original', rel_path)
                poster_urls.append(url)
    else:
        _missing_poster += 1
        print("missing" + str(_missing_poster) + "poster " + imdbid)
        return
    return poster_urls


def create_connection():
    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\imdb.db")
    except sqlite3.Error as e:
        print(e)

    return conn


_counter = 0
_missing_poster = 0
_downloaded = 0
_saved = 0
_started_downloaded = 0


def thread_function(imdbid, count=None, outpath='.'):
    global _counter
    urls = get_poster_urls(imdbid)
    if urls is not None:
        if count is not None:
            urls = urls[:count]
        _download_images(imdbid, urls, outpath)
    _counter -= 1


def tmdb_posters(imdbid, count=None, outpath='.'):
    x = threading.Thread(target=thread_function, args=(imdbid, count, outpath,))
    x.start()


if __name__ == '__main__':
    conn = create_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM ratings")

    rows = cur.fetchall()

    config = _get_json(CONFIG_PATTERN.format(key=KEY))
    base_url = config['images']['base_url']

    for row in rows:
        while _counter > 10:
            sleep(0)
        tmdb_posters(row[0])
        _counter += 1
