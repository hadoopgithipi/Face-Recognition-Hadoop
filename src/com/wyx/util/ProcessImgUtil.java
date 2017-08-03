package com.wyx.util;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * 加工图片
 * srcImage------>greyImage 灰度图
 * greyImage----->resizeImage 固定大小
 * resizeImage--->equalizedImg 直方图
 * @author hadoop
 *
 */
public class ProcessImgUtil {
	
	private static Logger logger = Logger.getLogger(ProcessImgUtil.class);
	private static PropUtil prop = PropUtil.getInstance();
	private static final int IMAGE_WIDTH;
	private static final int IMAGE_HEIGHT;
	static {
		IMAGE_WIDTH = Integer.parseInt(prop.getProperty("FACE_IMAGE_WIDTH"));
		IMAGE_HEIGHT = Integer.parseInt(prop.getProperty("FACE_IMAGE_HEIGHT"));
		//System.load("/usr/local/lib/libopencv_core.so.2.4");
	}
	
	/**
	 * 转化为灰度图
	 * @param imageSrc  原图像
	 * @return 返回灰度图
	 */
    public static IplImage convertImageToGreyscale(IplImage imageSrc){
    	IplImage imageGrey = null;
	  	// Either convert the image to greyscale, or make a copy of the existing greyscale image.
	  	// This is to make sure that the user can always call cvReleaseImage() on the output, whether it was greyscale or not.
	  	if (imageSrc.nChannels()==3) {
	  		imageGrey = cvCreateImage( cvGetSize(imageSrc), IPL_DEPTH_8U, 1 );
	  		cvCvtColor( imageSrc, imageGrey, CV_BGR2GRAY );
	  	}else {
	  		imageGrey = cvCloneImage(imageSrc);
	  	}
	  	return imageGrey;
	}
    /**
	 * 将灰度图像裁剪出人脸图像
	 * @param img 灰度图像
	 * @param region ROI 感兴趣的区域
	 * @return 返回人脸图像
	 */
	 public static IplImage cropImage(IplImage img, CvRect region){
		 IplImage imageTmp = null;
	  	 IplImage imageRGB = null;	
	  	 if (img.depth() != IPL_DEPTH_8U) {
	  		 logger.info("ERROR in cropImage: Unknown image depth of");
	  		 logger.info(String.valueOf(img.depth()));
	  		 logger.info(" given in cropImage() instead of 8 bits per pixel.");
	  		 System.exit(1);
	  	 }
	
	  	 // First create a new (color or greyscale) IPL Image and copy contents of img into it.
	  	 imageTmp = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, img.nChannels());
	  	 cvCopy(img, imageTmp);
	  	
	  	 // Create a new image of the detected region
	  	 // Set region of interest to that surrounding the face ROI(感兴趣的区域)
	  	 cvSetImageROI(imageTmp, region);
	  	 // Copy region of interest (i.e. face) into a new iplImage (imageRGB) and return it
	  	 imageRGB = cvCreateImage(cvSize(region.width(),region.height()),  IPL_DEPTH_8U, img.nChannels());
	  	 // Copy just the region.
	  	 cvCopy(imageTmp, imageRGB);	
	
	  	 cvReleaseImage(imageTmp);
	  	 return imageRGB;	
	 }
	 /**
	 * 将人脸图像转化为固定大小
	 * @param origImg 人脸图像
	 * @return 固定大小的图像
	 */
	 @Test 
	 public static IplImage resizeImage(IplImage origImg){
		 IplImage outImg = null;
		 int origWidth=0;
		 int origHeight=0;
		 if (origImg!=null) {
			 origWidth = origImg.width();
			 origHeight = origImg.height();
		 }
		 if (IMAGE_WIDTH <= 0 || IMAGE_HEIGHT <= 0 || origImg == null || origWidth <= 0 || origHeight <= 0) {
			 logger.info("ERROR in resizeImage: Bad desired image size of");
			 logger.info(String.valueOf(IMAGE_WIDTH)+","+String.valueOf(IMAGE_HEIGHT));
			 System.exit(1);
		 }
		
		 // Scale the image to the new dimensions, even if the aspect ratio will be changed.
		 outImg = cvCreateImage(cvSize(IMAGE_WIDTH, IMAGE_HEIGHT), origImg.depth(), origImg.nChannels());
		 if (IMAGE_WIDTH > origImg.width() && IMAGE_HEIGHT > origImg.height()) {
			 // Make the image larger
			 cvResetImageROI(origImg);
     		 // CV_INTER_CUBIC or CV_INTER_LINEAR is good for enlarging
			 cvResize(origImg, outImg, CV_INTER_LINEAR);	
		 }else {
			 // Make the image smaller
			 cvResetImageROI((IplImage)origImg);
		  	 // CV_INTER_AREA is good for shrinking / decimation, but bad at enlarging.
			 cvResize(origImg, outImg, CV_INTER_AREA);	
		 }
		
		  return outImg;
	 }
}
