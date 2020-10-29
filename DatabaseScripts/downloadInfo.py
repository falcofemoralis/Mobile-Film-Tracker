from imdb import IMDb
import os
import threading
from time import sleep
import requests
import sqlite3

_saved = 1
_all = 0
ia = None

def save(plot, title_id):
    global _saved, _all
    try:
        cur = conn.cursor()
        updateQuery = "UPDATE titles SET plot = ?"" WHERE titles.title_id = ?"""
        cur.execute(updateQuery, (plot, title_id))
        conn.commit()
        print("saved " + str(_saved) + "/" + str(_all))
        _saved += 1
    except:
        print("saving error " + title_id)


def thread_function(pid):
    global ia

    title_id = pid
    title_new_id = title_id.replace('tt', '')

    try:
        infoset = ia.get_movie_plot(title_new_id)
        save(infoset['data']['plot'][0], pid)
    except:
        print("no plot")


if __name__ == '__main__':
    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\imdb.db")
    except sqlite3.Error as e:
        print(e)

    # create an instance of the IMDb class
    ia = IMDb()

    cur = conn.cursor()
    cur.execute("SELECT titles.title_id from titles")

    rows = cur.fetchall()
    _all = len(rows)

    _saved = 15115
    for i in range(15115, _all+1):
        thread_function(rows[i][0])







