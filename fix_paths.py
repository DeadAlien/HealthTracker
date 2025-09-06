import sqlite3
import os

db_file = 'healthtracker.db'
if not os.path.exists(db_file):
    print(f"Database file '{db_file}' not found.")
else:
    try:
        conn = sqlite3.connect(db_file)
        cursor = conn.cursor()

        # Check for profile_picture column
        cursor.execute("PRAGMA table_info(users)")
        columns = [info[1] for info in cursor.fetchall()]
        if 'profile_picture' not in columns:
            print("The 'users' table does not have a 'profile_picture' column.")
        else:
            # Find users with incorrect paths
            cursor.execute("SELECT email, profile_picture FROM users WHERE profile_picture LIKE 'static/uploads/%'")
            users_to_fix = cursor.fetchall()

            if not users_to_fix:
                print("No incorrect profile picture paths were found in the database.")
            else:
                print(f"Found {len(users_to_fix)} user(s) with incorrect paths. Fixing now...")
                for email, old_path in users_to_fix:
                    if old_path:
                        new_path = old_path.replace('static/', '', 1)
                        print(f"Updating path for {email}: from '{old_path}' to '{new_path}'")
                        cursor.execute('UPDATE users SET profile_picture = ? WHERE email = ?', (new_path, email))
                
                conn.commit()
                print("Finished fixing profile picture paths in the database.")
                
    except sqlite3.Error as e:
        print(f"A database error occurred: {e}")
    finally:
        if 'conn' in locals() and conn:
            conn.close()
