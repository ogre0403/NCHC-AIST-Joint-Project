from PIL import Image
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


def addWatermark(origin, watermark, blk_width=0, blk_height=0):
    origin_image = Image.open(origin)
    origin_pixel = origin_image.load()
    width, height = origin_image.size

    if blk_height != 0 and blk_width != 0:
        width = blk_width
        height = blk_height

    result_image = Image.new('RGB', (width, height))
    result_pixel = result_image.load()

    # todo: add watermark block by block
    addWatermarkBlock((0, 0), (width - 1, height - 1), watermark, origin_pixel, result_pixel)
    return result_image


def addWatermarkBlock(start_wh, end_wh, watermark, origin_pixel, result_pixel):
    width = end_wh[0] - start_wh[0] + 1
    height = end_wh[1] - start_wh[1] + 1

    watermark = Image.open(watermark).resize((width, height))
    watermark_rgb = watermark.convert('RGB')

    for w in range(width):
        result_pixel[w, end_wh[1]] = origin_pixel[w, end_wh[1]]

    for h in range(height):
        result_pixel[end_wh[0], h] = origin_pixel[end_wh[0], h]

    for h in range(start_wh[1], height - 1):
        for w in range(start_wh[0], width - 1):
            if watermark_rgb.getpixel((w, h)) != (255, 255, 255):
                a = estimate_a(origin_pixel[w + 1, h],
                               origin_pixel[w, h + 1],
                               origin_pixel[w + 1, h + 1])

                b = watermark_rgb.getpixel((w, h))
                result_pixel[w, h] = F_inverse(b, F(a, origin_pixel[w, h]))
            else:
                result_pixel[w, h] = origin_pixel[w, h]


def removeWatermark(coverImg, watermark):
    width, height = coverImg.size

    watermark = Image.open(watermark).resize(coverImg.size)
    watermark_rgb = watermark.convert('RGB')

    result = Image.new('RGB', (width, height))
    pixels = result.load()

    base_pixel = coverImg.load()

    for w in range(width):
        pixels[w, height - 1] = base_pixel[w, height - 1]

    for h in range(height):
        pixels[width - 1, h] = base_pixel[width - 1, h]

    for h in range(height - 2, -1, -1):
        for w in range(width - 2, -1, -1):
            if watermark_rgb.getpixel((w, h)) != (255, 255, 255):
                a = estimate_a(pixels[w + 1, h],
                               pixels[w, h + 1],
                               pixels[w + 1, h + 1])
                b = watermark_rgb.getpixel((w, h))
                pixels[w, h] = F_inverse(a, F(b, base_pixel[w, h]))
            else:
                pixels[w, h] = base_pixel[w, h]

    return result


def removeWatermarkBlock(coverImg, watermark):
    pass
