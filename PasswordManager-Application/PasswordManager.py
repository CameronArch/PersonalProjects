import json
import random
import string
import sqlite3   

class PasswordContainer:
    def __init__(self, password, website, username):
        self.password = password
        self.website = website
        self.username = username

    def __str__(self):
        return f"Website: {self.website}\nUsername: {self.username}\nPassword: {self.password}"

    def change_password(self, new_password):
        self.password = new_password
    
    def change_username(self, new_username):
        self.username = new_username
    
    def change_website(self, new_website):
        self.website = new_website
    
    def get_password(self):
        return self.password
    
    def get_username(self):
        return self.username
    
    def get_website(self):
        return self.website
    
    def serialize(self):
        return json.dumps(self.__dict__)
    
    def deserialize(cls, data):
        return cls(**json.loads(data))
    
def generate_password(length = 12):
    characters = string.ascii_letters + string.digits + string.punctuation

    password = []
    password.append(string.ascii_lowercase)
    password.append(string.ascii_uppercase)
    password.append(string.digits)
    password.append(string.punctuation)

    for i in range(length - 4):
        password.append(random.choice(characters))
    
    random.shuffle(password)

    password = "".join(password)

    return password

def rate_password(password):
    score = 0

    length_score = min(len(password) / 4, 4.0)
    score += length_score

    if any(char.isdigit() for char in password):
        score += 1.5
    if any(char.isupper() for char in password):
        score += 1.5
    if any(char.islower() for char in password):
        score += 1.0
    if any(char in string.punctuation for char in password):
        score += 2.0
    
    penalty = len(password) - len(set(password))
    score -= min(penalty / 4, 2.0)

    if score < 0:
        score = 0

    if score > 2.5:
        strength = "Strong"
    elif score > 5.0:
        strength = "Good"
    elif score > 2.5:
        strength = "Weak"
    else:
        strength = "Very Weak"

    return strength + "\n" + "Score: " + str(score) + "/10.0"










connect = sqlite3.connect("passwords.db")
cursor = connect.cursor()

cursor.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL
                )
                """)

cursor.execute("""
                CREATE TABLE IF NOT EXISTS passwords (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account_id INTEGER NOT NULL,
                    password BLOB NOT NULL,
                )
                """)
