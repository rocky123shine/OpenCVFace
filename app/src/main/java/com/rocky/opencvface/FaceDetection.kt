package com.rocky.opencvface

import android.content.Context
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream

/**
 * <pre>
 *     author : rocky
 *     time   : 2022/07/29
 * </pre>
 */
class FaceDetection() {


    init {
        System.loadLibrary("opencvface")
    }


    /**
     * 检测人脸并保存人脸信息
     *
     * @param mat 当前帧
     */
    fun faceDetection(mat: Mat) {
        faceDetection(mat.nativeObj)
    }
    /**
     * 加载人脸识别的分类器文件
     *
     * @param filePath
     */
    external fun loadCascade(filePath: String?)

    private external fun faceDetection(nativeObj: Long)

    external fun trainingPattern()

    // 加载样本数据
    external fun loadPattern(patternPath: String?)
}