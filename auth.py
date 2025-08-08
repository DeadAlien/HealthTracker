from models import load_users, save_users, User

def register_user(email, password, mobile=None, social_id=None, **profile):
    users = load_users()
    if email in users:
        return False, 'Email already registered.'
    user = User(email, password, mobile, social_id, **profile)
    users[email] = user.to_dict()
    save_users(users)
    return True, 'Registration successful.'

def login_user(email, password):
    users = load_users()
    user = users.get(email)
    if not user:
        return False, 'User not found.'
    if user['password'] != password:
        return False, 'Incorrect password.'
    return True, user
