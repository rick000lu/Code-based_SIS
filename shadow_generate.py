import cv2
import numpy as np

def generate_random_shadow(shape):
    shadow = np.zeros(shape = shape, dtype = np.uint8)
    for i in range(shape[0]):
        for j in range(shape[1]):
            shadow[i][j] = np.random.randint(low = 0, high = 256, dtype = np.uint8)
    
    shadow = np.array(shadow)

    return shadow

def save_shadow(file_name, shadow_img):
    cv2.imwrite(filename = file_name, img = shadow_img)

def cyclic_shift_right(num, d, N):
    return ((num << d) % (1 << N)) | (num >> (N - d))

def generate_rotate_map(shape):
    rotate_map = np.zeros(shape = shape, dtype = np.uint8)
    for i in range(shape[0]):
        for j in range(shape[1]):
            rotate_map[i][j] = np.random.randint(low = 0, high = 8, dtype = np.uint8)
    
    return rotate_map

def generate_final_shadow(intermidiate_shadow, rotate_map):
    final_shadow = np.zeros_like(a = intermidiate_shadow, dtype = np.uint8)
    for i in range(intermidiate_shadow.shape[0]):
        for j in range(intermidiate_shadow.shape[1]):
            final_shadow[i][j] = cyclic_shift_right(num = intermidiate_shadow[i][j], d = rotate_map[i][j], N = 8)
    
    return final_shadow



shadow_shape = (512, 128)
shadows = []
final_shadows = []

#Generate intermidiate shadow
file_name = 'intermidiate_shadow'
save_dir = './img/shadows/'
for i in range(4):
    shadow = generate_random_shadow(shadow_shape)
    shadows.append(shadow)
    save_shadow(file_name = save_dir + file_name + '_' + str(i + 1) + '.bmp', shadow_img = shadow)

#Generate final shadow
file_name = 'final_shadow'
for i in range(len(shadows)):
    rotate_map = generate_rotate_map(shape = shadow_shape)
    final_shadow = generate_final_shadow(intermidiate_shadow = shadows[i], rotate_map = rotate_map)
    save_shadow(file_name = save_dir + file_name + '_' + str(i + 1) + '.bmp', shadow_img = final_shadow)