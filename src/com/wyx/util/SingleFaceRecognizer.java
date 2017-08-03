package com.wyx.util;

import com.wyx.face.FaceRecognizer;

public class SingleFaceRecognizer {
	private static FaceRecognizer faceRecognizer = new FaceRecognizer();
	private SingleFaceRecognizer(){};

	public static FaceRecognizer getInstance(){
		return faceRecognizer;
	}
}
