from models import load_users, save_users

def get_user_profile(email):
    users = load_users()
    user = users.get(email)
    if not user:
        return None
    return user.get('profile', {})

def update_user_profile(email, **profile):
    users = load_users()
    user = users.get(email)
    if not user:
        return False
    user['profile'].update(profile)
    users[email] = user
    save_users(users)
    return True
