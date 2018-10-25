import watermark
import reversible

import monotonic


def main():
    # for b in range(256):
    #     aRow = []
    #     for q in range(256):
    #         aRow.append(reversible.F_inverse(b, q))
    #     print(str(aRow) + ",")

    # print monotonic.F_inv_Table[3][5]

    # p = (4, 4, 4)
    # a = (3, 3, 3)
    # b = (1, 1, 1)
    #
    # q = reversible.F_inverse_rgb(b, reversible.F_rgb(a, p))
    # print q
    # pp = reversible.F_inverse_rgb(a, reversible.F_rgb(b, q))
    # print pp

    # print F(224, 227)
    # print F_inverse(30, F(224, 227))
    # print F_inverse(224, F(30, 33))
    # watermark_pixel("./logo.png")
    # watermark_pixel("./water-2.jpg")
    # watermark.watermark_with_transparency("./aa.jpg", "./ww.jpg", './watermark.png', position=(0, 0))  # functio
    # watermark.watermark_with_transparency("./watermark.png", "./ww.jpg", './logo.png', position=(0, 0))  # functio
    A, B = reversible.addWatermark("./aa.jpg", "./logo.png")

    print A[423][477]


if __name__ == "__main__":
    main()
