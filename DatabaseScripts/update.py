from imdb import IMDb
import os
import threading
from time import sleep
import requests
import sqlite3

_saved = 1
_all = 0
ia = None

def update(title_id):
    global _saved, _all
    try:
        cur = conn.cursor()
        updateQuery = "UPDATE titles SET lang_id = ?"" WHERE titles.title_id = ?"""
        cur.execute(updateQuery, (1, title_id))
        conn.commit()
        print("saved " + str(_saved) + "/" + str(_all))
        _saved += 1
    except:
        print("saving error " + title_id)

if __name__ == '__main__':
    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\wimdb.db")
    except sqlite3.Error as e:
        print(e)

    cur = conn.cursor()
    cur.execute("SELECT titles.title_id from titles")

    rows = cur.fetchall()
    _all = len(rows)

    _saved = 0
    for i in range(0, _all+1):
        update(rows[i][0])







