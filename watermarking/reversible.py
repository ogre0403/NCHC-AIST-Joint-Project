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

    result_image = Image.new('RGB', (width, height))
    result_pixel = result_image.load()

    if blk_width == 0:
        blk_width = width

    if blk_height == 0:
        blk_height = height

    # add watermark block by block in generic form
    # ref: https://stackoverflow.com/questions/2356501/how-do-you-round-up-a-number-in-python
    for x in range(width // blk_width + (width % blk_width > 0)):
        for y in range(height // blk_height + (height % blk_height > 0)):
            start_x = x * blk_width
            start_y = y * blk_height
            end_x = (x + 1) * blk_width - 1
            end_y = (y + 1) * blk_height - 1

            if x == (width // blk_width + (width % blk_width > 0) - 1):
                end_x = width - 1

            if y == (height // blk_height + (height % blk_height > 0) - 1):
                end_y = height - 1
            # todo: speedup by multiple process
            addWatermarkBlock((start_x, start_y), (end_x, end_y), watermark, origin_pixel, result_pixel)

    return result_image


def addWatermarkBlock(start_wh, end_wh, watermark, origin_pixel, result_pixel):
    width = end_wh[0] - start_wh[0] + 1
    height = end_wh[1] - start_wh[1] + 1

    watermark = Image.open(watermark).resize((width, height))
    watermark_rgb = watermark.convert('RGB')

    for w in range(start_wh[0], end_wh[0]):
        result_pixel[w, end_wh[1]] = origin_pixel[w, end_wh[1]]

    for h in range(start_wh[1], end_wh[1]):
        result_pixel[end_wh[0], h] = origin_pixel[end_wh[0], h]

    for h in range(start_wh[1], end_wh[1]):
        for w in range(start_wh[0], end_wh[0]):
            if watermark_rgb.getpixel((w - start_wh[0], h - start_wh[1])) != (255, 255, 255):
                a = estimate_a(origin_pixel[w + 1, h],
                               origin_pixel[w, h + 1],
                               origin_pixel[w + 1, h + 1])

                b = watermark_rgb.getpixel((w - start_wh[0], h - start_wh[1]))
                result_pixel[w, h] = F_inverse(b, F(a, origin_pixel[w, h]))
            else:
                result_pixel[w, h] = origin_pixel[w, h]


def removeWatermark(masked_image, watermark, blk_width=0, blk_height=0):
    width, height = masked_image.size
    masked_pixel = masked_image.load()

    result_image = Image.new('RGB', (width, height))
    result_pixel = result_image.load()

    if blk_width == 0:
        blk_width = width

    if blk_height == 0:
        blk_height = height

    # remove watermark block by block in generic form
    for x in range(width // blk_width + (width % blk_width > 0)):
        for y in range(height // blk_height + (height % blk_height > 0)):
            start_x = x * blk_width
            start_y = y * blk_height
            end_x = (x + 1) * blk_width - 1
            end_y = (y + 1) * blk_height - 1

            if x == (width // blk_width + (width % blk_width > 0) - 1):
                end_x = width - 1

            if y == (height // blk_height + (height % blk_height > 0) - 1):
                end_y = height - 1
            # todo: speedup by multiple process
            removeWatermarkBlock((start_x, start_y), (end_x, end_y), watermark, masked_pixel, result_pixel)

    return result_image


def removeWatermarkBlock(start_wh, end_wh, watermark_file, masked_pixel, result_pixel):
    width = end_wh[0] - start_wh[0] + 1
    height = end_wh[1] - start_wh[1] + 1

    watermark = Image.open(watermark_file).resize((width, height))
    watermark_rgb = watermark.convert('RGB')

    for w in range(start_wh[0], end_wh[0]):
        result_pixel[w, end_wh[1]] = masked_pixel[w, end_wh[1]]

    for h in range(start_wh[1], end_wh[1]):
        result_pixel[end_wh[0], h] = masked_pixel[end_wh[0], h]

    for h in range(end_wh[1] - 1, start_wh[1] - 1, -1):
        for w in range(end_wh[0] - 1, start_wh[0] - 1, -1):
            # print(start_wh)
            # print(end_wh)
            # print(w, h)
            # print("=====")
            if watermark_rgb.getpixel((w - start_wh[0], h - start_wh[1])) != (255, 255, 255):
                a = estimate_a(result_pixel[w + 1, h],
                               result_pixel[w, h + 1],
                               result_pixel[w + 1, h + 1])
                b = watermark_rgb.getpixel((w - start_wh[0], h - start_wh[1]))
                result_pixel[w, h] = F_inverse(a, F(b, masked_pixel[w, h]))
            else:
                result_pixel[w, h] = masked_pixel[w, h]
