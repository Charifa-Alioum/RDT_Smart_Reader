# our imports
import numpy as np
import cv2



# here we define all functions that we will use

# this function crop an image by applying harris method's
def crop_tdr(image):
    h,w, c = image.shape
    # Make a copy of the image
    image_copy = np.copy(image)
    # Change color to RGB (from BGR)
    image_copy = cv2.cvtColor(image_copy, cv2.COLOR_BGR2RGB)
    # Convert to grayscale
    gray = cv2.cvtColor(image_copy, cv2.COLOR_RGB2GRAY)
    gray = np.float32(gray)
    # Detect corners 
    dst = cv2.cornerHarris(gray, 16, 19, 0.04)
    # Dilate corner image to enhance corner points
    dst = cv2.dilate(dst,None)
    # This value vary depending on the image and how many corners you want to detect
    thresh = 0.1*dst.max()
    # Create an image copy to draw corners on
    corner_image = np.copy(image_copy)

    # to get 4 borders of TDR
    min_i = w
    min_j = h
    max_i = 0
    max_j = 0

    # Iterate through all the corners and draw them on the image (if they pass the threshold)
    for j in range(0, dst.shape[0]):
        for i in range(0, dst.shape[1]):
            if(dst[j,i] > thresh):
                # get the 4 borders of detected corner of TDR
                min_i = min(min_i, i) 
                max_i = max(max_i, i)
                min_j = min(min_j, j) 
                max_j = max(max_j, j)

    # crop the TDR image
    cropped = image[min_j+2:max_j-2, min_i+5:max_i-5]

    return cropped


# to enhance contrast of image
def enhance_img(img, clip):
    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    clahe = cv2.createCLAHE(clipLimit=clip, tileGridSize=(10,10))
    final = clahe.apply(gray_img)

    # apply median filter
    final = cv2.medianBlur(final,5)

    return final


# to resize image in order to conserve ratio
def resize_image(image, width = None, height = None, inter = cv2.INTER_CUBIC):
    # initialize the dimensions of the image to be resized and
    # grab the image sizeDecoder
    dim = None
    (h, w) = image.shape[:2]
    print(h,w)

    # if both the width and height are None, then return the
    # original image
    if width is None and height is None:
        return image

    # check to see if the width is None
    if width is None:
        # calculate the ratio of the height and construct the
        # dimensions
        r = height / float(h)
        dim = (int(w * r), height)

    # otherwise, the height is None
    else:
        # calculate the ratio of the width and construct the
        # dimensions
        r = width / float(w)
        dim = (width, int(h * r))

    # resize the image
    resized = cv2.resize(image, dim, interpolation = inter)

    # return the resized image
    return resized


# to add black padding to roi
def padd_roi(image):
    TARGET_SIZE = 640
    (h, w) = image.shape[:2]
    img = np.copy(image)
    border_h = (TARGET_SIZE - h) // 2
    border_w = (TARGET_SIZE - w) // 2
    top, bottom, left, right = border_h, border_h, border_w, border_w
    borderType = cv2.BORDER_CONSTANT
    padded = cv2.copyMakeBorder( img, top, bottom, left, right, borderType)
    padded = cv2.resize(padded, dsize=(TARGET_SIZE,TARGET_SIZE), interpolation=cv2.INTER_CUBIC)
        
    return padded
  
  
# assemble all above
def preprocess(src):
    enhance_clip_value = 8.0
    width, height = (None, 590)
    img = cv2.imread(src, 1)
    # reduce resolution of images for best cropping
    img = resize_image(img, 440, None)

    cropped = crop_tdr(img) # crop
    cropped = resize_image(cropped, width, height) # resize
    cropped = enhance_img(cropped, clip=enhance_clip_value) # contrast enhancement
    cropped = cv2.cvtColor(cropped, cv2.COLOR_GRAY2RGB)
    cropped = padd_roi(cropped) # padding

    # get bytes to pass to java
    is_success, cropped_buf_arr = cv2.imencode(".jpg", cropped)
    result = cropped_buf_arr.tobytes()
	
    return result

def main():
    a = 0