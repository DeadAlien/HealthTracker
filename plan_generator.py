
import random
import json
import os

def load_meals():
    with open('meals.json', 'r') as f:
        return json.load(f)

def generate_meal_plan(profile, goal):
    meals = load_meals()
    # Calorie targets by goal
    calories = {'Fat Loss': 1500, 'Muscle Gain': 2500, 'Weight Maintenance': 2000}
    user_diet = profile.get('diet', 'Balanced/Mixed')
    user_goal = goal if goal in calories else 'Weight Maintenance'
    cal_target = calories[user_goal]
    plan = []
    for meal in ['Breakfast', 'Lunch', 'Snack', 'Dinner']:
        options = [m for m in meals if m['diet'] == user_diet or user_diet == 'No preference']
        if not options:
            options = meals
        meal_food = random.choice(options)
        plan.append({'meal': meal, 'food': meal_food['name'], 'calories': meal_food['calories']})
    return plan

def load_exercises():
    with open('exercises.json', 'r') as f:
        return json.load(f)

def generate_workout_schedule(profile, goal, location='home'):
    exercises = load_exercises()
    # Filter by location and (optionally) by goal/type
    filtered = [e for e in exercises if e['location'] == location]
    if not filtered:
        filtered = exercises
    schedule = []
    for day in range(1, 8):
        workout = random.choice(filtered)
        schedule.append({'day': f'Day {day}', 'workout': workout['name']})
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
