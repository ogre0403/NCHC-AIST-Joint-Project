import base64
from Crypto.Cipher import AES


class AESCipher(object):
    def __init__(self, key, block_size=16):
        self.bs = block_size
        if len(key) >= len(str(block_size)):
            self.key = key[:block_size]
        else:
            self.key = self._pad(key)


    def encrypt(self, raw):
        raw = self._pad(raw)
        cipher = AES.new(self.key)
        return base64.b64encode(cipher.encrypt(raw))


    def decrypt(self, enc):
        enc = base64.b64decode(enc)
        cipher = AES.new(self.key)
        return self._unpad(cipher.decrypt(enc))


    def _pad(self, s):
        return s + (self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)

    def _unpad(self, s):
        return s[:-ord(s[len(s)-1:])]

