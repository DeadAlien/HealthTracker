from models import load_users, save_users

def get_user_profile(email):
    users = load_users()
    user = users.get(email)
    if not user:
        return None
    return user.get('profile', {})

def update_user_profile(email, **profile):
    # --- Input Validation (same as registration) ---
    for field in ['age', 'height', 'weight', 'goal_weight']:
        val = profile.get(field)
        if val is not None and val != '':
            try:
                if float(val) <= 0:
                    return False
            except Exception:
                return False
    if 'gender' in profile and profile['gender'] not in ['M', 'F', 'Other', '', None]:
        return False
    allowed_activities = [
        'Sedentary', 'Lightly Active', 'Moderately Active', 'Very Active', 'Super Active'
    ]
    if 'activity' in profile and profile['activity'] not in allowed_activities:
        return False
    allowed_diets = [
        'High Protein', 'Vegetarian', 'Vegan', 'Balanced/Mixed', 'Low Carb / Keto',
        'High Carb', 'Paleo', 'Gluten-Free', 'No preference'
    ]
    if 'diet' in profile and profile['diet'] not in allowed_diets:
        return False
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
                return False
    users = load_users()
    user = users.get(email)
    if not user:
        return False
    user['profile'].update(profile)
    users[email] = user
    save_users(users)
    return True
