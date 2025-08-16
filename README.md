# HealthTracker

A full-stack health and fitness tracker web application built with Python and Flask. Supports user registration, login, personalized meal and workout plans, daily activity logging, progress tracking, and evidence-based guidance. Easily extendable for Android integration via REST API.

## Features
- User registration/login (email, mobile)
- User profiles: age, gender, height, weight, goals, activity, diet, preferences
- Personalized meal and workout plan generator (daily/weekly/on-demand)
- Log daily meals, workouts, water intake, sleep, weight, and measurements
- Progress summaries and (optionally) graphs
- Evidence-based fitness and nutrition advice
- Goal-setting tools and feedback
- Content/data management: meals and exercises in JSON files
- Ready for Android app integration via REST API (see below)

## Getting Started

### Prerequisites
- Python 3.x
- pip

### Installation
1. Clone the repository or download the source code.
2. Install dependencies:
   ```bash
   pip install flask flask-cors
   ```
3. (Optional) For Android integration, ensure `flask-cors` is installed.

### Running the App
```bash
python app.py
```
Visit [http://127.0.0.1:5000/](http://127.0.0.1:5000/) in your browser.

### Project Structure
- `app.py` - Main Flask app (web and API routes)
- `models.py` - User model and data persistence
- `auth.py` - Registration and login logic
- `tracking.py` - Daily activity and progress logging
- `plan_generator.py` - Meal and workout plan generation
- `guidance.py` - Fitness/nutrition advice and goal feedback
- `users.json`, `logs.json` - User and log data
- `meals.json`, `exercises.json` - Content databases
- `templates/` - HTML templates for web frontend

### Android Integration
- The backend can be used as a REST API for Android apps.
- Example endpoints: `/api/register`, `/api/login`, `/api/profile`, `/api/plan`, `/api/log`, `/api/logs`
- Use HTTP requests from your Android app to interact with the backend.

## Customization
- Add new meals/exercises by editing `meals.json` and `exercises.json`.
- Update advice logic in `guidance.py`.
- Extend with more features as needed!

## Security Notes
- Passwords are stored in plain text for demo purposes. Use hashing for production.
- For production, use a real database and secure deployment.

## License
MIT
