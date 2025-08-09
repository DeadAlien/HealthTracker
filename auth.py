import re

from models import load_users, save_users, add_user, get_user
from werkzeug.security import generate_password_hash, check_password_hash

def register_user(email, password, mobile=None, social_id=None, **profile):
    # --- Input Validation ---
    # Email format
    if not email or not re.match(r"^[\w\.-]+@[\w\.-]+\.\w+$", email):
        return False, 'Invalid email address.'
    # Password length
    if not password or len(password) < 6:
        return False, 'Password must be at least 6 characters.'
    # Age, height, weight, goal_weight: must be positive numbers
    for field in ['age', 'height', 'weight', 'goal_weight']:
        val = profile.get(field)
        if val is not None and val != '':
            try:
                if float(val) <= 0:
                    return False, f'{field.replace("_", " ").capitalize()} must be positive.'
            except Exception:
                return False, f'{field.replace("_", " ").capitalize()} must be a number.'
    # Gender: must be M/F/Other
    if 'gender' in profile and profile['gender'] not in ['M', 'F', 'Other', '', None]:
        return False, 'Gender must be M, F, or Other.'
    # Activity: must be one of allowed
    allowed_activities = [
        'Sedentary', 'Lightly Active', 'Moderately Active', 'Very Active', 'Super Active'
    ]
    if 'activity' in profile and profile['activity'] not in allowed_activities:
        return False, 'Invalid activity level.'
    # Diet: must be one of allowed
    allowed_diets = [
        'High Protein', 'Vegetarian', 'Vegan', 'Balanced/Mixed', 'Low Carb / Keto',
        'High Carb', 'Paleo', 'Gluten-Free', 'No preference'
    ]
    if 'diet' in profile and profile['diet'] not in allowed_diets:
        return False, 'Invalid diet preference.'
    # Fitness goals: must be list of allowed
    allowed_goals = [
        'Fat Loss', 'Muscle Gain', 'Weight Maintenance', 'Flexibility & Mobility',
        'Cardiovascular Health', 'Mental Well-being', 'Strength & Performance',
        'Endurance / Stamina', 'Medical Rehabilitation'
    ]
    fg = profile.get('fitness_goals')
    if fg:
        if isinstance(fg, str):
            fg = [fg]
        for g in fg:
            if g not in allowed_goals:
                return False, 'Invalid fitness goal.'
        if get_user(email):
            return False, 'Email already registered.'
        users = load_users()
    # Check mobile uniqueness
    if mobile:
        for u in users.values():
            if u.get('mobile') == mobile:
                return False, 'Mobile number already registered.'
    hashed_password = generate_password_hash(password)
    user = {
        'email': email,
        'password': hashed_password,
        'mobile': mobile,
        'age': profile.get('age'),
        'gender': profile.get('gender'),
        'height': profile.get('height'),
        'weight': profile.get('weight'),
        'goal_weight': profile.get('goal_weight'),
        'activity': profile.get('activity'),
        'diet': profile.get('diet'),
        'fitness_goals': profile.get('fitness_goals', [])
    }
    add_user(user)
    return True, 'Registration successful.'

def login_user(email, password):
    user = get_user(email)
    if not user:
        return False, 'User not found.'
    if not check_password_hash(user['password'], password):
        return False, 'Incorrect password.'
    return True, user
