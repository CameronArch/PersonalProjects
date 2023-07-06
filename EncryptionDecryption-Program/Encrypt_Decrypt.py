import base64
import os
import sys
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.fernet import Fernet

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

    encrpted = fernet.encrypt(file_data)

    with open(file_path, "wb") as file:
        file.write(salt + encrpted)

def decrypt(file_path, password):
    with open(file_path, "rb") as file:
        file_data = file.read()
        salt = file_data[:16]
        encrypted_data = file_data[16:]
    
    key = base64.urlsafe_b64encode(generate_key(password, salt))
    fernet = Fernet(key)

    decrypted = fernet.decrypt(encrypted_data)

    with open(file_path, "wb") as file:
        file.write(decrypted)

input_file = input("Enter file path:")

if not os.path.exists(input_file):
    print("File not found")
    sys.exit()

input_choice = input("Enter choice (encrypt/decrypt):")

while input_choice not in ["encrypt", "decrypt"]:
    print("Invalid choice")
    input_choice = input("Enter choice (encrypt/decrypt):")

input_password = input("Enter password:")

if input_choice == "encrypt":
    encrypt(input_file, input_password)

else:
    decrypt(input_file, input_password)
