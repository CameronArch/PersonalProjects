import base64
from io import StringIO
import os
import sys
import tkinter as tk
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.fernet import Fernet, InvalidToken
import json
import random
import string
import sqlite3   

class PasswordContainer:
    def __init__(self, id, password, website, username):
        self.id = id
        self.password = password
        self.website = website
        self.username = username

    def __str__(self):
        return f"Website: {self.website}\nUsername: {self.username}\nPassword: {self.password}\n"

    def change_password(self, new_password):
        self.password = new_password
    
    def change_username(self, new_username):
        self.username = new_username
    
    def change_website(self, new_website):
        self.website = new_website

    def change_id(self, new_id):
        self.id = new_id

    def get_id(self):
        return self.id
    
    def get_password(self):
        return self.password
    
    def get_username(self):
        return self.username
    
    def get_website(self):
        return self.website
    
    def serialize(self):
        return json.dumps(self.__dict__)
    
    def deserialize(cls, data, id):
        container = cls(**json.loads(data))
        container.change_id(id)
        return container
    
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

    if score > 7.5:
        strength = "Strong"
    elif score > 5.0:
        strength = "Good"
    elif score > 2.5:
        strength = "Weak"
    else:
        strength = "Very Weak"

    return strength + "\n" + "Score: " + str(score) + "/10.0\n"

def generate_key(salt):
    if isinstance(account_password, str):
        password = account_password.encode()
    else:
        password = str(account_password).encode()
    
    kdf = PBKDF2HMAC(
        algorithm = hashes.SHA256(),
        length = 32,
        salt = salt,
        iterations = 100000,
    )

    return(kdf.derive(password)) 

def encrypt(container):
    salt = os.urandom(16)
    key = base64.urlsafe_b64encode(generate_key(salt))
    fernet = Fernet(key)

    encrypted = fernet.encrypt(container.serialize())

    print("Encryption Successful\n")
    get_output()

    return encrypted

def decrypt(container):
    salt = container[:16]
    encrypted_data = container[16:]
    try:
        key = base64.urlsafe_b64encode(generate_key(salt))
        fernet = Fernet(key)
        
        decrypted = fernet.decrypt(encrypted_data)

        print("Decryption Successful\n")
        get_output()

        return decrypted

    except InvalidToken:
        return None

def account_login(username,password):
    global account_id
    global account_username
    global account_password
    global passwords
    account_password = password

    if username == "" and password == "":
        print("Enter a valid Username and Password\n")
        get_output()
    elif username == "":
        print("Enter a valid Username\n")
        get_output()
    elif password == "":
        print("Enter a valid Password\n")
        get_output()
    else :
        connect = sqlite3.connect("password_manager.db")
        cursor = connect.cursor()
        cursor.execute("SELECT * FROM accounts WHERE username = ?", (username,))
        account_name = cursor.fetchone()
        cursor.close()
        connect.close()

        if account_name:
            account_id, account_username = account_name
            
            passwords = get_passwords()
            if passwords is None:
                print("Incorrect Password\n")
                get_output()
            else :
                print("Login Successful\n")
                get_output()

                for container in passwords:
                    print(str(container))
                    get_output()
        else :
            print("Account does not exist\n")
            get_output()

def get_passwords():
    connect = sqlite3.connect("password_manager.db")
    cursor = connect.cursor()

    cursor.execute("SELECT id, password FROM encrypted_passwords WHERE account_id = ?", (account_id,))
    passwords_data = cursor.fetchall()

    cursor.close()
    connect.close()

    passwords = []
    if passwords_data:
        for data in passwords_data:
            id, encrypted_container = data
            decrypted_data = decrypt(encrypted_container)
            
            if decrypted_data is None:
                return None

            container = PasswordContainer.deserialize(decrypted_data, id)
            passwords.append(container)
    else :
        print("No passwords contained in account\n")
        get_output()

    return passwords
    
def change_username(container, new_username):
    container.change_username(new_username) 
    id = container.get_id()

    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE passwords SET password = ? WHERE id = ?", (encrypted_container, id))

    conn.commit()

    cursor.close()
    connect.close()

def change_password(container, new_password):
    container.change_password(new_password) 
    id = container.get_id()

    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE passwords SET password = ? WHERE id = ?", (encrypted_container, id))

    conn.commit()

    cursor.close()
    connect.close()

def change_website(container, new_website):
    container.change_website(new_website) 
    id = container.get_id()

    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE passwords SET password = ? WHERE id = ?", (encrypted_container, id))

    conn.commit()

    cursor.close()
    connect.close()

def add_password():
    

def create_account():
    

def get_output():
    output_text = output.getvalue().strip() + "\n"
    text_widget.insert(tk.END, output_text)
    output.truncate(0)
    output.seek(0)

connect = sqlite3.connect("password_manager.db")
cursor = connect.cursor()

cursor.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL
                )
                """)

cursor.execute("""
                CREATE TABLE IF NOT EXISTS encrypted_passwords (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account_id INTEGER NOT NULL,
                    password BLOB NOT NULL,
                )
                """)

connect.commit()
cursor.close()
connect.close()

output = StringIO()
sys.stdout = output

window = tk.Tk()

label = tk.Label(text="Password Manager", font=("Arial", 20))
label.pack()

text_box = tk.Entry(window, width=50, borderwidth=5)
text_box.insert(tk.END, "Enter Username")
text_box.pack()

text_box2 = tk.Entry(window, width=50, borderwidth=5)
text_box2.insert(tk.END, "Enter Password")
text_box2.pack()

button = tk.Button(window, text="Enter", padx=10, pady=5, fg="white", bg="black", command= get_path)
button.pack()

button2 = tk.Button(window, text="Encrypt", padx=10, pady=5, fg="white", bg="black", command= is_encrypt)
button2.pack()

button3 = tk.Button(window, text="Decrypt", padx=10, pady=5, fg="white", bg="black", command= is_decrypt)
button3.pack()

button5 = tk.Button(window, text="Start Program", padx=10, pady=5, fg="white", bg="black", command= start)
button5.pack()

text_widget = tk.Text(window)
text_widget.pack()

window.mainloop()
