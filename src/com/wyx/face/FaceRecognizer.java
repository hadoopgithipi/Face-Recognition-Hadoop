package com.wyx.face;
		
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_legacy.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;	
import com.wyx.GUI.FunctionPanel;
import com.wyx.GUI.RegisterFaceDialog;
import com.wyx.util.LocalFileUtil;
import com.wyx.util.ProcessImgUtil;
import com.wyx.util.PropUtil;

import static com.googlecode.javacv.cpp.opencv_core.CvMat;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.CV_FONT_HERSHEY_COMPLEX_SMALL;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.CvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvInitFont;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvPutText;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.CV_L1;
import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_ITER;
import static com.googlecode.javacv.cpp.opencv_core.cvNormalize;
import static com.googlecode.javacv.cpp.opencv_core.CvFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvWriteInt;
import static com.googlecode.javacv.cpp.opencv_core.cvTermCriteria;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_WRITE;
import static com.googlecode.javacv.cpp.opencv_core.cvOpenFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvWrite;
import static com.googlecode.javacv.cpp.opencv_core.cvWriteString;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_READ;
import static com.googlecode.javacv.cpp.opencv_core.cvReadIntByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReadStringByName;
import static com.googlecode.javacv.cpp.opencv_core.cvReadByName;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvMinMaxLoc;

import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
	
public class FaceRecognizer{
	private Logger logger = Logger.getLogger(FaceRecognizer.class);
	private static String resourcePath = PropUtil.getInstance().getProperty("resourcePath"); 
	private CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(resourcePath+"data/haarcascade_frontalface_alt2.xml"));
	//存放人脸图像的本地路径
	private String localFaceFramesPath = PropUtil.getInstance().getProperty("local_faceFramesPath");
	//存放人脸图像的dfs路径
	private String dfsFaceFramesPath = PropUtil.getInstance().getProperty("dfs_faceFramesPath");
	
	//
	private int nTrainFaces = 0;
	//
	private int nPersons=0;
	//
	private int nEigens = 0;
	  
	private CvMat eigenValMat;
	private CvMat projectedTrainFaceMat;
	private CvMat trainPersonNumMat=null; 
	private IplImage pAvgTrainImg;
	private IplImage[] eigenVectArr;
	//
	private List<String> personNames = new ArrayList<String>();
	//
	private IplImage[] trainingFaceImgArr;
	//平均
	private CvMat personNumTruthMat;
	//注册人脸时保存的数量
	private int countSavedFace=1;
	private static String textName="unknow";
	public static String personName = "";
	private int faceFrameCount = 0;
	private static double g_confidence=0;  
	
	public FaceRecognizer() {
		trainPersonNumMat = loadTrainingData();
	}
	
	/**
	 * 载入训练数据
	 * @return
	 */
	public CvMat loadTrainingData() {
		logger.info("loading training data");
		 // the person numbers during training
		 CvMat pTrainPersonNumMat = null;
	
		 // create a file-storage interface
		 CvFileStorage fileStorage = cvOpenFileStorage(
				 resourcePath+"data/facedata.xml", // filename
				 null, // memstorage
				 CV_STORAGE_READ, // flags
				 null); // encoding
		 if (fileStorage == null) {
			 logger.info("Can't open training database file 'data/facedata.xml'.");
			 return null;
		 }
		 // Make sure it starts as empty.
		 personNames.clear();      
		 nPersons = cvReadIntByName(fileStorage, null, "nPersons", 0);
		 if (nPersons == 0) {
			 logger.info("No people found in the training database 'data/facedata.xml'.");
			 return null;
		 } else {
			 logger.info(nPersons + " persons read from the training database");
		 }
	
		 // Load each person's name.
		 String personName = null;
		 for (int i = 0; i < nPersons; i++) {
			 String varname = "personName_" + (i + 1);
			 personName = cvReadStringByName(fileStorage, null, varname,"");
			 personNames.add(personName);
		 }
		 logger.info("person names: " + personNames);
	
		 // Load the data
		 nEigens = cvReadIntByName(fileStorage, null, "nEigens",0); 
		 nTrainFaces = cvReadIntByName(fileStorage,null, "nTrainFaces",0);
		  
		 Pointer pointer = cvReadByName(fileStorage, null, "trainPersonNumMat"); 
		 pTrainPersonNumMat = new CvMat(pointer);
	
		 pointer = cvReadByName(fileStorage, null, "eigenValMat"); 
		 eigenValMat = new CvMat(pointer);
	
		 pointer = cvReadByName(fileStorage, null, "projectedTrainFaceMat"); 
		 projectedTrainFaceMat = new CvMat(pointer);
	
		 pointer = cvReadByName(fileStorage,null, "avgTrainImg");
		 pAvgTrainImg = new IplImage(pointer);
	
		 eigenVectArr = new IplImage[nTrainFaces];
		 for (int i = 0; i <= nEigens; i++) {
			 String varname = "eigenVect_" + i;
			 pointer = cvReadByName(fileStorage,null, varname);
			 eigenVectArr[i] = new IplImage(pointer);
		 }
	
		 // release the file-storage interface
		 cvReleaseFileStorage(fileStorage);
	
		 logger.info("Training data loaded (" + nTrainFaces + " training images of " + nPersons + " people)");
		 StringBuilder stringBuilder = new StringBuilder();
		 stringBuilder.append("People: ");
		 if (nPersons > 0) {
			 for (int i = 0; i < nPersons; i++) {
				 stringBuilder.append(", <").append(personNames.get(i)).append(">");
			 }
			 logger.info(stringBuilder.toString());
		 }
		 return pTrainPersonNumMat;
	 }	  
	
	/**
	 * 识别图片中的人脸
	 * @param filePath
	 */
	public void recongizeFormImage(String filePath){
		logger.info("被识别的图片的路径是： "+filePath);
		IplImage signleImage=null;
	 	//载入
		signleImage=cvLoadImage(filePath);
		if(!signleImage.isNull()){//输入不为空
			//识别人脸
			detectAndCropFromImg(filePath,signleImage,cascade,CV_HAAR_DO_CANNY_PRUNING | CV_HAAR_DO_ROUGH_SEARCH);
		}
//      cvShowImage("Press 'Esc' to exit",signleImage);
//	 	cvWaitKey(0);
//	 	cvDestroyWindow("Press 'Esc' to exit");
	}
	
	/**
	 *检测、裁剪 从图片中
	 * @param src 源文件
	 * @param cascade
	 * @param flag
	 */
	public void detectAndCropFromImg(String filePath,IplImage src,CvHaarClassifierCascade cascade,int flag){
		IplImage greyImg=null;
		IplImage faceImg=null;
		IplImage sizedImg=null;
		IplImage equalizedImg=null;
		CvRect r ;
//		CvFont font = null;
		CvFont font = new CvFont(CV_FONT_HERSHEY_COMPLEX_SMALL, 1, 1); 
		cvInitFont(font,CV_FONT_HERSHEY_COMPLEX_SMALL, 1.0, 0.8,1,1,CV_AA);
		greyImg = cvCreateImage( cvGetSize(src), IPL_DEPTH_8U, 1 );	 	
		greyImg=ProcessImgUtil.convertImageToGreyscale(src);
		CvMemStorage storage = CvMemStorage.create();
			
		CvSeq sign = cvHaarDetectObjects(
				greyImg,
				cascade,
				storage,
				1.1,
				3,
				flag);
		faceFrameCount = sign.total();
		cvClearMemStorage(storage);
		if(sign.total()>0){//检测到人脸
			for(int i=0;i<sign.total();i++){
				r = new CvRect(cvGetSeqElem(sign, i));
				faceImg = ProcessImgUtil.cropImage(greyImg, r);	
				sizedImg = ProcessImgUtil.resizeImage(faceImg);
				if(i==0){
					equalizedImg = cvCreateImage(cvGetSize(sizedImg), 8, 1);	
				}
				cvEqualizeHist(sizedImg, equalizedImg);				
				cvRectangle (
						src,
						cvPoint(r.x(), r.y()),
						cvPoint(r.width() + r.x(), r.height() + r.y()),
						CvScalar.WHITE,
						1,
						CV_AA,
						0);
				
				eigenDecomImg(equalizedImg);
				if(g_confidence*100>60){
//					if (personName.equals("")) {
//						cvPutText(src, textName,cvPoint(r.x()-10, r.y() + r.height() + 20), font, CvScalar.WHITE);
//						cvPutText(src, " conf="+Integer.valueOf((int) (g_confidence*100))+"%",cvPoint(r.x()-30, r.y() + r.height() + 40), font, CvScalar.GREEN);
//						saveFaceFrames(filePath, src);
//					}else if (!personName.equals("")&&textName.equals(personName)) {
//						cvPutText(src, personName,cvPoint(r.x()-10, r.y() + r.height() + 20), font, CvScalar.WHITE);
//						cvPutText(src, " conf="+Integer.valueOf((int) (g_confidence*100))+"%",cvPoint(r.x()-30, r.y() + r.height() + 40), font, CvScalar.GREEN);
//						saveFaceFrames(filePath, src);
//					} 
//					textName="unknow";
					if (!FunctionPanel.isRecog) {
						personName = textName;
					}else {
						if (!"".equals(personName) && textName.equals(personName)) {
							cvPutText(src, textName,cvPoint(r.x()-10, r.y() + r.height() + 20), font, CvScalar.WHITE);
							cvPutText(src, " conf="+Integer.valueOf((int) (g_confidence*100))+"%",cvPoint(r.x()-30, r.y() + r.height() + 40), font, CvScalar.GREEN);
							saveFaceFrames(filePath, src);
						}
						if("".equals(personName)){
							cvPutText(src, textName,cvPoint(r.x()-10, r.y() + r.height() + 20), font, CvScalar.WHITE);
							cvPutText(src, " conf="+Integer.valueOf((int) (g_confidence*100))+"%",cvPoint(r.x()-30, r.y() + r.height() + 40), font, CvScalar.GREEN);
							saveFaceFrames(filePath, src);
						}
					}
					FunctionPanel.isRecog = true;
				}
//				else{
//					cvPutText(src, "Unknow",cvPoint(r.x()-10, r.y() + r.height() + 20), font, CvScalar.WHITE);
//					cvPutText(src, " conf="+Integer.valueOf((int) (g_confidence*100))+"%",cvPoint(r.x()-30, r.y() + r.height() + 40), font, CvScalar.GREEN);
//				}
			}
		}else{
			cvPutText(src, "can't find any face!",cvPoint(src.width()/2, src.height()/2), font, CvScalar.GREEN);
		}
		//cvReleaseImage(greyImg);
		//if(!faceImg.isNull())
		//	cvReleaseImage(faceImg);
	  	//cvReleaseImage(sizedImg);
	  	//cvReleaseImage(equalizedImg);
	}
	
	
	
	
	/**
	 * 保存检测到的人脸图像
	 * @param filePath
	 * @param src
	 */
	public void saveFaceFrames(String filePath,IplImage src){
		Path filename = new Path(filePath);
//		if (new File(localFaceFramesPath).exists()) {
//			LocalFileUtil.deleteSrcDir(localFaceFramesPath);
//		}
	 	if (LocalFileUtil.mkdirDir(localFaceFramesPath)) {
			cvSaveImage(localFaceFramesPath+filename.getName(), src); 	
			logger.info("face frames is put into: "+localFaceFramesPath);
		}else {
			logger.info(localFaceFramesPath+"is not exists,save image is incompletely!");
			return;
		}
	}
	
	
	/**
	 * 特征分解
	 * @param src 直方图图像
	 */
	public void eigenDecomImg(IplImage src){
		float confidence = 0.0f;
	  	int  nearest=0;
	  	int iNearest=0;
		
	  	logger.info("=====================================Waiting For the camera .....");
	  	if( trainPersonNumMat==null) {
	  		logger.info("ERROR in recognizeFromCam(): Couldn't load the training data!\n");
	  		System.exit(1);
	  	}
	  	float[] projectedTestFace = new float[nEigens];
	  	cvEigenDecomposite(
	  			src,
	  			nEigens,
	  			eigenVectArr,
	  			0,
	  			null,
	  			pAvgTrainImg,
	  			projectedTestFace);

	  	FloatPointer pConfidence = new FloatPointer(confidence);
	  	iNearest = findNearestNeighbor(projectedTestFace, new FloatPointer(pConfidence));
	  	confidence = pConfidence.get();
	  	nearest = trainPersonNumMat.data_i().get(iNearest);
	  	textName=personNames.get(nearest-1);
	  	g_confidence=confidence;
	}
	
	
	/**
	  * 
	  * @param projectedTestFace
	  * @param pConfidencePointer
	  * @return
	  */
	public int findNearestNeighbor(float projectedTestFace[], FloatPointer pConfidencePointer) {
		double leastDistSq = Double.MAX_VALUE;
		int i = 0;
		int iTrain = 0;
		int iNearest = 0;
	
		logger.info("................");
		logger.info("find nearest neighbor from " + nTrainFaces + " training faces");
		for (iTrain = 0; iTrain < nTrainFaces; iTrain++) {
			//logger.info("considering training face " + (iTrain + 1));
			double distSq = 0;
	
			for (i = 0; i < nEigens; i++) {
				//logger.debug("  projected test face distance from eigenface " + (i + 1) + " is " + projectedTestFace[i]);
				float projectedTrainFaceDistance = (float) projectedTrainFaceMat.get(iTrain, i);
				float d_i = projectedTestFace[i] - projectedTrainFaceDistance;
				distSq += d_i * d_i; // / eigenValMat.data_fl().get(i);  // Mahalanobis distance (might give better results than Eucalidean distance)
			}
	
			if (distSq < leastDistSq) {
				leastDistSq = distSq;
				iNearest = iTrain;
				logger.info("  training face " + (iTrain + 1) + " is the new best match, least squared distance: " + leastDistSq);
			}
		}
	
		// Return the confidence level based on the Euclidean distance,
		// so that similar images should give a confidence between 0.5 to 1.0,
		// and very different images should give a confidence between 0.0 to 0.5.
		float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq / (float) (nTrainFaces * nEigens)) / 255.0f);
		pConfidencePointer.put(pConfidence);
	
		logger.info("training face " + (iNearest + 1) + " is the final best match, confidence " + pConfidence);
		return iNearest;
	}
	
	/**
	 * 注册人脸
	 * @param name 要注册的用户的姓名
	 * @return 注册成功返回true 否则返回false
	 * @throws Exception
	 */
	public boolean register(String name) throws Exception{	 
		boolean flag=true;
	 	OpenCVFrameGrabber  grabber = null;
	 	IplImage pFrame=null;
	  	int keypress = 0;
	  	int countSecond=0;
		  
	  	if(RegisterFaceDialog.flag){ //表示从视频中获取人脸并进行注册
	  		JFileChooser chooser = new JFileChooser();
	  		FileNameExtensionFilter filter = new FileNameExtensionFilter("AVI & WMV ", "avi", "wmv");
	  		chooser.setFileFilter(filter);
	  		int returnVal = chooser.showOpenDialog(null);
	  		if(returnVal == JFileChooser.APPROVE_OPTION) {
	  			String filename = chooser.getSelectedFile().getAbsolutePath();
	  			logger.info("You chose to open this file(register face): "+filename);
	  			grabber = new OpenCVFrameGrabber(filename);
	  		}else {
	  			logger.info("You don't choose an video file");
	  			return false;
	  		}
	  	}else{
	  		grabber = new OpenCVFrameGrabber(0);  //从摄像头中获取人脸并进行注册
	  	}
	  	grabber.start();
	  	logger.info("开始扫描，人脸注册进行中。。。。");
	  	pFrame = grabber.grab();		        	        
	  	while( pFrame!=null ){
	  		countSecond++;
	  		//检测
	  		detectForRegister(pFrame,cascade,CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH,name);
//	  		cvShowImage("Press 'Esc' to Stop",pFrame);
	  		pFrame = grabber.grab();
	  		keypress=cvWaitKey(24);
	  		if( keypress== 27 ||countSecond==100||countSavedFace==6){//27 代表Esc键
	  			grabber.release();
//	  			cvDestroyWindow("Press 'Esc' to Stop");
	  			break;  			
	  		}
	  	}

	  	personNames.add(name);
	  	//
	  	writeNameToTXT(name);
	  	learn(resourcePath+"data/ForTraining.txt");
	  	//cvReleaseImage(pFrame); JVM 崩溃
	  	return flag;
	}
	 
	 /**
	  * 从视频中或摄像头中检测人脸进行注册
	  * @param src 捕获到的图像
	  * @param cascade 
	  * @param flag 
	  * @param name 要注册的用户名
	  */
	 public void detectForRegister(IplImage src,CvHaarClassifierCascade cascade,int flag,String name){
		 IplImage greyImg=null;
	 	 IplImage faceImg=null;
	 	 IplImage sizedImg=null;
	 	 IplImage equalizedImg=null;

	 	 CvRect r ;
	 	 CvFont font = new CvFont(CV_FONT_HERSHEY_COMPLEX_SMALL, 1, 1); 
		 cvInitFont(font,CV_FONT_HERSHEY_COMPLEX_SMALL, 1.0, 0.8,1,1,CV_AA);
	 	 greyImg = cvCreateImage( cvGetSize(src), IPL_DEPTH_8U, 1 );	 	
		 greyImg=ProcessImgUtil.convertImageToGreyscale(src);
		 CvMemStorage storage = CvMemStorage.create();
		
		 CvSeq sign = cvHaarDetectObjects( greyImg , cascade , storage , 1.1 , 3 , flag );
		 cvClearMemStorage(storage);
		 storage.release();
		 if(sign.total()==1){	
			 r = new CvRect(cvGetSeqElem(sign, 0));
		  	 faceImg = ProcessImgUtil.cropImage(greyImg, r);		
			 sizedImg = ProcessImgUtil.resizeImage(faceImg);
			 equalizedImg = cvCreateImage(cvGetSize(sizedImg), 8, 1);	
			 cvEqualizeHist(sizedImg, equalizedImg);		
			 cvRectangle (
					 src,
					 cvPoint(r.x(), r.y()),
					 cvPoint(r.width() + r.x(), r.height() + r.y()),
					 CvScalar.WHITE,
					 1,
					 CV_AA,
					 0);
			 cvPutText(src, "This is your No."+String.valueOf(countSavedFace)+" photos. " ,cvPoint(r.x()-30, r.y() + r.height() + 30), font, CvScalar.RED);				
			 cvSaveImage(resourcePath+"img/"+name+countSavedFace+".jpg",equalizedImg);
			 cvWaitKey(1000);
			 countSavedFace++;	
			 cvReleaseImage(greyImg);
		  	 cvReleaseImage(faceImg);
		  	 cvReleaseImage(sizedImg);
		  	 cvReleaseImage(equalizedImg);	
		 }
	 }
		  			 
	 /**
	  * 将名字写入文档
	  * @param name 要注册的用户名
	  */
	 public void writeNameToTXT(String name) throws Exception{
		 String text=null;
		 int temp = personNames.size();
		 if(temp==0){
			 temp=temp+1;
		 }
		 
		 BufferedWriter bw = null;
		 try {
			 File file = new File(resourcePath+"data/ForTraining.txt");
			 FileOutputStream fos = new FileOutputStream(file,true);
			 bw = new BufferedWriter(new OutputStreamWriter(fos));
			 for(int i=1;i<6;i++){
				 text=temp+" "+name+" "+"img/"+name+i+".jpg";
				 bw.write(text);
				 bw.newLine();
			 } 
		 } catch (FileNotFoundException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }finally{
			 if (bw!=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					bw = null;
				}
			}
		 }
	}
	 
	 /**
	  * 
	  * @param trainingFileName  
	  */
	 public void learn(String trainingFileName) {
		 // load training data
		 logger.info("===========================================");
		 logger.info("Loading the training images in " + trainingFileName);
		 
		 trainingFaceImgArr = loadFaceImgArray(trainingFileName);
		 
		 nTrainFaces = trainingFaceImgArr.length;
		 logger.info("Got " + nTrainFaces + " training images");
		 if (nTrainFaces < 3) {
			 logger.info("Need 3 or more training faces\n"
	              + "Input file contains only " + nTrainFaces);
			 return;
	   	 }
	
		 // do Principal Component Analysis on the training faces
		 doPCA();
	
		 logger.info("projecting the training images onto the PCA subspace");
		 // project the training images onto the PCA subspace
		 projectedTrainFaceMat = cvCreateMat(
				  nTrainFaces, // rows
				  nEigens, // cols
				  CV_32FC1); // type, 32-bit float, 1 channel
	
		 // initialize the training face matrix - for ease of debugging
		 for (int i = 0; i < nTrainFaces; i++) {
			 for (int j = 0; j < nEigens; j++) {
				 projectedTrainFaceMat.put(i, j, 0.0);
			 }
		 }
	
		 logger.info("created projectedTrainFaceMat with " + nTrainFaces + " (nTrainFaces) rows and " + nEigens + " (nEigens) columns");
		 if (nTrainFaces < 5) {
			 logger.info("projectedTrainFaceMat contents:\n" + oneChannelCvMatToString(projectedTrainFaceMat));
		 }
	
		 FloatPointer floatPointer = new FloatPointer(nEigens);
		 for (int i = 0; i < nTrainFaces; i++) {
			 cvEigenDecomposite(
					 trainingFaceImgArr[i], // obj
					 nEigens, // nEigObjs
					 eigenVectArr, // eigInput (Pointer)
					 0, // ioFlags
					 null, // userData (Pointer)
					 pAvgTrainImg, // avg
					 floatPointer); // coeffs (FloatPointer)
	
			  if (nTrainFaces < 5) {
				  logger.info("floatPointer: " + floatPointerToString(floatPointer));
			  }
			  for (int j = 0; j < nEigens; j++) {
				  projectedTrainFaceMat.put(i, j, floatPointer.get(j));
			  }
		  }
		  if (nTrainFaces < 5) {
			  logger.info("projectedTrainFaceMat after cvEigenDecomposite:\n" + projectedTrainFaceMat);
		  }
	
		  // store the recognition data as an xml file
		  storeTrainingData();
	
		  // Save all the eigenvectors as images, so that they can be checked.
		  storeEigenfaceImages();
	  }
	 
	 /**
	  * 载入人脸图片数组
	  * @param filename
	  * @return 返回人脸图像数组
	  */
	 public IplImage [] loadFaceImgArray(String filename) {
		 IplImage[] faceImgArr = null;
		 BufferedReader imgListFile = null;
		 String imgFilename;
		 int nFaces = 0;
		 try {
			 File file = new File(filename);
			 // open the input file
			 imgListFile = new BufferedReader(new FileReader(filename));
			 // count the number of faces
			 String line = null;
			 imgListFile.mark((int)file.length()+1);
			 while ((line=imgListFile.readLine())!=null) {
				 nFaces++;
			 }
			 logger.info("already register nFaces: " + nFaces);
//			 imgListFile = new BufferedReader(new FileReader(filename));
	
			 // allocate the face-image array and person number matrix
			 faceImgArr = new IplImage[nFaces];
			 personNumTruthMat = cvCreateMat(
					 1, // rows
					 nFaces, // cols
					 CV_32SC1); // type, 32-bit unsigned, one channel
	
			 // initialize the person number matrix - for ease of debugging
			 for (int i= 0; i < nFaces; i++) {
				 personNumTruthMat.put(0, i, 0);
			 }
	
			 personNames.clear();        // Make sure it starts as empty.
			 nPersons = 0;
	
			 // store the face images in an array
			 imgListFile.reset();
			 String personName;
			 int personNumber;
			 for (int i = 0; i < nFaces; i++) {
				 // read person number (beginning with 1), their name and the image filename.
				 line = imgListFile.readLine();
				 if (line==null) {
					 break;
				 }
				 String[] tokens = line.split(" ");
				 personNumber = Integer.parseInt(tokens[0]);
				 personName = tokens[1];
				 imgFilename = tokens[2];
				 logger.info("Got " + i + " " + personNumber + " " + personName + " " + imgFilename);
	
				 // Check if a new person is being loaded.
				 if (personNumber > nPersons) {
					 // Allocate memory for the extra person (or possibly multiple), using this new person's name.
					 personNames.add(personName);
					 nPersons = personNumber;
					 logger.info("Got new person " + personName + " -> nPersons = " + nPersons + " [" + personNames.size() + "]");
				 }
	
				 // Keep the data
				 personNumTruthMat.put(0, i, personNumber); 
				 // load the face image
				 faceImgArr[i] = cvLoadImage(resourcePath+imgFilename, CV_LOAD_IMAGE_GRAYSCALE); 
				 if (faceImgArr[i] == null) {
					// throw new RuntimeException("Can't load image from " + imgFilename);
					 logger.info("Can't load image from " + imgFilename);
				 }
			 }
			 imgListFile.close();
		 }catch (IOException e) {
			 throw new RuntimeException(e);
		 }
		 logger.info("Data loaded from '" + filename + "': (" + nFaces + " images of " + nPersons + " people).");
		 StringBuilder stringBuilder = new StringBuilder();
		 stringBuilder.append("People: ");
		 if (nPersons > 0) {
			 stringBuilder.append("<").append(personNames.get(0)).append(">");
		 }
		 for (int i = 1; i < nPersons && i < personNames.size(); i++) {
			 stringBuilder.append(", <").append(personNames.get(i)).append(">");
		 }
		 logger.info(stringBuilder.toString());
		 return faceImgArr;
	 }
	
	 /**
	  * 提取人脸中的特征脸   降维 协防差矩阵
	  */
	 public void doPCA() {
		 CvTermCriteria calcLimit;
		 CvSize faceImgSize = new CvSize();
	
		 // set the number of eigenvalues to use
		 nEigens = nTrainFaces-1 ;
	
		 logger.info("allocating images for principal component analysis, using " + nEigens + (nEigens == 1 ? " eigenvalue" : " eigenvalues"));
	
		 // allocate the eigenvector images
		 faceImgSize.width(trainingFaceImgArr[0].width());
		 faceImgSize.height(trainingFaceImgArr[0].height());
		 eigenVectArr = new IplImage[nEigens];
		 for (int i = 0; i < nEigens; i++) {
			 eigenVectArr[i] = cvCreateImage(
					 faceImgSize, // size
					 IPL_DEPTH_32F, // depth
					 1); // channels
		 }
	
		 // allocate the eigenvalue array
		 eigenValMat = cvCreateMat(
				 1, // rows
				 nEigens, // cols
				 CV_32FC1); // type, 32-bit float, 1 channel
	
		 // allocate the averaged image
		 pAvgTrainImg = cvCreateImage(
				 faceImgSize, // size
				 IPL_DEPTH_32F, // depth
				 1); // channels
	
		 // set the PCA termination criterion
		 calcLimit = cvTermCriteria(
				 CV_TERMCRIT_ITER, // type
				 nEigens, // max_iter
				 1); // epsilon
	
		 logger.info("computing average image, eigenvalues and eigenvectors");
		 // compute average image, eigenvalues, and eigenvectors
		 cvCalcEigenObjects(
				 nTrainFaces, // nObjects
				 trainingFaceImgArr, // input
				 eigenVectArr, // output
				 CV_EIGOBJ_NO_CALLBACK, // ioFlags
				 0, // ioBufSize
				 null, // userData
				 calcLimit,
				 pAvgTrainImg, // avg
				 eigenValMat.data_fl()); // eigVals
	
		 logger.info("normalizing the eigenvectors");
		 cvNormalize(
				 eigenValMat, // src (CvArr)
				 eigenValMat, // dst (CvArr)
				 1, // a
				 0, // b
				 CV_L1, // norm_type
				 null); // mask
	 }
	
	 /**
	  * 保存数据到facedata.xml
	  */
	 public void storeTrainingData() {
		 logger.info("writing data/facedata.xml");
		 // create a file-storage interface
		 CvFileStorage fileStorage = cvOpenFileStorage(
				 resourcePath+"data/facedata.xml", // filename
				 null, // memstorage
	             CV_STORAGE_WRITE, // flags
	             null); // encoding
	
		 // Store the person names.
		 cvWriteInt(fileStorage, "nPersons", nPersons); 
	
		 for (int i = 0; i < nPersons; i++) {
			 String varname = "personName_" + (i + 1);
			 cvWriteString(fileStorage, varname, personNames.get(i), 0); 
		 }
	
		 // store all the data
		 cvWriteInt(fileStorage, "nEigens", nEigens);
	
		 cvWriteInt(fileStorage,"nTrainFaces", nTrainFaces); 
		 
		 cvWrite(fileStorage, "trainPersonNumMat", personNumTruthMat); 
	
		 cvWrite(fileStorage, "eigenValMat", eigenValMat); 
	
		 cvWrite(fileStorage, "projectedTrainFaceMat", projectedTrainFaceMat);
	
		 cvWrite(fileStorage, "avgTrainImg", pAvgTrainImg); 
	
		 for (int i = 0; i < nEigens; i++) {
			 String varname = "eigenVect_" + i;
			 cvWrite(fileStorage, varname, eigenVectArr[i]); 
		 }
	
		 // release the file-storage interface
		 cvReleaseFileStorage(fileStorage);
	 }
	
	 /**
	  * 
	  */
	 public void storeEigenfaceImages() {
		 // Store the average image to a file
		 logger.info("Saving the image of the average face as 'data/out_averageImage.bmp'");
		 cvSaveImage(resourcePath+"img/out_averageImage.jpg", pAvgTrainImg);
	
		 // Create a large image made of many eigenface images.
		 // Must also convert each eigenface image to a normal 8-bit UCHAR image instead of a 32-bit float image.
		 logger.info("Saving the " + nEigens + " eigenvector images as 'data/out_eigenfaces.bmp'");
	
		 if (nEigens > 0) {
			 // Put all the eigenfaces next to each other.
			 int COLUMNS = 8;        // Put upto 8 images on a row.
			 int nCols = Math.min(nEigens, COLUMNS);
			 int nRows = 1 + (nEigens / COLUMNS);        // Put the rest on new rows.
			 int w = eigenVectArr[0].width();
			 int h = eigenVectArr[0].height();
			 CvSize size = cvSize(nCols * w, nRows * h);
			 IplImage bigImg = cvCreateImage(
					 	size,
					 	IPL_DEPTH_8U, // depth, 8-bit Greyscale UCHAR image
					 	1);        // channels
			 for (int i = 0; i < nEigens; i++) {
				 // Get the eigenface image.
				 IplImage byteImg = convertFloatImageToUcharImage(eigenVectArr[i]);
				 // Paste it into the correct position.
				 int x = w * (i % COLUMNS);
				 int y = h * (i / COLUMNS);
				 CvRect ROI = cvRect(x, y, w, h);
				 cvSetImageROI(
						 bigImg, // image
						 ROI); // rect
				 cvCopy(
						 byteImg, // src
						 bigImg, // dst
						 null); // mask
				 cvResetImageROI(bigImg);
				 cvReleaseImage(byteImg);
			 }
			 cvSaveImage(
					 resourcePath+"img/out_eigenfaces.jpg", // filename
					 bigImg); // image
			 cvReleaseImage(bigImg);
		 }
	 }
	
	 /**
	  * 
	  * @param srcImg
	  * @return
	  */
	 public IplImage convertFloatImageToUcharImage(IplImage srcImg) {
		 IplImage dstImg;
		 if ((srcImg != null) && (srcImg.width() > 0 && srcImg.height() > 0)) {
			 // Spread the 32bit floating point pixels to fit within 8bit pixel range.
			 CvPoint minloc = new CvPoint();
		     CvPoint maxloc = new CvPoint();
		     double[] minVal = new double[1];
		     double[] maxVal = new double[1];
		     cvMinMaxLoc(srcImg, minVal, maxVal, minloc, maxloc, null);
		     // Deal with NaN and extreme values, since the DFT seems to give some NaN results.
		     if (minVal[0] < -1e30) {
		    	 minVal[0] = -1e30;
		     }
		     if (maxVal[0] > 1e30) {
		    	 maxVal[0] = 1e30;
		     }
		     if (maxVal[0] - minVal[0] == 0.0f) {
		    	 maxVal[0] = minVal[0] + 0.001;  // remove potential divide by zero errors.
		     }                        // Convert the format
		     dstImg = cvCreateImage(cvSize(srcImg.width(), srcImg.height()), 8, 1);
		     cvConvertScale(srcImg, dstImg, 255.0 / (maxVal[0] - minVal[0]), -minVal[0] * 255.0 / (maxVal[0] - minVal[0]));
		     return dstImg;
		 }
		 return null;
	 }
	
	 /**
	  * 
	  * @param floatPointer
	  * @return
	  */
	 public String floatPointerToString(FloatPointer floatPointer) {
		 StringBuilder stringBuilder = new StringBuilder();
		 boolean isFirst = true;
		 stringBuilder.append('[');
		 for (int i = 0; i < floatPointer.capacity(); i++) {
			 if (isFirst) {
				 isFirst = false;
			 } else {
				 stringBuilder.append(", ");
			 }
			 stringBuilder.append(floatPointer.get(i));
		 }
		 stringBuilder.append(']');
		 return stringBuilder.toString();
	 }
	
	 /**
	  * 
	  * @param cvMat
	  * @return
	  */
	 public String oneChannelCvMatToString(CvMat cvMat) {
		 //Preconditions
		 if (cvMat.channels() != 1) {
			 throw new RuntimeException("illegal argument - CvMat must have one channel");
		 }
	
		 int type = cvMat.type();
		 StringBuilder s = new StringBuilder("[ ");
		 for (int i = 0; i < cvMat.rows(); i++) {
			 for (int j = 0; j < cvMat.cols(); j++) {
				 if (type == CV_32FC1 || type == CV_32SC1) {
					 s.append(cvMat.get(i, j));
				 } else {
					 throw new RuntimeException("illegal argument - CvMat must have one channel and type of float or signed integer");
				 }
				 if (j < cvMat.cols() - 1) {
					 s.append(", ");
				 }
			 }
			 if (i < cvMat.rows() - 1) {
				 s.append("\n  ");
			 }
		 }
		 s.append(" ]");
		 return s.toString();
	 }
	 
	 /**
	  * 
	  * @return
	  */
	 public List<String> getList(){
		 return personNames;
	 }
	 /**
	  * 获取检测到的人脸的个数
	  * @return 人脸数
	  */
	 public int getFaceFramesCount(){
		 return faceFrameCount;
	 }
	 
}
