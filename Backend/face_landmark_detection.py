import sys
import os
import dlib
import glob
import cv2  #opencv 사용

#opencv에서 ESC 키입력 상수
ESC_KEY = 27


def swapRGB2BGR(rgb):
    r, g, b = cv2.split(img)
    bgr = cv2.merge([b,g,r])
    return bgr


if len(sys.argv) != 3:
    exit()


predictor_path = sys.argv[1]

faces_folder_path = sys.argv[2]

detector = dlib.get_frontal_face_detector()

predictor = dlib.shape_predictor(predictor_path)

outfile = open("./landmark.txt", "w")

cntt = 0

test = glob.glob(os.path.join(faces_folder_path, "img00*.jpg"))

for f in test:
    cntt= cntt +1
    imgnumber = int(f[13:19]     )
    print(imgnumber) 
    if(imgnumber >=2650) :
        continue

    img = dlib.load_rgb_image(f)      
    

    cvImg = swapRGB2BGR(img)    

    cvImg = cv2.resize(cvImg, None, fx=2, fy=2, interpolation=cv2.INTER_AREA)
    height, width, channel = cvImg.shape
    

    dets = detector(img, 1)



    for k, d in enumerate(dets):

        shape = predictor(img, d)

        position = dict() 
        for i in range(0, shape.num_parts):

            x = shape.part(i).x*2
            y = shape.part(i).y*2

            pos_list = []
           
            if i == 36 or i == 45 or i == 33 or i == 48 or i == 54 or i == 8 or i == 39 or i == 42:
                pos_list.append(int(x/2))
                pos_list.append(int(y/2))
                position[i] = pos_list 
                
        txt_data = str(f[10:19])+" "+str(int(height/2))+" "+str(int(width/2))+" "+str(int((position[36][0]+position[39][0])/2))+" "+str(int((position[36][1]+position[39][1])/2))+" "+str(int((position[45][0]+position[42][0])/2))+" "+str(int((position[45][1]+position[42][1])/2))+" "+str(position[33][0])+" "+str(position[33][1])+" "+str(position[48][0])+" "+str(position[48][1])+" "+str(position[54][0])+" "+str(position[54][1])+" "+str(position[8][0])+" "+str(position[8][1])+"\n"
        outfile.write(txt_data)
   
print(cntt)
outfile.close()
