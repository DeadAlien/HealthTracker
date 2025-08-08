def get_fitness_advice(profile):
    advice = []
    goal = profile.get('fitness_goals', ['Weight Maintenance'])
    if isinstance(goal, list):
        goal = goal[0] if goal else 'Weight Maintenance'
    activity = profile.get('activity', 'Sedentary')
    diet = profile.get('diet', 'Balanced/Mixed')
    weight = float(profile.get('weight', 0))
    goal_weight = float(profile.get('goal_weight', 0))

    # Fitness advice
    if goal == 'Fat Loss':
        advice.append("Aim for a moderate calorie deficit (300-500 kcal/day) for sustainable fat loss.")
        advice.append("Combine cardio and strength training for best results.")
    elif goal == 'Muscle Gain':
        advice.append("Increase protein intake and focus on progressive overload in strength training.")
        advice.append("Aim for a small calorie surplus (200-300 kcal/day).")
    elif goal == 'Weight Maintenance':
        advice.append("Maintain a balanced diet and regular physical activity.")
    # Activity advice
    if activity == 'Sedentary':
        advice.append("Try to add short walks or light activity throughout your day.")
    elif activity == 'Lightly Active':
        advice.append("Gradually increase exercise frequency or intensity for more benefits.")
    # Diet advice
    if diet == 'High Protein':
        advice.append("High protein diets support muscle gain and satiety.")
    elif diet == 'Vegetarian' or diet == 'Vegan':
        advice.append("Ensure adequate protein and vitamin B12 intake.")
    elif diet == 'Low Carb / Keto':
        advice.append("Monitor energy levels and include healthy fats.")
    # General
    advice.append("Stay hydrated and aim for 7-9 hours of sleep per night.")
    return advice

def get_goal_feedback(profile):
    weight = float(profile.get('weight', 0))
    goal_weight = float(profile.get('goal_weight', 0))
    if weight and goal_weight:
        diff = abs(weight - goal_weight)
        if diff < 2:
            return "Your goal is very close to your current weight. Consider focusing on maintenance or body composition."
        elif diff > 20:
            return "Your goal is ambitious. Break it into smaller milestones for better success."
        else:
            return "Your goal is realistic. Stay consistent and track your progress!"
    return "Set your current and goal weight for personalized feedback."
