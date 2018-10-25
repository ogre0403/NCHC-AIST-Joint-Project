from PIL import Image

import numpy as np

import monotonic


def watermark_pixel(watermark_image_path):
    watermark = Image.open(watermark_image_path).resize((980, 340))
    watermark.show()
    rgb_im = watermark.convert('RGB')
    width, height = watermark.size  # getting size of image

    for h in range(height):
        for w in range(width):
            r, g, b = rgb_im.getpixel((w, h))
            print(r, g, b)


def F_rgb(x, p):
    return (monotonic.FTable[x[0]][p[0]],
            monotonic.FTable[x[1]][p[1]],
            monotonic.FTable[x[2]][p[2]])


#
#
# def F(x, p):
#     q = 0
#     data = range(256)
#
#     r = find_min(x, data)
#
#     while r != p:
#         data.remove(r)
#         r = find_min(x, data)
#         q = q + 1
#
#     return q
#
#
def F_inverse_rgb(x, q):
    return (monotonic.F_inv_Table[x[0]][q[0]],
            monotonic.F_inv_Table[x[1]][q[1]],
            monotonic.F_inv_Table[x[2]][q[2]])


#
#
# def F_inverse(x, q):
#     data = range(256)
#     p = find_min(x, data)
#
#     while q > 0:
#         data.remove(p)
#         q = q - 1
#         p = find_min(x, data)
#
#     return p


def find_min(x, data):
    MIN = 256
    r = 0
    for i in data:
        if abs(x - i) < MIN:
            MIN = abs(x - i)
            r = i
    return r


def addWatermark(base, watermark):
    base_image = Image.open(base)
    base_rgb = base_image.convert('RGB')
    base_pixel = base_image.load()
    width, height = base_image.size

    watermark = Image.open(watermark).resize(base_image.size)
    watermark_rgb = watermark.convert('RGB')

    result = Image.new('RGB', base_image.size)
    pixels = result.load()  # create the pixel map

    for w in range(width):
        pixels[w, 0] = base_pixel[w, 0]

    for h in range(height):
        pixels[0, h] = base_pixel[0, h]

    A = np.zeros([height, width], dtype=(int, 3))
    B = np.zeros([height, width], dtype=(int, 3))

    for h in range(1, height):
        for w in range(1, width):
            if watermark_rgb.getpixel((w, h)) != (255, 255, 255):  # mask pixel not white
                a = pixelMean(base_rgb.getpixel((w - 1, h)), base_rgb.getpixel((w, h - 1)),
                              base_rgb.getpixel((w - 1, h - 1)))

                A[h, w] = a

                print (h, w, a)

                b = watermark_rgb.getpixel((w, h))
                pixels[w, h] = F_inverse_rgb(b, F_rgb(a, base_rgb.getpixel((w, h))))
            else:
                pixels[w, h] = base_pixel[w, h]

    result.show()

    return A, B


def pixelMean(p1, p2, p3):
    p = (int((p1[0] + p2[0] + p3[0]) / 3),
         int((p1[1] + p2[1] + p3[1]) / 3),
         int((p1[2] + p2[2] + p3[2]) / 3))

    return p


def removeWatermark(coverImg, A, B):
    pass
