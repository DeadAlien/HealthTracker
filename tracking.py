
from db import get_db
from datetime import date

def log_activity(email, **kwargs):
    today = str(date.today())
    db = get_db()
    db.execute('''INSERT INTO logs (email, date, meals, workout, water, sleep, weight, measurements)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)''',
        (
            email,
            today,
            kwargs.get('meals'),
            kwargs.get('workout'),
            kwargs.get('water'),
            kwargs.get('sleep'),
            kwargs.get('weight'),
            kwargs.get('measurements')
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
