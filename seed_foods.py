import sqlite3

DB_PATH = 'healthtracker.db'

# Example meals data
MEALS = [
    ("Grilled Chicken Breast", "200g", 330, 62, 0, 7, "Balanced/Mixed", "lunch"),
    ("Oatmeal with Berries", "1 cup", 250, 8, 45, 4, "Vegetarian", "breakfast"),
    ("Greek Yogurt", "150g", 120, 10, 15, 2, "Vegetarian", "snack"),
    ("Salmon Fillet", "150g", 280, 25, 0, 18, "Pescatarian", "dinner"),
    ("Quinoa Salad", "1 bowl", 220, 7, 35, 6, "Vegan", "lunch"),
]

SNACKS = [
    ("Almonds", 100, 4, 4, 9),
    ("Banana", 90, 1, 23, 0),
    ("Protein Bar", 200, 20, 25, 5),
]

SUPPLEMENTS = [
    ("Vitamin D", "Bone health, immunity"),
    ("Omega-3", "Heart health, anti-inflammatory"),
    ("Protein Powder", "Muscle recovery, satiety"),
]

def seed_meals():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    for m in MEALS:
        cur.execute("INSERT INTO meals (name, portion_size, calories, protein, carbs, fats, diet, meal_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", m)
    conn.commit()
    conn.close()

def seed_snacks():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    for s in SNACKS:
        cur.execute("INSERT INTO snacks (name, calories, protein, carbs, fats) VALUES (?, ?, ?, ?, ?)", s)
    conn.commit()
    conn.close()

def seed_supplements():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    for s in SUPPLEMENTS:
        cur.execute("INSERT INTO supplements (name, benefit) VALUES (?, ?)", s)
    conn.commit()
    conn.close()

if __name__ == "__main__":
    seed_meals()
    seed_snacks()
    seed_supplements()
    print("Meals, snacks, and supplements seeded.")
