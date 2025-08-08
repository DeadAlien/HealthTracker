import json
import os

USERS_FILE = 'users.json'

def load_users():
    if os.path.exists(USERS_FILE):
        with open(USERS_FILE, 'r') as f:
            return json.load(f)
    return {}

def save_users(users):
    with open(USERS_FILE, 'w') as f:
        json.dump(users, f, indent=2)

class User:
    def __init__(self, email, password, mobile=None, social_id=None, **profile):
        self.email = email
        self.password = password
        self.mobile = mobile
        self.social_id = social_id
        # Ensure fitness_goals is always a list
        if 'fitness_goals' in profile and not isinstance(profile['fitness_goals'], list):
            if isinstance(profile['fitness_goals'], str):
                profile['fitness_goals'] = [profile['fitness_goals']]
            else:
                profile['fitness_goals'] = list(profile['fitness_goals'])
        self.profile = profile

    def to_dict(self):
        return {
            'email': self.email,
            'password': self.password,
            'mobile': self.mobile,
            'social_id': self.social_id,
            'profile': self.profile
        }

    @staticmethod
    def from_dict(data):
        return User(
            data['email'],
            data['password'],
            data.get('mobile'),
            data.get('social_id'),
            **data.get('profile', {})
        )
