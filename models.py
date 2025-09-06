from db import get_db, dict_from_row

def load_users():
    db = get_db()
    cur = db.execute('SELECT * FROM users')
    users = {}
    for row in cur.fetchall():
        user = dict_from_row(row)
        email = user['email']
        users[email] = user
    db.close()
    return users

def save_users(users):
    # Not needed with DB, but kept for compatibility
    pass

def add_user(user):
    db = get_db()
    db.execute('''INSERT INTO users (email, password, mobile, age, gender, height, weight, goal_weight, activity, diet, fitness_goals, allergies, dislikes, foods_to_avoid, preferred_workout_days, workout_time, goal_wizard)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)''',
        (
            user['email'], user['password'], user.get('mobile'), user.get('age'), user.get('gender'),
            user.get('height'), user.get('weight'), user.get('goal_weight'), user.get('activity'),
            user.get('diet'), ','.join(user.get('fitness_goals', [])),
            ','.join(user.get('allergies', [])),
            ','.join(user.get('dislikes', [])),
            ','.join(user.get('foods_to_avoid', [])),
            ','.join(user.get('preferred_workout_days', [])),
            user.get('workout_time'),
            user.get('goal_wizard')
        )
    )
    db.commit()
    db.close()

def get_user(email):
    db = get_db()
    cur = db.execute('SELECT * FROM users WHERE email = ?', (email,))
    user = dict_from_row(cur.fetchone())
    db.close()
    return user

def update_user(email, updates):
    db = get_db()
    fields = []
    values = []
    for k, v in updates.items():
        if k == 'profile_picture':
            # Special handling for profile picture to store the path
            fields.append("profile_picture=?")
            values.append(v)
        else:
            fields.append(f"{k}=?")
            # Convert lists to comma-separated strings for new fields
            if k in ['fitness_goals', 'allergies', 'dislikes', 'foods_to_avoid', 'preferred_workout_days'] and isinstance(v, list):
                values.append(','.join(v))
            else:
                values.append(v)

    if not fields:
        return

    query = f"UPDATE users SET {', '.join(fields)} WHERE email=?"
    values.append(email)
    
    db.execute(query, tuple(values))
    db.commit()
    db.close()

def delete_user(email):
    db = get_db()
    db.execute('DELETE FROM users WHERE email=?', (email,))
    db.commit()
    db.close()

# User class is not needed for DB-based CRUD, but can be re-added if needed for business logic
