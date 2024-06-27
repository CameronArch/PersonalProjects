import base64
import hashlib
from io import StringIO
import sys
import tkinter as tk
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.fernet import Fernet
import json
import random
import string
import sqlite3

'''
Container class to store data associated with a given password.

Member Variables:
container_id - The unique identifier for the container.
password - The password associated and stored in the container.
website - The website associated with the password.
username - The username associated with the password.
'''
class PasswordContainer:
    '''
    Class constuctor to initialize the container object.

    @param container_id The unique identifier for the container.
    @param password The password associated and stored in the container.
    @param website The website associated with the password.
    @param username The username associated with the password.
    '''
    def __init__(self, container_id, password, website, username):
        self.container_id = container_id
        self.password = password
        self.website = website
        self.username = username

    '''
    Creates string representation of the container object.

    @return The string representation of the container object.
    '''
    def __str__(self):
        return f"Container ID: {self.container_id}\nWebsite: {self.website}\nUsername: {self.username}\nPassword: {self.password}\n-\n"

    '''
    Changes the password stored in the container.

    @param new_password The new password to store in the container.
    '''
    def change_password(self, new_password):
        self.password = new_password
    
    '''
    Changes the username stored in the container.

    @param new_username The new username to store in the container.
    '''
    def change_username(self, new_username):
        self.username = new_username
    
    '''
    Changes the website stored in the container.

    @param new_website The new website to store in the container.
    '''
    def change_website(self, new_website):
        self.website = new_website

    '''
    Changes the container ID.

    @param new_id The new container ID.
    '''
    def change_id(self, new_id):
        self.container_id = new_id

    '''
    Gets the container ID.

    @return The container ID.
    '''
    def get_id(self):
        return self.container_id
    
    '''
    Gets the password stored in the container.

    @return The password stored in the container.
    '''
    def get_password(self):
        return self.password
    
    '''
    Gets the username stored in the container.
    
    @return The username stored in the container.
    '''
    def get_username(self):
        return self.username
    '''
    Gets the website stored in the container.
    
    @return The website stored in the container.
    '''
    def get_website(self):
        return self.website
    '''
    Serializes the container object by converting it to a JSON string
    and then encoding it into bytes.
    
    @return The serialized container object.
    '''
    def serialize(self):
        return json.dumps(self.__dict__).encode()
    
    '''
    Deserializes the container object by converting bytes back 
    to a JSON string and then reconstructing the container object
    with a new id.

    @param data The serialized container object.
    @param container_id The new container ID.
    
    @return The deserialized container object.
    '''
    @classmethod
    def deserialize(cls, data, container_id):
        container = cls(**json.loads(data.decode()))
        container.change_id(container_id)
        return container
    
    '''
    Generates a random password of a given length.
    
    @param length The length of the password to generate.
    
    @return The generated password.
    '''
def generate_password(length = 12):
    characters = string.ascii_letters + string.digits + string.punctuation

    password = []
    password.append(random.choice(string.ascii_lowercase))
    password.append(random.choice(string.ascii_uppercase))
    password.append(random.choice(string.digits))
    password.append(random.choice(string.punctuation))

    for i in range(length - 4):
        password.append(random.choice(characters))
    
    random.shuffle(password)

    password = "".join(password)

    print("Generated Password: " + password + "\n-\n")
    get_output()
'''
Rates the strength of a given password based on a scoring system
based on unique characters, length, and types of characters.

@param text_box The text box containing the password to rate.

@return The strength of the password.
'''
def rate_password(text_box):
    password = text_box.get().strip()
    
    score = 0

    length_score = min(len(password) / 3, 4.0)
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

    print(strength + "\n" + "Score: " + str(score) + "/10.0\n-\n")
    get_output()

'''
Generates a key based on a given password and salt for encryption.

@param password The password to generate the key from.
@param salt The salt to use in generating the key.

@return The generated key.
'''
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

'''
Encrypts a given container object using Fernet symmetric key encryption.

@param container The container object to encrypt.

@return The encrypted container object.
'''
def encrypt(container):
    salt = hashlib.sha256(account_username.encode()).digest()
    
    key = base64.urlsafe_b64encode(generate_key(account_password, salt))
    fernet = Fernet(key)

    encrypted = fernet.encrypt(container.serialize())

    return encrypted

'''
Decrypts a given encrypted container object using Fernet symmetric key encryption.

@param encrypted_data The encrypted container object to decrypt.

@return The decrypted container object.
'''
def decrypt(encrypted_data):
    salt = hashlib.sha256(account_username.encode()).digest()

    key = base64.urlsafe_b64encode(generate_key(account_password, salt))
    fernet = Fernet(key)
      
    decrypted = fernet.decrypt(encrypted_data)

    return decrypted

'''
Logs into an account using a given username and password.

@param text_box The text box containing the username.
@param text_box2 The text box containing the password.
'''
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
        print("Enter a Valid Username and Password\n-\n")
        get_output()
    elif username == "":
        print("Enter a Valid Username\n-\n")
        get_output()
    elif password == "":
        print("Enter a Valid Password\n-\n")
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
            print("Account Does Not Exist\n-\n")
            get_output()

'''
Gets the password containers associated with an account.

@return The list of containers associated with the account.'''
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
        print("No Passwords Contained in Account\n-\n")
        get_output()

    return passwords

'''
Checks the inputted account password with the actual account password.

@return True if the passwords match, False otherwise.'''
def check_master_password():
    if hash_master_password(account_password) == account_password_hash:
        return True
    else:
        return False

'''
Changes the username of a given container for an account.

@param text_box2 The text box containing the container ID.
@param text_box3 The text box containing the new username.
'''    
def change_username(text_box2, text_box3):
    container_id = text_box2.get().strip()
    new_username = text_box3.get().strip()
    
    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("SELECT password FROM encrypted_passwords WHERE id = ? AND account_id = ?", (container_id, account_id))

    password_data = cursor.fetchone()

    cursor.close()
    connect.close()

    if password_data:
        encrypted_container = password_data[0]
        decrypted_data = decrypt(encrypted_container)
        
        container = PasswordContainer.deserialize(decrypted_data, container_id)
        container.change_username(new_username)

        encrypted_container = encrypt(container)

        conn = sqlite3.connect("password_manager.db")
        cursor = conn.cursor()

        cursor.execute("UPDATE encrypted_passwords SET password = ? WHERE id = ? AND account_id = ?", (encrypted_container, container_id, account_id))

        conn.commit()

        cursor.close()
        connect.close()
        
        text_widget.delete("1.0", tk.END)

        passwords = get_passwords()
        for container in passwords:
            print(str(container))
            get_output()

        print("Username Changed Successfully\n-\n")
        get_output()

    else :
        print("Container ID Does Not Exist\n-\n")
        get_output()

'''
Changes the password of a given container for an account.

@param text_box2 The text box containing the container ID.
@param text_box3 The text box containing the new password.
'''
def change_password(text_box2, text_box3):
    container_id = text_box2.get().strip()
    new_password = text_box3.get().strip()
    
    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("SELECT password FROM encrypted_passwords WHERE id = ? AND account_id = ?", (container_id, account_id))

    password_data = cursor.fetchone()

    cursor.close()
    connect.close()

    if password_data:
        encrypted_container = password_data[0]
        decrypted_data = decrypt(encrypted_container)
        
        container = PasswordContainer.deserialize(decrypted_data, container_id)
        container.change_password(new_password)

        encrypted_container = encrypt(container)

        conn = sqlite3.connect("password_manager.db")
        cursor = conn.cursor()

        cursor.execute("UPDATE encrypted_passwords SET password = ? WHERE id = ? AND account_id = ?", (encrypted_container, container_id, account_id))

        conn.commit()

        cursor.close()
        connect.close()
        
        text_widget.delete("1.0", tk.END)

        passwords = get_passwords()
        for container in passwords:
            print(str(container))
            get_output()

        print("Password Changed Successfully\n-\n")
        get_output()

    else :
        print("Container ID Does Not Exist\n-\n")
        get_output()

'''
Changes the website of a given container for an account.

@param text_box2 The text box containing the container ID.
@param text_box3 The text box containing the new website.
'''
def change_website(text_box2, text_box3):
    container_id = text_box2.get().strip()
    new_website = text_box3.get().strip()
    
    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("SELECT password FROM encrypted_passwords WHERE id = ? AND account_id = ?", (container_id, account_id))

    password_data = cursor.fetchone()

    cursor.close()
    connect.close()

    if password_data:
        encrypted_container = password_data[0]
        decrypted_data = decrypt(encrypted_container)
        
        container = PasswordContainer.deserialize(decrypted_data, container_id)
        container.change_website(new_website)

        encrypted_container = encrypt(container)

        conn = sqlite3.connect("password_manager.db")
        cursor = conn.cursor()

        cursor.execute("UPDATE encrypted_passwords SET password = ? WHERE id = ? AND account_id = ?", (encrypted_container, container_id, account_id))

        conn.commit()

        cursor.close()
        connect.close()
        
        text_widget.delete("1.0", tk.END)

        passwords = get_passwords()
        for container in passwords:
            print(str(container))
            get_output()

        print("Website Changed Successfully\n-\n")
        get_output()

    else :
        print("Container ID Does Not Exist\n-\n")
        get_output()

'''
Removes a password container from an account.

@param text_box2 The text box containing the container ID.
'''
def remove_container(text_box2):
    container_id = text_box2.get().strip()
    
    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM encrypted_passwords WHERE id = ? AND account_id = ?", (container_id, account_id))
    container = cursor.fetchone()

    if container:
        cursor.execute("DELETE FROM encrypted_passwords WHERE id = ? AND account_id = ?", (container_id, account_id))

        conn.commit()

        cursor.close()
        connect.close()

        text_widget.delete("1.0", tk.END)

        passwords = get_passwords()
    
        for container in passwords:
            print(str(container))
            get_output()

        print("Container Removed Successfully\n-\n")
        get_output()

    else :
        print("Container ID Does Not Exist\n-\n")
        get_output()

'''
Adds a password container to an account.

@param text_box The text box containing the website.
@param text_box2 The text box containing the username.
@param text_box3 The text box containing the password.
'''
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

'''
Creates an account with a given username and password.

@param text_box The text box containing the username.
@param text_box2 The text box containing the password.
'''
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
        if not account_exists(username):
            cursor.execute("INSERT INTO accounts (username, password_hash) VALUES (?, ?)", (username, hashed_password))
            connection.commit()

            cursor.close()
            connection.close()

            login_screen()

            print("Account created successfully\n-\n")
            get_output()

        else :
            print("Username already exists. Please choose a different username\n-\n")
            get_output()

'''
Checks if an account exists with a given username.

@param username The username to check.

@return True if the account exists, False otherwise.
'''
def account_exists(username):
    connect = sqlite3.connect("password_manager.db")
    cursor = connect.cursor()

    cursor.execute("SELECT * FROM accounts WHERE username = ?", (username,))
    account_name = cursor.fetchone()

    cursor.close()
    connect.close()

    return bool(account_name)

'''
Hashes a given password using SHA-256.

@param master_password The password to hash.

@return The hashed password.
'''    
def hash_master_password(master_password):
    hashed_password = hashlib.sha256(master_password.encode()).hexdigest()
    return hashed_password

def change_account_username(text_box2):
    global account_username
    
    new_account_username = text_box2.get().strip()

    if account_exists(new_account_username):
        print("Username already exists. Please choose a different username\n-\n")
        get_output()

    else :
        conn = sqlite3.connect("password_manager.db")
        cursor = conn.cursor()

        cursor.execute("UPDATE accounts SET username = ? WHERE id = ?", (new_account_username, account_id))

        conn.commit()
    
        passwords = get_passwords()
        account_username = new_account_username

        for container in passwords:
            container_id = container.get_id()
            encrypted_container = encrypt(container)

            cursor.execute("UPDATE encrypted_passwords SET password = ? WHERE id = ?", (encrypted_container, container_id))

            conn.commit()

        cursor.close()
        connect.close()

        return_to_main()

        print("Account Username Changed Successfully\n-\n")
        get_output()

'''
Changes the password of an account.

@param text_box3 The text box containing the new password.
'''
def change_account_password(text_box3):
    global account_password
    global account_password_hash
    
    new_account_password = text_box3.get().strip()

    account_password_hash = hash_master_password(new_account_password)

    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("UPDATE accounts SET password_hash = ? WHERE id = ?", (account_password_hash, account_id))

    conn.commit()

    passwords = get_passwords()
    account_password = new_account_password

    for container in passwords:
        container_id = container.get_id()
        encrypted_container = encrypt(container)

        cursor.execute("UPDATE encrypted_passwords SET password = ? WHERE id = ?", (encrypted_container, container_id))

        conn.commit()

    cursor.close()
    connect.close()

    return_to_main()

    print("Account Password Changed Successfully\n-\n")
    get_output()

'''
Deletes an account.
'''
def delete_account():
    conn = sqlite3.connect("password_manager.db")
    cursor = conn.cursor()

    cursor.execute("DELETE FROM accounts WHERE id = ?", (account_id,))
    cursor.execute("DELETE FROM encrypted_passwords WHERE account_id = ?", (account_id,))

    conn.commit()

    cursor.close()
    conn.close()

    login_screen()

    print("Account Deleted Successfully\n-\n")
    get_output()

'''
Flushes output buffer stream to text widget.
'''
def get_output():
    output_text = output.getvalue().strip() + "\n"
    text_widget.insert(tk.END, output_text)
    output.truncate(0)
    output.seek(0)

'''
Clears all frames in the root window.
'''
def clear_frames():
    for widget in root.winfo_children():
        if widget is not text_widget:
            widget.destroy()

'''
Initializes the login screen.
'''
def login_screen():
    global account_id
    global account_username
    global account_password
    global account_password_hash
    global passwords

    clear_frames()
    text_widget.delete("1.0", tk.END)

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
    button.pack(side=tk.TOP, padx=5, pady=5)

    button2 = tk.Button(login_frame, text="Create Account", padx=10, pady=10, fg="white", bg="black", command= new_account_screen)
    button2.pack(side=tk.TOP, padx=5, pady=5)

'''
Initializes the new account screen for creating an account.
'''
def new_account_screen():
    clear_frames()
    text_widget.delete("1.0", tk.END)

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
    button.pack(side=tk.TOP, padx=5, pady=5)

    button2 = tk.Button(new_account_frame, text="Return to Login", padx=10, pady=10, fg="white", bg="black", command= login_screen)
    button2.pack(side=tk.TOP, padx=5, pady=5)

'''
Initializes the main screen where passwords will be displayed for account.
'''    
def main_screen():
    clear_frames()
    text_widget.delete("1.0", tk.END)
    
    main_frame = tk.Frame(root)
    main_frame.pack()

    label = tk.Label(main_frame, text="Main Screen")
    label.pack(padx=10, pady=10)

    text_box = tk.Entry(main_frame, width=50, borderwidth=5)
    text_box.insert(tk.END, "Enter Password to check strength")
    text_box.pack()

    button3 = tk.Button(main_frame, text="Check Strength", padx=10, pady=10, fg="white", bg="black", command= lambda: rate_password(text_box))
    button3.pack(side=tk.TOP, padx=5, pady=5)

    button4 = tk.Button(main_frame, text="Generate Strong Password", padx=10, pady=10, fg="white", bg="black", command= generate_password)
    button4.pack(side=tk.TOP, padx=5, pady=5)

    button = tk.Button(main_frame, text="Add Password", padx=10, pady=10, fg="white", bg="black", command= add_password_screen)
    button.pack(side=tk.TOP, padx=5, pady=5)

    text_box2 = tk.Entry(main_frame, width=50, borderwidth=5)
    text_box2.insert(tk.END, "Enter ID of Container Receiving Changes")
    text_box2.pack()
    
    text_box3 = tk.Entry(main_frame, width=50, borderwidth=5)
    text_box3.insert(tk.END, "Enter New Website, Username, or Password")
    text_box3.pack()

    button5 = tk.Button(main_frame, text="Change Website", padx=10, pady=10, fg="white", bg="black", command= lambda: change_website(text_box2, text_box3))
    button5.pack(side=tk.LEFT, padx=5, pady=5)

    button6 = tk.Button(main_frame, text="Change Username", padx=10, pady=10, fg="white", bg="black", command= lambda: change_username(text_box2, text_box3))
    button6.pack(side=tk.LEFT, padx=5, pady=5)

    button7 = tk.Button(main_frame, text="Change Password", padx=10, pady=10, fg="white", bg="black", command= lambda: change_password(text_box2, text_box3))
    button7.pack(side=tk.LEFT, padx=5, pady=5)

    button8 = tk.Button(main_frame, text="Remove Container", padx=10, pady=10, fg="white", bg="black", command= lambda: remove_container(text_box2))
    button8.pack(side=tk.LEFT, padx=5, pady=5)

    center_frame = tk.Frame(root)
    center_frame.pack()

    button9 = tk.Button(center_frame, text="Edit Account", padx=10, pady=10, fg="white", bg="black", command= edit_screen)
    button9.pack(side=tk.LEFT, padx=5, pady=5)

    button2 = tk.Button(center_frame, text="Return to Login", padx=10, pady=10, fg="white", bg="black", command= login_screen)
    button2.pack(side=tk.LEFT, padx=5, pady=5)

'''
Initializes the edit screen for changing account's data.
'''
def edit_screen():
    clear_frames()
    text_widget.delete("1.0", tk.END)

    edit_frame = tk.Frame(root)
    edit_frame.pack()

    label = tk.Label(edit_frame, text="Edit Account")
    label.pack(padx=10, pady=10)

    text_box2 = tk.Entry(edit_frame, width=50, borderwidth=5)
    text_box2.insert(tk.END, "Enter New Account Username")
    text_box2.pack()
    
    button = tk.Button(edit_frame, text="Change Account Username", padx=10, pady=10, fg="white", bg="black", command= lambda: change_account_username(text_box2))
    button.pack(side=tk.TOP, padx=5, pady=5)

    text_box3 = tk.Entry(edit_frame, width=50, borderwidth=5)
    text_box3.insert(tk.END, "Enter New Account Password")
    text_box3.pack()

    button2 = tk.Button(edit_frame, text="Change Account Password", padx=10, pady=10, fg="white", bg="black", command= lambda: change_account_password(text_box3))
    button2.pack(side=tk.TOP, padx=5, pady=5)

    button4 = tk.Button(edit_frame, text="Delete Account", padx=10, pady=10, fg="white", bg="black", command= delete_account)
    button4.pack(side=tk.TOP, padx=5, pady=5)

    button3 = tk.Button(edit_frame, text="Return to Main Screen", padx=10, pady=10, fg="white", bg="black", command= return_to_main)
    button3.pack(side=tk.TOP, padx=5, pady=5)

'''
Initializes the add password screen for adding a password container.
'''
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
    button.pack(side=tk.TOP, padx=5, pady=5)

    button2 = tk.Button(add_frame, text="Return to Main Screen", padx=10, pady=10, fg="white", bg="black", command= return_to_main)
    button2.pack(side=tk.TOP, padx=5, pady=5)

'''
Returns to the main screen from the current screen.
'''
def return_to_main():
    main_screen()

    passwords = get_passwords()
    for container in passwords:
        print(str(container))
        get_output()


'''
Initializes the database for the password manager.
Redirects standard output to StringIO object.
Creates the root window for the application and calls the login screen.
'''

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