from PIL import Image
import numpy as np
import monotonic


def F(x, p):
    return (monotonic.FTable[x[0]][p[0]],
            monotonic.FTable[x[1]][p[1]],
            monotonic.FTable[x[2]][p[2]])


def F_inverse(x, q):
    return (monotonic.F_inv_Table[x[0]][q[0]],
            monotonic.F_inv_Table[x[1]][q[1]],
            monotonic.F_inv_Table[x[2]][q[2]])


def estimate_a(p1, p2, p3):
    p = (int((p1[0] + p2[0] + p3[0]) / 3),
         int((p1[1] + p2[1] + p3[1]) / 3),
         int((p1[2] + p2[2] + p3[2]) / 3))

    return p


def addWatermark(base, watermark):
    base_image = Image.open(base)
    base_rgb = base_image.convert('RGB')
    base_pixel = base_image.load()
    width, height = base_image.size

    watermark = Image.open(watermark).resize(base_image.size)
    watermark_rgb = watermark.convert('RGB')

    result = Image.new('RGB', base_image.size)
    pixels = result.load()

    for w in range(width):
        pixels[w, 0] = base_pixel[w, 0]

    for h in range(height):
        pixels[0, h] = base_pixel[0, h]

    A = np.zeros([width, height], dtype=(int, 3))

    for h in range(1, height):
        for w in range(1, width):
            if watermark_rgb.getpixel((w, h)) != (255, 255, 255):
                a = estimate_a(base_rgb.getpixel((w - 1, h)),
                               base_rgb.getpixel((w, h - 1)),
                               base_rgb.getpixel((w - 1, h - 1)))

                # a = (128, 128, 128)
                A[w, h] = a
                b = watermark_rgb.getpixel((w, h))
                pixels[w, h] = F_inverse(b, F(a, base_rgb.getpixel((w, h))))
            else:
                pixels[w, h] = base_pixel[w, h]

    return A, result


def removeWatermark(coverImg, A, watermark):
    width = A.shape[0]
    height = A.shape[1]

    watermark = Image.open(watermark).resize(coverImg.size)
    watermark_rgb = watermark.convert('RGB')

    result = Image.new('RGB', (width, height))
    pixels = result.load()

    base_pixel = coverImg.load()

    for w in range(width):
        pixels[w, 0] = base_pixel[w, 0]

    for h in range(height):
        pixels[0, h] = base_pixel[0, h]

    for h in range(height - 1, 0, -1):
        for w in range(width - 1, 0, -1):
            if watermark_rgb.getpixel((w, h)) != (255, 255, 255):
                a = A[w, h]
                # a = (128, 128, 128)
                b = watermark_rgb.getpixel((w, h))
                pixels[w, h] = F_inverse(a, F(b, base_pixel[w, h]))
            else:
                pixels[w, h] = base_pixel[w, h]

    return result
