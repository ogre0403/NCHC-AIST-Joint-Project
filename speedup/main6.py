from PIL import Image
import ctypes as c
import numpy as np
import multiprocessing as mp
import Queue


def do_job(width, height, tasks_to_accomplish, mp_arr):
    while True:
        try:
            '''
                try to get task from the queue. get_nowait() function will 
                raise queue.Empty exception if the queue is empty. 
                queue(False) function would do the same task also.
            '''
            start = tasks_to_accomplish.get_nowait()
        except Queue.Empty:
            break
        else:
            '''
                if no exception has been raised, add the task completion 
                message to task_that_are_done queue
            '''
            b = np.frombuffer(mp_arr.get_obj(), dtype="uint8").reshape((height, width, 3))
            for w in range(start * 100, (start + 1) * 100):
                for h in range(start * 100, (start + 1) * 100):
                    b[h, w] = [0, 0, 0]

            print('done by ' + mp.current_process().name)
    return True


def main():
    im = Image.open('./light.jpg')
    width, height = im.size
    channel = 3
    im.show()
    im2arr = np.array(im)

    im_reshape = im2arr.reshape(width * height * channel)
    mp_arr = mp.Array(c.c_uint8, width * height * channel)
    arr = np.frombuffer(mp_arr.get_obj(), dtype="uint8")
    b = arr.reshape(width * height * channel)
    for x in range(width * height * channel):
        b[x] = im_reshape[x]

    number_of_task = 3
    number_of_processes = mp.cpu_count()
    tasks_to_accomplish = mp.Queue()
    processes = []

    for i in range(number_of_task):
        tasks_to_accomplish.put(i)

    # creating processes
    for w in range(number_of_processes):
        p = mp.Process(target=do_job, args=(width, height, tasks_to_accomplish, mp_arr))
        processes.append(p)
        p.start()

    # completing process
    for p in processes:
        p.join()

    b = arr.reshape((height, width, channel))
    arr2im = Image.fromarray(b)
    arr2im.show()


if __name__ == "__main__":
    main()
