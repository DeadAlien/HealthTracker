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

@app.route('/dashboard')
def dashboard():
    if 'email' not in session:
        return redirect(url_for('login'))
    email = session['email']
    profile = get_user_profile(email)
    return render_template('dashboard.html', email=email, profile=profile)

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
