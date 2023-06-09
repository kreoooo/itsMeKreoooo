import os
import uuid
import re
from flask import Flask, request, send_file
from cryptography.fernet import Fernet

app = Flask(__name__)
UPLOAD_FOLDER = 'uploads'
ENCRYPTED_FOLDER = 'encrypted'
KEY_FILE = 'key.key'


# Generowanie klucza szyfrowania
def generate_key():
    key = Fernet.generate_key()
    with open(KEY_FILE, 'wb') as key_file:
        key_file.write(key)


# Wczytanie klucza szyfrowania
def load_key():
    with open(KEY_FILE, 'rb') as key_file:
        key = key_file.read()
    return key


# Szyfrowanie pliku
def encrypt_file(file_path, key):
    fernet = Fernet(key)
    with open(file_path, 'rb') as file:
        file_data = file.read()
    encrypted_data = fernet.encrypt(file_data)
    return encrypted_data


# Odszyfrowywanie pliku
def decrypt_file(file_path, key):
    fernet = Fernet(key)
    with open(file_path, 'rb') as file:
        encrypted_data = file.read()
    decrypted_data = fernet.decrypt(encrypted_data)
    return decrypted_data


# Wrzucanie pliku i szyfrowanie
@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return 'No file part in the request'

    file = request.files['file']
    if file.filename == '':
        return 'No selected file'

    # Tworzenie folderu, jeśli nie istnieje
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    if not os.path.exists(ENCRYPTED_FOLDER):
        os.makedirs(ENCRYPTED_FOLDER)

    # Generowanie unikalnej nazwy pliku
    result = re.findall(r"\.([^.]*)$", file.filename)
    extension = result[0]
    unique_filename = str(uuid.uuid4()) + '.' + extension

    # Zapisywanie pliku na dysku
    file_path = os.path.join(UPLOAD_FOLDER, unique_filename)
    file.save(file_path)

    # Szyfrowanie pliku
    key = load_key()
    encrypted_data = encrypt_file(file_path, key)

    # Generowanie nazwy zaszyfrowanego pliku
    encrypted_filename = unique_filename + '.enc'

    # Zapisywanie zaszyfrowanego pliku na dysku
    encrypted_file_path = os.path.join(ENCRYPTED_FOLDER, encrypted_filename)
    with open(encrypted_file_path, 'wb') as encrypted_file:
        encrypted_file.write(encrypted_data)

    return 'File uploaded and encrypted successfully'


# Pobieranie i odszyfrowywanie pliku
@app.route('/download/<filename>', methods=['GET'])
def download_file(filename):
    encrypted_file_path = os.path.join(ENCRYPTED_FOLDER, filename)
    if not os.path.exists(encrypted_file_path):
        return 'File not found'

    # Odszyfrowywanie pliku
    key = load_key()
    decrypted_data = decrypt_file(encrypted_file_path, key)

    # Generowanie nazwy pliku do pobrania
    original_filename = filename[:-4]  # Usunięcie rozszerzenia ".enc" z nazwy pliku

    # Zapisywanie odszyfrowanego pliku na dysku
    decrypted_file_path = os.path.join(UPLOAD_FOLDER, original_filename)
    print(decrypted_file_path)
    with open(decrypted_file_path, 'wb') as decrypted_file:
        decrypted_file.write(decrypted_data)

    # Zwracanie pliku do pobrania

    return send_file(decrypted_file_path, as_attachment=True)


if __name__ == '__main__':
    # Sprawdzanie istnienia klucza szyfrowania, jeśli nie to go generuje
    if not os.path.exists(KEY_FILE):
        generate_key()

    app.run(debug=True)

