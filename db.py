import sqlite3
from contextlib import closing

DB_NAME = 'healthtracker.db'

def get_db():
    conn = sqlite3.connect(DB_NAME)
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    with closing(get_db()) as db:
        db.executescript('''
        CREATE TABLE IF NOT EXISTS users (
            email TEXT PRIMARY KEY,
            password TEXT NOT NULL,
            mobile TEXT UNIQUE,
            age INTEGER,
            gender TEXT,
            height REAL,
            weight REAL,
            goal_weight REAL,
            activity TEXT,
            diet TEXT,
            fitness_goals TEXT,
            allergies TEXT,
            dislikes TEXT,
            foods_to_avoid TEXT,
            preferred_workout_days TEXT,
            workout_time TEXT,
            goal_wizard TEXT
        );
        CREATE TABLE IF NOT EXISTS logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT,
            date TEXT,
            meals TEXT,
            workout TEXT,
            water REAL,
            sleep REAL,
            weight REAL,
            measurements TEXT,
            FOREIGN KEY(email) REFERENCES users(email)
        );
        CREATE TABLE IF NOT EXISTS meals (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            diet TEXT,
            calories INTEGER
        );
        CREATE TABLE IF NOT EXISTS exercises (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            location TEXT
        );
        ''')
        db.commit()

# Migration helpers to seed meals and exercises from JSON
def seed_meals_from_json(json_path='meals.json'):
    import json
    db = get_db()
    with open(json_path, 'r') as f:
        meals = json.load(f)
        for m in meals:
            db.execute('INSERT INTO meals (name, diet, calories) VALUES (?, ?, ?)',
                       (m['name'], m['diet'], m['calories']))
    db.commit()
    db.close()

def seed_exercises_from_json(json_path='exercises.json'):
    import json
    db = get_db()
    with open(json_path, 'r') as f:
        exercises = json.load(f)
        for e in exercises:
            db.execute('INSERT INTO exercises (name, location) VALUES (?, ?)',
                       (e['name'], e['location']))
    db.commit()
    db.close()

def dict_from_row(row):
    return dict(zip(row.keys(), row)) if row else None
