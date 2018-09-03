from aes import AESCipher


def main():
    cipher = AESCipher("12345")
    cipher.encrypt("./aa.jpg", "./aa.jpg.enc")
    cipher.decrypt("./aa.jpg.enc", "./bb.jpg")


if __name__ == "__main__":
    main()
