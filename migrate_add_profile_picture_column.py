import sqlite3

def migrate():
    conn = sqlite3.connect('healthtracker.db')
    cursor = conn.cursor()
    
    try:
        cursor.execute("ALTER TABLE users ADD COLUMN profile_picture TEXT")
        print("Column 'profile_picture' added to 'users' table.")
    except sqlite3.OperationalError as e:
        if "duplicate column name" in str(e):
            print("Column 'profile_picture' already exists.")
        else:
            raise e
            
    conn.commit()
    conn.close()

if __name__ == '__main__':
    migrate()
