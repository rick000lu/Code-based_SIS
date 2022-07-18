import numpy as np
import cv2

index_table = []
#Generate image index table
def generate_index_table(img_shape):
    global index_table
    img_height = img_shape[0]
    img_width = img_shape[1]
    for i in range(img_height):
        for j in range(img_width):
            index_table.append((i, j))

#Randomly generate pixel position
def random_generate_pos():
    
    global index_table
    if len(index_table) == 0:
        return None
    pos = index_table.pop(np.random.randint(low = 0, high = len(index_table)))
    return pos

def generate_n_pos(n):
        pos_list = []
        for i in range(n):
            pos = random_generate_pos()
            pos_list.append(pos)
        
        return pos_list

def varify_image():
    img_shape = (512, 128)
    varified_img = np.full(shape = img_shape, fill_value = 255, dtype = np.uint8)
    #n = 100

    #generate index table
    

    for n in range(100, 200, 100):
        generate_index_table(img_shape)
        n_pos_list = generate_n_pos(n)
    
        for pos in n_pos_list:
            img_i, img_j = pos
            varified_img[img_i][img_j] = 0
    
        print(varified_img)
        cv2.imwrite(filename = './img/verification_posistion_img/varify_block_N_' + str(n) + '.bmp', img = varified_img)

varify_image()
