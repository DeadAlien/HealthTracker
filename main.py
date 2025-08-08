from auth import register_user, login_user
from profile import get_user_profile, update_user_profile

def main():
    while True:
        print("\nHealth Tracker")
        print("1. Register")
        print("2. Login")
        print("3. Exit")
        choice = input("Choose an option: ").strip()
        if choice == '1':
            email = input("Email: ")
            password = input("Password: ")
            mobile = input("Mobile (optional): ")
            age = input("Age: ")
            gender = input("Gender: ")
            height = input("Height (cm): ")
            weight = input("Current Weight (kg): ")
            goal_weight = input("Goal Weight (kg): ")
            activity = input("Activity Level: ")
            diet = input("Dietary Preferences/Restrictions: ")
            fitness_goals = input("Fitness Goals: ")
            success, msg = register_user(
                email, password, mobile=mobile,
                age=age, gender=gender, height=height, weight=weight,
                goal_weight=goal_weight, activity=activity, diet=diet, fitness_goals=fitness_goals
            )
            print(msg)
        elif choice == '2':
            email = input("Email: ")
            password = input("Password: ")
            success, user = login_user(email, password)
            if not success:
                print(user)
            else:
                print(f"Welcome, {user['email']}!")
                while True:
                    profile = get_user_profile(email)
                    print("Profile:")
                    for k, v in profile.items():
                        print(f"  {k}: {v}")
                    action = input("(E)dit profile, (L)ogout: ").strip().lower()
                    if action == 'e':
                        updates = {}
                        for field in profile:
                            val = input(f"{field} [{profile[field]}]: ")
                            if val:
                                updates[field] = val
                        if updates:
                            update_user_profile(email, **updates)
                            print("Profile updated.")
                    elif action == 'l':
                        break
                    else:
                        print("Invalid option.")
        elif choice == '3':
            print("Goodbye!")
            break
        else:
            print("Invalid choice.")

if __name__ == "__main__":
    main()
