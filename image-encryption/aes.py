import os
import random
from Crypto.Cipher import AES
from Crypto.Hash import SHA256


class AESCipher(object):
    def __init__(self, key):
        self.key = key

    def decrypt(self, filename, output_file):
        chunk_size = 64 * 1024
        with open(filename, 'rb') as inf:
            filesize = long(inf.read(16))
            IV = inf.read(16)
            decryptor = AES.new(self._key(), AES.MODE_CBC, IV)
            with open(output_file, 'wb') as outf:
                while True:
                    chunk = inf.read(chunk_size)
                    if len(chunk) == 0:
                        break
                    outf.write(decryptor.decrypt(chunk))
                outf.truncate(filesize)

    def encrypt(self, filename, output_file):
        chunk_size = 64 * 1024
        file_size = str(os.path.getsize(filename)).zfill(16)
        IV = ''
        for i in range(16):
            IV += chr(random.randint(0, 0xFF))
        encryptor = AES.new(self._key(), AES.MODE_CBC, IV)
        with open(filename, 'rb') as inputfile:
            with open(output_file, 'wb') as outf:
                outf.write(file_size)
                outf.write(IV)
                while True:
                    chunk = inputfile.read(chunk_size)
                    if len(chunk) == 0:
                        break
                    elif len(chunk) % 16 != 0:
                        chunk += ' ' * (16 - len(chunk) % 16)
                    outf.write(encryptor.encrypt(chunk))

    def _key(self):
        hasher = SHA256.new(self.key)
        return hasher.digest()
