import base64
import hashlib
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
    def __init__(self, container_id, password, website, username):
        self.container_id = container_id
        self.password = password
        self.website = website
        self.username = username

    def __str__(self):
        return f"Website: {self.website}\nUsername: {self.username}\nPassword: {self.password}\n-\n"

    def change_password(self, new_password):
        self.password = new_password
    
    def change_username(self, new_username):
        self.username = new_username
    
    def change_website(self, new_website):
        self.website = new_website

    def change_id(self, new_id):
        self.container_id = new_id

    def get_id(self):
        return self.container_id
    
    def get_password(self):
        return self.password
    
    def get_username(self):
        return self.username
    
    def get_website(self):
        return self.website
    
    def serialize(self):
        return json.dumps(self.__dict__).encode()
    
    @classmethod
    def deserialize(cls, data, container_id):
        container = cls(**json.loads(data.decode()))
        container.change_id(container_id)
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

    return strength + "\n" + "Score: " + str(score) + "/10.0\n-\n"

def generate_key(password, salt):
    if isinstance(password, str):
        password = password.encode()
    else:
        password = str(password).encode()
    
    kdf = PBKDF2HMAC(
        algorithm = hashes.SHA256(),
        length = 32,
        salt = salt,
        iterations = 100000,
    )

    return(kdf.derive(password)) 

def encrypt(container):
    salt = hashlib.sha256(account_username.encode()).digest()
    
    key = base64.urlsafe_b64encode(generate_key(account_password, salt))
    fernet = Fernet(key)

    encrypted = fernet.encrypt(container.serialize())

    print("Encryption Successful\n-\n")
    get_output()

    return encrypted

def decrypt(encrypted_data):
    salt = hashlib.sha256(account_username.encode()).digest()

    key = base64.urlsafe_b64encode(generate_key(account_password, salt))
    fernet = Fernet(key)
      
    decrypted = fernet.decrypt(encrypted_data)

    print("Decryption Successful\n-\n")
    get_output()

    return decrypted

def account_login(text_box, text_box2):
    global account_id
    global account_username
    global account_password
    global account_password_hash
    global passwords

    username = text_box.get().strip()
    password = text_box2.get().strip()

    account_password = password

    if username == "" and password == "":
        print("Enter a valid Username and Password\n-\n")
        get_output()
    elif username == "":
        print("Enter a valid Username\n-\n")
        get_output()
    elif password == "":
        print("Enter a valid Password\n-\n")
        get_output()
    else :
        connect = sqlite3.connect("password_manager.db")
        cursor = connect.cursor()
        
        cursor.execute("SELECT * FROM accounts WHERE username = ?", (username,))
        account_name = cursor.fetchone()
        
        cursor.close()
        connect.close()

        if account_name:
            account_id, account_username, account_password_hash = account_name
            
            if  not check_master_password():
                print("Incorrect Password\n-\n")
                get_output()
            else :
                main_screen()
                print("Login Successful\n-\n")
                get_output()

                passwords = get_passwords()

                for container in passwords:
                    print(str(container))
                    get_output()
        else :
            print("Account does not exist\n-\n")
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
            container_id, encrypted_container = data
            
            decrypted_data = decrypt(encrypted_container)

            container = PasswordContainer.deserialize(decrypted_data, container_id)
            passwords.append(container)
    else :
        print("No passwords contained in account\n-\n")
        get_output()

    return passwords

def check_master_password():
    if hash_master_password(account_password) == account_password_hash:
        return True
    else:
        return False
    
def change_username(container, new_username):
    container.change_username(new_username) 
    container_id = container.get_id()

    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE passwords SET password = ? WHERE id = ?", (encrypted_container, container_id))

    conn.commit()

    cursor.close()
    connect.close()

    print("Username changed successfully\n-\n")
    get_output()

def change_password(container, new_password):
    container.change_password(new_password) 
    container_id = container.get_id()

    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE passwords SET password = ? WHERE id = ?", (encrypted_container, container_id))

    conn.commit()

    cursor.close()
    connect.close()
    
    print("Password changed successfully\n-\n")
    get_output()

def change_website(container, new_website):
    container.change_website(new_website) 
    container_id = container.get_id()

    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE passwords SET password = ? WHERE id = ?", (encrypted_container, container_id))

    conn.commit()

    cursor.close()
    connect.close()

    print("Website changed successfully\n-\n")
    get_output()

def add_password(text_box, text_box2, text_box3):
    new_website = text_box.get().strip()
    new_username = text_box2.get().strip()
    new_password = text_box3.get().strip()
    
    container = PasswordContainer(account_id, new_password, new_website, new_username)
    encrypted_container = encrypt(container)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("INSERT INTO encrypted_passwords (account_id, password) VALUES (?, ?)", (account_id, encrypted_container))

    conn.commit()

    cursor.close()
    connect.close()

    return_to_main()

    print("Password container added successfully\n-\n")
    get_output()

def create_account(text_box, text_box2):
    
    username = text_box.get().strip()
    master_password = text_box2.get().strip()

    if username == "" and master_password == "":
        print("Enter a valid Username and Password\n-\n")
        get_output()
    elif username == "":
        print("Enter a valid Username\n-\n")
        get_output()
    elif master_password == "":
        print("Enter a valid Password\n-\n")
        get_output()
    else :

        hashed_password = hash_master_password(master_password)

        connection = sqlite3.connect("password_manager.db")
        cursor = connection.cursor()
        try:
            cursor.execute("INSERT INTO accounts (username, password_hash) VALUES (?, ?)", (username, hashed_password))
            connection.commit()

            cursor.close()
            connection.close()

            login_screen()

            print("Account created successfully\n-\n")
            get_output()

        except sqlite3.IntegrityError:
            print("Username already exists. Please choose a different username\n-\n")
            get_output()

def hash_master_password(master_password):
    hashed_password = hashlib.sha256(master_password.encode()).hexdigest()
    return hashed_password

def get_output():
    output_text = output.getvalue().strip() + "\n"
    text_widget.insert(tk.END, output_text)
    output.truncate(0)
    output.seek(0)

def clear_frames():
    for widget in root.winfo_children():
        if widget is not text_widget:
            widget.destroy()
    
    text_widget.delete("1.0", tk.END)
    

def login_screen():
    clear_frames()
    
    account_id = None
    account_username = None
    account_password = None
    account_password_hash = None
    passwords = None

    login_frame = tk.Frame(root)
    login_frame.pack()

    label = tk.Label(login_frame, text="Login")
    label.pack(padx=10, pady=10)

    text_box = tk.Entry(login_frame, width=50, borderwidth=5)
    text_box.insert(tk.END, "Enter Username")
    text_box.pack()

    text_box2 = tk.Entry(login_frame, width=50, borderwidth=5)
    text_box2.insert(tk.END, "Enter Password")
    text_box2.pack()

    button = tk.Button(login_frame, text="Login", padx=10, pady=10, fg="white", bg="black", command= lambda: account_login(text_box, text_box2))
    button.pack()

    button2 = tk.Button(login_frame, text="Create Account", padx=10, pady=10, fg="white", bg="black", command= new_account_screen)
    button2.pack()

def new_account_screen():
    clear_frames()

    new_account_frame = tk.Frame(root)
    new_account_frame.pack()

    label = tk.Label(new_account_frame, text="Create Account")
    label.pack(padx=10, pady=10)

    text_box = tk.Entry(new_account_frame, width=50, borderwidth=5)
    text_box.insert(tk.END, "Enter New Username")
    text_box.pack()

    text_box2 = tk.Entry(new_account_frame, width=50, borderwidth=5)
    text_box2.insert(tk.END, "Enter New Password")
    text_box2.pack()

    button = tk.Button(new_account_frame, text="Create Account", padx=10, pady=10, fg="white", bg="black", command= lambda: create_account(text_box, text_box2))
    button.pack()

    button2 = tk.Button(new_account_frame, text="Return to Login", padx=10, pady=10, fg="white", bg="black", command= login_screen)
    button2.pack()
    
def main_screen():
    clear_frames()
    
    main_frame = tk.Frame(root)
    main_frame.pack()

    label = tk.Label(main_frame, text="Main Screen")
    label.pack(padx=10, pady=10)

    button = tk.Button(main_frame, text="Add Password", padx=10, pady=10, fg="white", bg="black", command= add_password_screen)
    button.pack()

    button2 = tk.Button(main_frame, text="Return to Login", padx=10, pady=10, fg="white", bg="black", command= login_screen)
    button2.pack()
    
def add_password_screen():
    clear_frames()

    add_frame = tk.Frame(root)
    add_frame.pack()

    label = tk.Label(add_frame, text="Add Password")
    label.pack(padx=10, pady=10)

    text_box = tk.Entry(add_frame, width=50, borderwidth=5)
    text_box.insert(tk.END, "Enter New Website")
    text_box.pack()

    text_box2 = tk.Entry(add_frame, width=50, borderwidth=5)
    text_box2.insert(tk.END, "Enter Username")
    text_box2.pack()

    text_box3 = tk.Entry(add_frame, width=50, borderwidth=5)
    text_box3.insert(tk.END, "Enter Password")
    text_box3.pack()

    button = tk.Button(add_frame, text="Add Password", padx=10, pady=10, fg="white", bg="black", command= lambda: add_password(text_box, text_box2, text_box3))
    button.pack()

    button2 = tk.Button(add_frame, text="Return to Main Screen", padx=10, pady=10, fg="white", bg="black", command= return_to_main)
    button2.pack()

def return_to_main():
    main_screen()

    passwords = get_passwords()
    for container in passwords:
        print(str(container))
        get_output()

connect = sqlite3.connect("password_manager.db")
cursor = connect.cursor()

cursor.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    password_hash TEXT NOT NULL
                )
                """)

cursor.execute("""
                CREATE TABLE IF NOT EXISTS encrypted_passwords (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account_id INTEGER NOT NULL,
                    password BLOB NOT NULL
                )
                """)

connect.commit()
cursor.close()
connect.close()

output = StringIO()
sys.stdout = output

root = tk.Tk()
root.title("Password Manager")

text_widget = tk.Text(root)
text_widget.pack(side=tk.BOTTOM, fill=tk.BOTH, expand=True)

login_screen()

root.mainloop()