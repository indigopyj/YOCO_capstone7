import cv2
import numpy as np
import math

def average_hash(image, hashSize=16):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    resized = cv2.resize(gray, (hashSize, hashSize))
    mean = np.mean(resized)
    bin_img = np.zeros((hashSize, hashSize))
    for i in range(hashSize):
        for j in range(hashSize):
            if (resized[i][j] >= mean):  # 평균보다 크면 1, 평균보다 작으면 0
                bin_img[i][j] = 1

    return bin_img.flatten()


def hamming_distance(x, y):
    aa = x.reshape(1, -1)
    bb = y.reshape(1,-1)
    dist = (aa != bb).sum()
    return dist

def find_match(before_img, before_boxes, after_img, after_boxes, threshold = 0.15):
    '''
    :param before_img : before image-array
    :param before_boxes: a list of items (label, topx, topy, btmx, btmy)
    :param after_img : after image-array
    :param after_boxes: a list of items (label, topx, topy, btmx, btmy)
    :param threshold: threshold for hamming distance
    :return:
    '''
    match_boxes = []
    same_set = []

    for i, after_coord in enumerate(after_boxes):
        '''
            after_coord : ( label, topx, topy, btmx, btmy)
        '''
        j = 0
        after = after_img[after_coord[2] : after_coord[4], after_coord[1] : after_coord[3]]
        for before_coord in before_boxes:
            before = before_img[before_coord[2]: before_coord[4], before_coord[1]: before_coord[3]]
            hash1 = average_hash(before)
            hash2 = average_hash(after)
            result = hamming_distance(hash1, hash2) / 256
            print(result, i, j)
            if (result < threshold):  # different defect
                same_set.append(i)

            j+=1
    #print()
    after_set = set(range(len(after_boxes)))
    #print(after_set)
    same_set = set(same_set)
    #print(same_set)
    after_set = after_set.difference(same_set) # get only different defects
    #print(after_set)

    print("the different defects are")

    for i in after_set:
        print(after_boxes[i])
        match_boxes.append(after_boxes[i])
    print()

    return match_boxes




