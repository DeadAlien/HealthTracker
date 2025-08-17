
from db import get_db
from datetime import date

def log_activity(email, **kwargs):
    log_date = kwargs.get('log_date')
    log_day = kwargs.get('log_day')
    db = get_db()
    # Check if log already exists for this email and date
    cur = db.execute('SELECT id FROM logs WHERE email=? AND log_date=?', (email, log_date))
    row = cur.fetchone()
    # Add columns if missing
    try:
        db.execute('ALTER TABLE logs ADD COLUMN breakfast TEXT')
    except Exception: pass
    try:
        db.execute('ALTER TABLE logs ADD COLUMN lunch TEXT')
    except Exception: pass
    try:
        db.execute('ALTER TABLE logs ADD COLUMN snacks TEXT')
    except Exception: pass
    try:
        db.execute('ALTER TABLE logs ADD COLUMN dinner TEXT')
    except Exception: pass
    if row:
        db.execute('''UPDATE logs SET breakfast=?, lunch=?, snacks=?, dinner=?, workout=?, water=?, sleep_hours=?, weight=?, measurements=?, notes=? WHERE email=? AND log_date=?''',
            (
                kwargs.get('breakfast'),
                kwargs.get('lunch'),
                kwargs.get('snacks'),
                kwargs.get('dinner'),
                kwargs.get('workout'),
                kwargs.get('water'),
                kwargs.get('sleep'),
                kwargs.get('weight'),
                kwargs.get('measurements'),
                log_day,
                email,
                log_date
            )
        )
    else:
        db.execute('''INSERT INTO logs (email, log_date, breakfast, lunch, snacks, dinner, workout, water, sleep_hours, weight, measurements, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)''',
            (
                email,
                log_date,
                kwargs.get('breakfast'),
                kwargs.get('lunch'),
                kwargs.get('snacks'),
                kwargs.get('dinner'),
                kwargs.get('workout'),
                kwargs.get('water'),
                kwargs.get('sleep'),
                kwargs.get('weight'),
                kwargs.get('measurements'),
                log_day
            )
        )
    db.commit()
    db.close()

def get_user_logs(email):
    db = get_db()
    cur = db.execute('SELECT * FROM logs WHERE email = ?', (email,))
    logs = {}
    for row in cur.fetchall():
        log = dict(zip([col[0] for col in cur.description], row))
        logs[log['date']] = log
    db.close()
    return logs
