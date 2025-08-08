import json
import os
from datetime import date

LOGS_FILE = 'logs.json'

# Load all logs from file
def load_logs():
    if os.path.exists(LOGS_FILE):
        with open(LOGS_FILE, 'r') as f:
            return json.load(f)
    return {}

# Save all logs to file
def save_logs(logs):
    with open(LOGS_FILE, 'w') as f:
        json.dump(logs, f, indent=2)

# Add or update a log for a user and date
def log_activity(email, log_date=None, meals=None, workout=None, water=None, sleep=None, weight=None, measurements=None):
    logs = load_logs()
    if not log_date:
        log_date = str(date.today())
    user_logs = logs.get(email, {})
    entry = user_logs.get(log_date, {})
    if meals is not None:
        entry['meals'] = meals
    if workout is not None:
        entry['workout'] = workout
    if water is not None:
        entry['water'] = water
    if sleep is not None:
        entry['sleep'] = sleep
    if weight is not None:
        entry['weight'] = weight
    if measurements is not None:
        entry['measurements'] = measurements
    user_logs[log_date] = entry
    logs[email] = user_logs
    save_logs(logs)
    return True

# Get all logs for a user
def get_user_logs(email):
    logs = load_logs()
    return logs.get(email, {})
