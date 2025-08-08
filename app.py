from guidance import get_fitness_advice, get_goal_feedback
from tracking import log_activity, get_user_logs
from plan_generator import generate_routine
from flask import Flask, render_template, request, redirect, url_for, session, flash
from auth import register_user, login_user
from profile import get_user_profile, update_user_profile
import os

app = Flask(__name__)
app.secret_key = 'your_secret_key_here'

@app.route('/')
def home():
    if 'email' in session:
        return redirect(url_for('dashboard'))
    return render_template('home.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        mobile = request.form.get('mobile')
        age = request.form.get('age')
        gender = request.form.get('gender')
        height = request.form.get('height')
        weight = request.form.get('weight')
        goal_weight = request.form.get('goal_weight')
        activity = request.form.get('activity')
        diet = request.form.get('diet')
        fitness_goals = request.form.getlist('fitness_goals')
        success, msg = register_user(
            email, password, mobile=mobile,
            age=age, gender=gender, height=height, weight=weight,
            goal_weight=goal_weight, activity=activity, diet=diet, fitness_goals=fitness_goals
        )
        flash(msg)
        if success:
            return redirect(url_for('login'))
    return render_template('register.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        success, user = login_user(email, password)
        if success:
            session['email'] = email
            return redirect(url_for('dashboard'))
        else:
            flash(user)
    return render_template('login.html')

@app.route('/dashboard', methods=['GET', 'POST'])
def dashboard():
    if 'email' not in session:
        return redirect(url_for('login'))
    email = session['email']
    profile = get_user_profile(email)
    routine = None
    period = request.args.get('period', 'weekly')
    location = request.args.get('location', 'home')
    goal = profile.get('fitness_goals', ['Weight Maintenance'])
    if isinstance(goal, list):
        goal = goal[0] if goal else 'Weight Maintenance'
    if request.args.get('generate'):
        routine = generate_routine(profile, goal, location, period)

    # Handle activity/weight log POST
    if request.method == 'POST':
        meals = request.form.get('meals')
        workout = request.form.get('workout')
        water = request.form.get('water')
        sleep = request.form.get('sleep')
        weight = request.form.get('weight')
        measurements = request.form.get('measurements')
        log_activity(email, meals=meals, workout=workout, water=water, sleep=sleep, weight=weight, measurements=measurements)
        flash('Log saved!')
        return redirect(url_for('dashboard'))

    logs = get_user_logs(email)
    # Prepare progress summary (last 7 days)
    sorted_dates = sorted(logs.keys(), reverse=True)
    recent_logs = [dict(date=d, **logs[d]) for d in sorted_dates[:7]]
    advice = get_fitness_advice(profile)
    goal_feedback = get_goal_feedback(profile)
    return render_template('dashboard.html', email=email, profile=profile, routine=routine, logs=recent_logs, advice=advice, goal_feedback=goal_feedback)
@app.route('/routine', methods=['GET'])
def routine():
    if 'email' not in session:
        return redirect(url_for('login'))
    email = session['email']
    profile = get_user_profile(email)
    period = request.args.get('period', 'weekly')
    location = request.args.get('location', 'home')
    goal = profile.get('fitness_goals', ['Weight Maintenance'])
    if isinstance(goal, list):
        goal = goal[0] if goal else 'Weight Maintenance'
    routine = generate_routine(profile, goal, location, period)
    return render_template('routine.html', routine=routine, period=period, location=location)

@app.route('/edit', methods=['GET', 'POST'])
def edit():
    if 'email' not in session:
        return redirect(url_for('login'))
    email = session['email']
    profile = get_user_profile(email)
    if request.method == 'POST':
        updates = {}
        for field in profile:
            if field == 'fitness_goals':
                val = request.form.getlist('fitness_goals')
            else:
                val = request.form.get(field)
            if val:
                updates[field] = val
        if updates:
            update_user_profile(email, **updates)
            flash('Profile updated!')
            return redirect(url_for('dashboard'))
    return render_template('edit.html', profile=profile)

@app.route('/logout')
def logout():
    session.pop('email', None)
    return redirect(url_for('home'))

if __name__ == '__main__':
    app.run(debug=True)
