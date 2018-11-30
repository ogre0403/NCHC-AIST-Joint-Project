from PIL import Image
import monotonic
import multiprocessing as mp
import Queue
import numpy as np
import ctypes as c


def F(x, p):
    return (monotonic.FTable[x[0]][p[0]],
            monotonic.FTable[x[1]][p[1]],
            monotonic.FTable[x[2]][p[2]])


def F_inverse(x, q):
    return (monotonic.F_inv_Table[x[0]][q[0]],
            monotonic.F_inv_Table[x[1]][q[1]],
            monotonic.F_inv_Table[x[2]][q[2]])


def estimate_a(p1, p2, p3):
    p = (
        (p1[0] / 3) + (p2[0] / 3) + (p3[0] / 3),
        (p1[1] / 3) + (p2[1] / 3) + (p3[1] / 3),
        (p1[2] / 3) + (p2[2] / 3) + (p3[2] / 3)
    )

    return p


def addWatermark(origin, watermark, blk_width=0, blk_height=0, proc_num=0):
    origin_image = Image.open(origin)
    width, height = origin_image.size
    channel = 3

    origin_image_nparray = np.array(origin_image)
    im_reshape = origin_image_nparray.reshape(width * height * channel)
    origin_arr = mp.Array(c.c_uint8, width * height * channel)
    arr = np.frombuffer(origin_arr.get_obj(), dtype="uint8")
    b = arr.reshape(width * height * channel)
    for x in range(width * height * channel):
        b[x] = im_reshape[x]

    if blk_width == 0:
        blk_width = width

    if blk_height == 0:
        blk_height = height

    # process number should not exceed cpu count
    if proc_num <= 0 or proc_num > mp.cpu_count():
        number_of_processes = mp.cpu_count()
    else:
        number_of_processes = proc_num

    tasks_to_accomplish = mp.Queue()
    processes = []

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

            # add task to queue, speedup by multiple process
            tasks_to_accomplish.put((start_x, start_y, end_x, end_y))
            # addWatermarkBlock((start_x, start_y), (end_x, end_y), watermark, origin_pixel, result_pixel)

    # creating processes for parallel processing
    for w in range(number_of_processes):
        p = mp.Process(target=AddWatermarkJob, args=(width, height, tasks_to_accomplish, origin_arr, watermark))
        processes.append(p)
        p.start()

    # wait for process complete
    for p in processes:
        p.join()

    # reshape 1-D shared array back to 2-D image
    b = arr.reshape((height, width, channel))
    arr2im = Image.fromarray(b)
    return arr2im


def AddWatermarkJob(width, height, tasks_to_accomplish, mp_arr, watermark_path):
    water_img = Image.open(watermark_path)

    while True:
        try:
            '''
                try to get task from the queue. get_nowait() function will 
                raise queue.Empty exception if the queue is empty. 
                queue(False) function would do the same task also.
            '''
            (start_x, start_y, end_x, end_y) = tasks_to_accomplish.get_nowait()

        except Queue.Empty:
            break
        else:
            '''
                if no exception has been raised, add the task completion 
                message to task_that_are_done queue
            '''
            sub_width = end_x - start_x + 1
            sub_height = end_y - start_y + 1

            watermark_rgb = water_img.resize((sub_width, sub_height)).convert('RGB')

            origin_pixel = np.frombuffer(mp_arr.get_obj(), dtype="uint8").reshape((height, width, 3))

            for h in range(start_y, end_y):
                for w in range(start_x, end_x):
                    if watermark_rgb.getpixel((w - start_x, h - start_y)) != (255, 255, 255):
                        a = estimate_a(origin_pixel[h, w + 1],
                                       origin_pixel[h + 1, w],
                                       origin_pixel[h + 1, w + 1])

                        b = watermark_rgb.getpixel((w - start_x, h - start_y))
                        origin_pixel[h, w] = F_inverse(b, F(a, origin_pixel[h, w]))
                    else:
                        origin_pixel[h, w] = origin_pixel[h, w]
    return True


def removeWatermark(masked_image, watermark, blk_width=0, blk_height=0, proc_num=0):
    width, height = masked_image.size
    channel = 3

    origin_image_nparray = np.array(masked_image)
    im_reshape = origin_image_nparray.reshape(width * height * channel)
    origin_arr = mp.Array(c.c_uint8, width * height * channel)
    arr = np.frombuffer(origin_arr.get_obj(), dtype="uint8")
    b = arr.reshape(width * height * channel)
    for x in range(width * height * channel):
        b[x] = im_reshape[x]

    if blk_width == 0:
        blk_width = width

    if blk_height == 0:
        blk_height = height

    # process number should not exceed cpu count
    if proc_num <= 0 or proc_num > mp.cpu_count():
        number_of_processes = mp.cpu_count()
    else:
        number_of_processes = proc_num

    tasks_to_accomplish = mp.Queue()
    processes = []

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

            # add task to queue, speedup by multiple process
            tasks_to_accomplish.put((start_x, start_y, end_x, end_y))

    # creating processes for parallel processing
    for w in range(number_of_processes):
        p = mp.Process(target=RemoveWatermarkJob, args=(width, height, tasks_to_accomplish, origin_arr, watermark))
        processes.append(p)
        p.start()

    # wait for process complete
    for p in processes:
        p.join()

    # reshape 1-D shared array back to 2-D image
    b = arr.reshape((height, width, channel))
    arr2im = Image.fromarray(b)
    return arr2im


def RemoveWatermarkJob(width, height, tasks_to_accomplish, mp_arr, watermark_path):
    water_img = Image.open(watermark_path)

    while True:
        try:
            '''
                try to get task from the queue. get_nowait() function will 
                raise queue.Empty exception if the queue is empty. 
                queue(False) function would do the same task also.
            '''
            (start_x, start_y, end_x, end_y) = tasks_to_accomplish.get_nowait()

        except Queue.Empty:
            break
        else:
            '''
                if no exception has been raised, add the task completion 
                message to task_that_are_done queue
            '''
            sub_width = end_x - start_x + 1
            sub_height = end_y - start_y + 1

            watermark_rgb = water_img.resize((sub_width, sub_height)).convert('RGB')

            origin_pixel = np.frombuffer(mp_arr.get_obj(), dtype="uint8").reshape((height, width, 3))

            for h in range(end_y - 1, start_y - 1, -1):
                for w in range(end_x - 1, start_x - 1, -1):
                    if watermark_rgb.getpixel((w - start_x, h - start_y)) != (255, 255, 255):
                        a = estimate_a(origin_pixel[h, w + 1],
                                       origin_pixel[h + 1, w],
                                       origin_pixel[h + 1, w + 1])

                        b = watermark_rgb.getpixel((w - start_x, h - start_y))
                        origin_pixel[h, w] = F_inverse(a, F(b, origin_pixel[h, w]))
                    else:
                        origin_pixel[h, w] = origin_pixel[h, w]
    return True
