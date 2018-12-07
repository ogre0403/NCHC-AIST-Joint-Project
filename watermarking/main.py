import reversible
import time


def main():
    p = (4, 4, 4)
    a = (3, 3, 3)
    b = (1, 1, 1)

    print("origin pixel\t: %s" % (p,))
    q = reversible.F_inverse(b, reversible.F(a, p))
    print("watermark pixel\t: %s" % (q,))
    p_prime = reversible.F_inverse(a, reversible.F(b, q))
    print("recovered pixel\t: %s" % (p_prime,))

    start1 = time.time()
    watermarking_img = reversible.addWatermark("./light.jpg", "./logo.png", blk_height=200, blk_width=300)
    end1 = time.time()
    print(end1 - start1)

    watermarking_img.show()

    start2 = time.time()
    recovered_img = reversible.removeWatermark(watermarking_img, "./logo.png", blk_height=200, blk_width=300)
    end2 = time.time()
    print(end2 - start2)

    recovered_img.show()

    # todo: generate watermark base64 type key


if __name__ == "__main__":
    main()
