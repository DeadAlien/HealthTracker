import sqlite3

# Migration script to add new columns to users table if missing
DB_PATH = 'healthtracker.db'
COLUMNS = [
    ('allergies', 'TEXT'),
    ('dislikes', 'TEXT'),
    ('foods_to_avoid', 'TEXT'),
    ('preferred_workout_days', 'TEXT'),
    ('workout_time', 'TEXT'),
    ('goal_wizard', 'TEXT'),
]

def add_missing_columns():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    # Get existing columns
    cursor.execute("PRAGMA table_info(users);")
    existing = set(row[1] for row in cursor.fetchall())
    for col, coltype in COLUMNS:
        if col not in existing:
            cursor.execute(f"ALTER TABLE users ADD COLUMN {col} {coltype};")
    conn.commit()
    conn.close()

if __name__ == "__main__":
    add_missing_columns()
    print("Migration complete.")
