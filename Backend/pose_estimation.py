import time
import cv2
import math
import sys
import numpy as np


def face_orientation(landmarks):
    size=[1080,1440]
    image_points = np.array([
                            (landmarks[4], landmarks[5]),     # Nose tip
                            (landmarks[10], landmarks[11]),   # Chin
                            (landmarks[0], landmarks[1]),     # Left eye left corner
                            (landmarks[2], landmarks[3]),     # Right eye right corne
                            (landmarks[6], landmarks[7]),     # Left Mouth corner
                            (landmarks[8], landmarks[9])      # Right mouth corner
                        ], dtype="double")

    

                        
    model_points = np.array([
                            (0.0, 0.0, 0.0),             # Nose tip
                            (0.0, -330.0, -65.0),        # Chin
                            (-165.0, 170.0, -135.0),     # Left eye left corner
                            (165.0, 170.0, -135.0),      # Right eye right corne
                            (-150.0, -150.0, -125.0),    # Left Mouth corner
                            (150.0, -150.0, -125.0)      # Right mouth corner                         
                        ])

    # Camera internals
 
    center = (size[1]/2, size[0]/2)
    focal_length = center[0] / np.tan(60/2 * np.pi / 180)
    camera_matrix = np.array(
                         [[focal_length, 0, center[0]],
                         [0, focal_length, center[1]],
                         [0, 0, 1]], dtype = "double"
                         )

    dist_coeffs = np.zeros((4,1)) # Assuming no lens distortion
    (success, rotation_vector, translation_vector) = cv2.solvePnP(model_points, image_points, camera_matrix, dist_coeffs, flags=cv2.cv2.SOLVEPNP_ITERATIVE)

    
    axis = np.float32([[500,0,0], 
                          [0,500,0], 
                          [0,0,500]])
                          
    imgpts, jac = cv2.projectPoints(axis, rotation_vector, translation_vector, camera_matrix, dist_coeffs)
    modelpts, jac2 = cv2.projectPoints(model_points, rotation_vector, translation_vector, camera_matrix, dist_coeffs)
    rvec_matrix = cv2.Rodrigues(rotation_vector)[0]

    proj_matrix = np.hstack((rvec_matrix, translation_vector))
    eulerAngles = cv2.decomposeProjectionMatrix(proj_matrix)[6] 

    
    pitch, yaw, roll = [math.radians(_) for _ in eulerAngles]


    pitch = math.degrees(math.asin(math.sin(pitch)))
    roll = -math.degrees(math.asin(math.sin(roll)))
    yaw = math.degrees(math.asin(math.sin(yaw)))
    return (str(int(roll)), str(int(pitch)), str(int(yaw)))


start = time.time()
landmarks = [sys.argv[1], sys.argv[2], sys.argv[3],sys.argv[4],sys.argv[5],sys.argv[6],sys.argv[7],sys.argv[8],sys.argv[9],sys.argv[10],sys.argv[11],sys.argv[12]]
rotate_degree = face_orientation(landmarks)
print (sys.argv[13], rotate_degree[0], rotate_degree[1], rotate_degree[2])
#print ("time : ", time.time()-start)
    
    
    #remapping = [2,3,0,4,5,1]
    #for index in range(len(landmarks)/2):
    #    random_color = tuple(np.random.random_integers(0,255,size=3))
 
        
            

