import random

def generate_meal_plan(profile, goal):
    # Example foods for each diet type
    foods = {
        'High Protein': ['Grilled chicken', 'Paneer tikka', 'Egg whites', 'Tofu stir-fry', 'Greek yogurt'],
        'Vegetarian': ['Dal', 'Paneer curry', 'Mixed veg', 'Chana masala', 'Curd rice'],
        'Vegan': ['Quinoa salad', 'Chickpea curry', 'Tofu stir-fry', 'Vegan smoothie', 'Lentil soup'],
        'Balanced/Mixed': ['Chicken curry', 'Veg pulao', 'Fish fry', 'Mixed dal', 'Chapati sabzi'],
        'Low Carb / Keto': ['Paneer bhurji', 'Egg omelette', 'Chicken salad', 'Zucchini noodles', 'Avocado salad'],
        'High Carb': ['Rice', 'Banana', 'Potato curry', 'Oats porridge', 'Sweet potato'],
        'Paleo': ['Grilled fish', 'Egg salad', 'Fruit bowl', 'Chicken roast', 'Veg stir-fry'],
        'Gluten-Free': ['Rice', 'Dosa', 'Idli', 'Poha', 'Sabudana khichdi'],
        'No preference': ['Upma', 'Poha', 'Paratha', 'Dosa', 'Egg curry']
    }
    # Calorie targets by goal
    calories = {'Fat Loss': 1500, 'Muscle Gain': 2500, 'Weight Maintenance': 2000}
    user_diet = profile.get('diet', 'Balanced/Mixed')
    user_goal = goal if goal in calories else 'Weight Maintenance'
    cal_target = calories[user_goal]
    plan = []
    for meal in ['Breakfast', 'Lunch', 'Snack', 'Dinner']:
        meal_food = random.choice(foods.get(user_diet, foods['Balanced/Mixed']))
        plan.append({'meal': meal, 'food': meal_food, 'calories': cal_target // 4})
    return plan

def generate_workout_schedule(profile, goal, location='home'):
    # Example workouts
    home_workouts = {
        'Fat Loss': ['HIIT', 'Jump rope', 'Bodyweight circuit', 'Yoga'],
        'Muscle Gain': ['Push-ups', 'Pull-ups', 'Squats', 'Dips'],
        'Weight Maintenance': ['Brisk walk', 'Yoga', 'Stretching', 'Cycling']
    }
    gym_workouts = {
        'Fat Loss': ['Treadmill', 'Elliptical', 'HIIT', 'Rowing'],
        'Muscle Gain': ['Bench press', 'Deadlift', 'Squats', 'Lat pulldown'],
        'Weight Maintenance': ['Treadmill', 'Cycling', 'Stretching', 'Swimming']
    }
    user_goal = goal if goal in home_workouts else 'Weight Maintenance'
    workouts = gym_workouts if location == 'gym' else home_workouts
    schedule = []
    for day in range(1, 8):
        workout = random.choice(workouts[user_goal])
        schedule.append({'day': f'Day {day}', 'workout': workout})
    return schedule

def generate_routine(profile, goal, location='home', period='weekly'):
    routines = []
    if period == 'daily':
        meal_plan = generate_meal_plan(profile, goal)
        workout = generate_workout_schedule(profile, goal, location)[0]
        routines.append({'day': 'Today', 'meals': meal_plan, 'workout': workout})
    else:  # weekly or on-demand
        meal_plans = [generate_meal_plan(profile, goal) for _ in range(7)]
        workouts = generate_workout_schedule(profile, goal, location)
        for i in range(7):
            routines.append({'day': f'Day {i+1}', 'meals': meal_plans[i], 'workout': workouts[i]})
    return routines
