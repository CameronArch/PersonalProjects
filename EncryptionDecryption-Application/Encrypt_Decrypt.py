import base64
from io import StringIO
import os
import sys
import tkinter as tk
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.fernet import Fernet, InvalidToken

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

def encrypt(file_path, password):
    salt = os.urandom(16)
    key = base64.urlsafe_b64encode(generate_key(password, salt))
    fernet = Fernet(key)

    with open(file_path, "rb") as file:
        file_data = file.read()

    encrypted = fernet.encrypt(file_data)

    with open(file_path, "wb") as file:
        file.write(salt + encrypted)

    print("Encryption Successful")
    get_output()

def decrypt(file_path, password):
    with open(file_path, "rb") as file:
        file_data = file.read()
        salt = file_data[:16]
        encrypted_data = file_data[16:]
    try:
        key = base64.urlsafe_b64encode(generate_key(password, salt))
        fernet = Fernet(key)
        
        decrypted = fernet.decrypt(encrypted_data)

        with open(file_path, "wb") as file:
            file.write(decrypted)

        print("Decryption Successful")
        get_output()

    except InvalidToken:
        print("Incorrect Password")
        get_output()

def encrypt_path(directory_path,password):
    for root, dirs, files in os.walk(directory_path):
        for file in files:
            encrypt(os.path.join(root, file), password)

def decrypt_path(directory_path,password):
    for root, dirs, files in os.walk(directory_path):
        for file in files:
            decrypt(os.path.join(root, file), password)

def is_encrypt():
    global cipher
    cipher = True
    print("Encryption Selected")
    get_output()

def is_decrypt():
    global cipher
    cipher = False
    print("Decryption Selected")
    get_output()

def start():
    if cipher != None:
        if cipher == True:
            start_encrypt()
        else:
            start_decrypt()
    else:
        print("Select Encrypt or Decrypt")
        get_output()

def start_encrypt():
    if proceed == True and input_password != None:
        if os.path.isfile(input_path):
            encrypt(input_path, input_password)
        else:
            encrypt_path(input_path, input_password)
        reset()
    elif proceed == False and input_password == None:
        print("Enter a Valid Path and Password")
        get_output()
    elif proceed == False:
        print("Enter a Valid Path")
        get_output()
    else:
        print("Enter Password")
        get_output()

def start_decrypt():
    if proceed == True and input_password != None:
        if os.path.isfile(input_path):
            decrypt(input_path, input_password)
        else:
            decrypt_path(input_path, input_password)
        reset()            
    elif proceed == False and input_password == None:
        print("Enter a Valid Path and Password")
        get_output()
    elif proceed == False:
        print("Enter a Valid Path")
        get_output()
    else:
        print("Enter Password")
        get_output()

def get_path():
    global proceed 
    global input_path
    input_path = text_box.get()

    if os.path.exists(input_path):
        print("Path Exists")
        get_output()
        proceed = True
    else:
        print("Path Does Not Exist")
        get_output()

def get_password():
    global input_password
    input_password = text_box2.get()
    print("Password Entered")
    get_output()

def get_output():
    output_text = output.getvalue().strip() + "\n"
    text_widget.insert(tk.END, output_text)
    output.truncate(0)
    output.seek(0)

def reset():
    global proceed
    global cipher
    global input_path
    global input_password
    proceed = False
    cipher = None
    input_path = None
    input_password = None

window = tk.Tk()
proceed = False
cipher = None
input_password = None

output = StringIO()
sys.stdout = output

label = tk.Label(text="Encryption and Decryption Program", font=("Arial", 20))
label.pack()

text_box = tk.Entry(window, width=50, borderwidth=5)
text_box.insert(tk.END, "Enter File Path")
text_box.pack()

button = tk.Button(window, text="Enter", padx=10, pady=5, fg="white", bg="black", command= get_path)
button.pack()

button2 = tk.Button(window, text="Encrypt", padx=10, pady=5, fg="white", bg="black", command= is_encrypt)
button2.pack()

button3 = tk.Button(window, text="Decrypt", padx=10, pady=5, fg="white", bg="black", command= is_decrypt)
button3.pack()

text_box2 = tk.Entry(window, width=50, borderwidth=5)
text_box2.insert(tk.END, "Enter Password")
text_box2.pack()

button4 = tk.Button(window, text="Enter", padx=10, pady=5, fg="white", bg="black", command= get_password)
button4.pack()

button5 = tk.Button(window, text="Start Program", padx=10, pady=5, fg="white", bg="black", command= start)
button5.pack()

text_widget = tk.Text(window)
text_widget.pack()

window.mainloop()