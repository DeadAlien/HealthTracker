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
            log_date TEXT,
            meals TEXT,
            workout TEXT,
            water REAL,
            sleep_hours REAL,
            weight REAL,
            notes TEXT,
            FOREIGN KEY(email) REFERENCES users(email)
        );
        CREATE TABLE IF NOT EXISTS meals (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            portion_size TEXT,
            calories INTEGER,
            protein REAL,
            carbs REAL,
            fats REAL,
            diet TEXT,
            meal_type TEXT
        );
        CREATE TABLE IF NOT EXISTS snacks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            calories INTEGER,
            protein REAL,
            carbs REAL,
            fats REAL
        );
        CREATE TABLE IF NOT EXISTS supplements (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            benefit TEXT
        );
        CREATE TABLE IF NOT EXISTS hydration_goals (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT,
            daily_goal REAL,
            FOREIGN KEY(email) REFERENCES users(email)
        );
        CREATE TABLE IF NOT EXISTS advice_tips (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT,
            tip TEXT,
            log_date TEXT,
            FOREIGN KEY(email) REFERENCES users(email)
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
