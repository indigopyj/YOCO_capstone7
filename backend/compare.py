import cv2
import numpy as np
import os
import argparse
from matching import find_match
import json

def main():
    '''
            arguemt example:
                python3 compare.py rent_id part
            rentid : ex. 1234
                    unique rent id
            part : choose one of ['ff', 'ft', 'bf', 'bt', 'lf', 'lb', 'rf', 'rb']
            filename : ex. 1234_profile.jpg
                    file name of before and after images. They have same name but in different directories.
    '''

    parser = argparse.ArgumentParser(description='Arguments for predicting yolo result')
    parser.add_argument('rent_id', type=str, help="unique rent id for comparison")
    parser.add_argument('part', type=str, help="one of positions of the car")

    args = parser.parse_args()
    rent_id = args.rent_id
    part = args.part

    file_name = rent_id + "_" + part  # ex. 1234_ff
    before_file_name = file_name + "_b"  # ex. 1234_ff_b
    after_file_name = file_name + "_a"   # ex. 1234_ff_a

    photo_path = "photos/"
    yolo_path = "results/yolo/"
    compare_path = "results/compare/"

    before_box_json = yolo_path + before_file_name +'.json'
    after_box_json = yolo_path + after_file_name + '.json'
    with open(before_box_json, 'r') as f:
        before_box = json.load(f)
    with open(after_box_json, 'r') as f:
        after_box = json.load(f)


    before_img = cv2.imread(photo_path + before_file_name + ".jpg")
    after_img = cv2.imread(photo_path + after_file_name + ".jpg")

    before_boxes = []   # save label and coordinate of bounding box
    after_boxes = []    # save label and coordinate of bounding box

    # get box coordinates from json file
    for box in before_box["predictions"]:
        before_boxes.append((box["label"], int(box["topx"]), int(box["topy"]), int(box["btmx"]), int(box["btmy"])))

    for box in after_box["predictions"]:
        after_boxes.append((box["label"], int(box["topx"]), int(box["topy"]), int(box["btmx"]), int(box["btmy"])))

    match_boxes = find_match(before_img, before_boxes, after_img, after_boxes)

    new_defect_path = compare_path + file_name + '.json'
    new_defects = []
    new_defect_dict = dict()
    for box in match_boxes:
        defect = dict()
        defect["label"] = box[0]
        defect["topx"] = box[1]
        defect["topy"] = box[2]
        defect["btmx"] = box[3]
        defect["btmy"] = box[4]

        new_defects.append(defect)

    new_defect_dict["new_defects"] = new_defects

    with open(new_defect_path, 'w', encoding='utf-8') as make_file:
        json.dump(new_defect_dict, make_file, indent='\t')
    make_file.close()




if __name__ == '__main__':
    main()
