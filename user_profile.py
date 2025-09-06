from models import load_users, save_users

def get_user_profile(email):
    from models import get_user
    user = get_user(email)
    if not user:
        return None
    # Remove password from profile dict
    profile = dict(user)
    profile.pop('password', None)
    profile.pop('email', None)
    return profile

def update_user_profile(email, **profile):
    # If no new data is provided at all, it's a success.
    if not profile:
        return True
        
    # Separate profile_picture from other fields that need validation
    profile_picture_path = profile.pop('profile_picture', None)

    # If the only thing provided was an empty profile picture, and no other data, it's a success
    if not profile and not profile_picture_path:
        return True

    # --- Input Validation (same as registration) ---
    for field in ['age', 'height', 'weight', 'goal_weight']:
        val = profile.get(field)
        if val is not None and val != '':
            try:
                # Allow empty strings to pass through, they will be ignored later
                if str(val).strip() == '': continue
                if float(val) <= 0:
                    return False
            except (ValueError, TypeError):
                return False
    if 'gender' in profile and profile.get('gender') not in ['M', 'F', 'Other', '', None]:
        return False
    allowed_activities = [
        'Sedentary', 'Lightly Active', 'Moderately Active', 'Very Active', 'Super Active'
    ]
    if 'activity' in profile and profile.get('activity') not in allowed_activities:
        return False
    allowed_diets = [
        'High Protein', 'Vegetarian', 'Vegan', 'Balanced/Mixed', 'Low Carb / Keto',
        'High Carb', 'Paleo', 'Gluten-Free', 'No preference'
    ]
    if 'diet' in profile and profile.get('diet') not in allowed_diets:
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
        if not isinstance(fg, list): # Ensure it's a list
             return False
        for g in fg:
            if g not in allowed_goals:
                return False
                
    from models import update_user, get_user
    user = get_user(email)
    if not user:
        return False
        
    # Prepare updates for DB, starting with the validated fields
    updates = {}
    for k, v in profile.items():
        # Ignore fields that are empty strings
        if v == '':
            continue
        # Handle string-to-list conversion for specific fields
        if k in ['fitness_goals', 'allergies', 'dislikes', 'foods_to_avoid', 'preferred_workout_days'] and isinstance(v, str):
            updates[k] = [x.strip() for x in v.split(',') if x.strip()]
        else:
            updates[k] = v
            
    # Add the profile picture path to the updates if it exists
    if profile_picture_path:
        updates['profile_picture'] = profile_picture_path

    # If after filtering, there are no actual updates, it's a success
    if not updates:
        return True

    update_user(email, updates)
    return True
