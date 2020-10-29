import sqlite3


def create_connection():
    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\imdb.db")
    except sqlite3.Error as e:
        print(e)

    return conn


if __name__ == '__main__':
    conn = create_connection()
    genresCursor = conn.cursor()
    genresCursor.execute("SELECT * FROM genres")
    all_genres = genresCursor.fetchall()

    titlesCursor = conn.cursor()
    titlesCursor.execute("SELECT titles.title_id, titles.genres FROM titles")
    titleRows = titlesCursor.fetchall()

    cur = conn.cursor()
    saved = 0
    _all = len(titleRows)


    for title in titleRows:
        genresSplitted = [x.strip() for x in title[1].split(',')]

        new_genres = ""
        for genre in genresSplitted:
            for i in range(0, len(all_genres)):
                if all_genres[i][1] == genre:
                    new_genres += str(all_genres[i][0]) + ","
                    break

        updateQuery = "UPDATE titles SET genres = ?"" WHERE titles.title_id = ?"""
        cur.execute(updateQuery, (new_genres, title[0]))
        conn.commit()
        print("saved " + str(saved) + "/" + str(_all))
        saved += 1



