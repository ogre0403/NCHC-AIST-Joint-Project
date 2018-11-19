import reversible


def main():
    p = (4, 4, 4)
    a = (3, 3, 3)
    b = (1, 1, 1)

    print "origin pixel\t: %s" % (p,)
    q = reversible.F_inverse(b, reversible.F(a, p))
    print "watermark pixel\t: %s" % (q,)
    p_prime = reversible.F_inverse(a, reversible.F(b, q))
    print "recovered pixel\t: %s" % (p_prime,)

    watermarking_img = reversible.addWatermark("./aa.jpg", "./logo.png")
    watermarking_img.show()

    recovered_img = reversible.removeWatermark(watermarking_img, "./logo.png")
    recovered_img.show()


if __name__ == "__main__":
    main()
