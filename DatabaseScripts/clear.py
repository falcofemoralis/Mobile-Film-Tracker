import sqlite3
import threading


def create_connection():
    conn = None
    try:
        conn = sqlite3.connect("E:\Developer\Projects\AndroidStudio\Resources\imdb.db")
    except sqlite3.Error as e:
        print(e)

    return conn


def clear_crew():
    crewTitleCursor = conn.cursor()
    crewTitleCursor.execute("SELECT DISTINCT crew.title_id FROM crew")
    titleRows = crewTitleCursor.fetchall()

    getFromRating = "SELECT ratings.rating FROM ratings WHERE ratings.title_id = ?"""
    deleteFromCrew = "DELETE FROM crew WHERE crew.title_id = ?"""

    count = 0
    listSize = len(titleRows)
    checkCursor = conn.cursor()
    cursor = conn.cursor()

    for title in titleRows:
        checkCursor.execute(getFromRating, (title[0],))
        checkRows = checkCursor.fetchall()
        if not checkRows:
            cursor.execute(deleteFromCrew, (title[0],))

        if (count % 1000) == 0:
            print("deleted " + str(count) + "/" + str(listSize))
            conn.commit()

        count += 1
    conn.commit()


def clear_people():
    personCursor = conn.cursor()
    personCursor.execute("SELECT people.person_id FROM people")
    persons = personCursor.fetchall()

    getPersonFromCrew = "SELECT crew.title_id FROM crew WHERE crew.person_id = ?"""
    deleteFromPeople = "DELETE FROM people WHERE people.person_id = ?"""

    count = 0
    listSize = len(persons)

    for person in persons:
        checkCursor = conn.cursor()
        checkCursor.execute(getPersonFromCrew, (person[0],))
        checkRows = checkCursor.fetchall()
        if not checkRows:
            cursor = conn.cursor()
            cursor.execute(deleteFromPeople, (person[0],))

        if (count % 1000) == 0:
            print("deleted " + str(count) + "/" + str(listSize))
            conn.commit()

        count += 1

    cursor.close()
    checkCursor.close()


def clear_titles():
    titlesCursor = conn.cursor()
    titlesCursor.execute("SELECT titles.title_id FROM titles")
    titleRows = titlesCursor.fetchall()

    getFromRating = "SELECT ratings.rating FROM ratings WHERE ratings.title_id = ?"""
    deleteFromCrew = "DELETE FROM titles WHERE titles.title_id = ?"""

    count = 0
    listSize = len(titleRows)

    for title in titleRows:
        checkCursor = conn.cursor()
        checkCursor.execute(getFromRating, (title[0],))
        checkRows = checkCursor.fetchall()
        if not checkRows:
            cursor = conn.cursor()
            cursor.execute(deleteFromCrew, (title[0],))

        if (count % 1000) == 0:
            print("deleted " + str(count) + "/" + str(listSize))
            conn.commit()

        count += 1
    conn.commit()
   # cursor.close()
    #   checkCursor.close()


def clear_akas():
    titlesCursor = conn.cursor()
    titlesCursor.execute("SELECT akas.title_id FROM akas")
    titleRows = titlesCursor.fetchall()

    getFromRating = "SELECT ratings.rating FROM ratings WHERE ratings.title_id = ?"""
    deleteFromCrew = "DELETE FROM akas WHERE akas.title_id = ?"""

    count = 0
    listSize = len(titleRows)

    for title in titleRows:
        checkCursor = conn.cursor()
        checkCursor.execute(getFromRating, (title[0],))
        checkRows = checkCursor.fetchall()
        if not checkRows:
            cursor = conn.cursor()
            cursor.execute(deleteFromCrew, (title[0],))

        if (count % 1000) == 0:
            print("deleted " + str(count) + "/" + str(listSize))
            conn.commit()

        count += 1

    conn.commit()


def clear_ratings():
    titlesCursor = conn.cursor()
    titlesCursor.execute("SELECT ratings.title_id FROM ratings")
    titleRows = titlesCursor.fetchall()

    getFromRating = "SELECT * FROM titles WHERE titles.title_id = ?"""
    deleteFromCrew = "DELETE FROM ratings WHERE ratings.title_id = ?"""

    count = 0
    listSize = len(titleRows)

    for title in titleRows:
        checkCursor = conn.cursor()
        checkCursor.execute(getFromRating, (title[0],))
        checkRows = checkCursor.fetchall()
        if not checkRows:
            cursor = conn.cursor()
            cursor.execute(deleteFromCrew, (title[0],))

        if (count % 1000) == 0:
            print("deleted " + str(count) + "/" + str(listSize))
            conn.commit()

        count += 1
    conn.commit()



if __name__ == '__main__':
    conn = create_connection()

    clear_crew()
    clear_people()
    #clear_titles()
    clear_akas()
    #clear_ratings()

    conn.close()
