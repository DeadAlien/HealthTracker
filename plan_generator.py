
import random

from db import get_db

def load_meals():
    db = get_db()
    try:
        cur = db.execute('SELECT * FROM meals')
        meals = [dict(zip([col[0] for col in cur.description], row)) for row in cur.fetchall()]
        print(f"Loaded {len(meals)} meals from DB")
        return meals
    except Exception as e:
        print(f"Error loading meals from DB: {e}")
        return []
    finally:
        db.close()

def generate_meal_plan(profile, goal):
    meals = load_meals()
    db = get_db()
    # Calorie targets by goal
    calories = {'Fat Loss': 1500, 'Muscle Gain': 2500, 'Weight Maintenance': 2000}
    user_diet = profile.get('diet', 'Balanced/Mixed')
    user_goal = goal if goal in calories else 'Weight Maintenance'
    cal_target = calories[user_goal]
    # Split calories: breakfast, lunch, dinner, snacks
    split = {'Breakfast': 0.25, 'Lunch': 0.3, 'Dinner': 0.35, 'Snack': 0.1}
    plan = []
    for meal in ['Breakfast', 'Lunch', 'Snack', 'Dinner']:
        meal_type = meal.lower()
        options = [m for m in meals if m['meal_type'] == meal_type and (m['diet'] == user_diet or user_diet == 'No preference')]
        if not options:
            options = [m for m in meals if m['meal_type'] == meal_type]
        if not options:
            plan.append({'meal': meal, 'food': 'No meal found', 'portion_size': '', 'calories': 0, 'protein': 0, 'carbs': 0, 'fats': 0})
        else:
            meal_food = random.choice(options)
            plan.append({
                'meal': meal,
                'food': meal_food['name'],
                'portion_size': meal_food.get('portion_size', ''),
                'calories': meal_food.get('calories', 0),
                'protein': meal_food.get('protein', 0),
                'carbs': meal_food.get('carbs', 0),
                'fats': meal_food.get('fats', 0)
            })
    # Add snack suggestions
    cur = db.execute('SELECT * FROM snacks')
    snacks = [dict(zip([col[0] for col in cur.description], row)) for row in cur.fetchall()]
    if snacks:
        snack = random.choice(snacks)
        plan.append({'meal': 'Extra Snack', 'food': snack['name'], 'portion_size': '', 'calories': snack['calories'], 'protein': snack['protein'], 'carbs': snack['carbs'], 'fats': snack['fats']})
    # Add supplement suggestion
    cur = db.execute('SELECT * FROM supplements')
    supplements = [dict(zip([col[0] for col in cur.description], row)) for row in cur.fetchall()]
    if supplements:
        supp = random.choice(supplements)
        plan.append({'meal': 'Supplement', 'food': supp['name'], 'portion_size': '', 'calories': 0, 'protein': 0, 'carbs': 0, 'fats': 0, 'benefit': supp['benefit']})
    db.close()
    return plan

def load_exercises():
    db = get_db()
    try:
        cur = db.execute('SELECT * FROM exercises')
        exercises = [dict(zip([col[0] for col in cur.description], row)) for row in cur.fetchall()]
        print(f"Loaded {len(exercises)} exercises from DB")
        return exercises
    except Exception as e:
        print(f"Error loading exercises from DB: {e}")
        return []
    finally:
        db.close()

def generate_workout_schedule(profile, goal, location='home'):
    exercises = load_exercises()
    # Filter by location and (optionally) by goal/type
    filtered = [e for e in exercises if e['location'] == location]
    if not filtered:
        print(f"No exercises found for location {location}, using all exercises as fallback.")
        filtered = exercises
    schedule = []
    for day in range(1, 8):
        if not filtered:
            schedule.append({'day': f'Day {day}', 'workout': 'No exercise found'})
        else:
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
