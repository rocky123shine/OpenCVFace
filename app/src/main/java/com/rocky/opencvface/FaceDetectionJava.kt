package com.rocky.opencvface

import android.content.Context
import android.util.Log
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.ml.EM
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * <pre>
 *     author : rocky
 *     time   : 2022/07/29
 * </pre>
 */
class FaceDetectionJava(val context: Context) {
    private val TAG = "FaceDetectionJava"
    var classifier: CascadeClassifier? = null
    var mLoaderCallbackInterfaceSUCCESSCallback: (() -> Unit)? = null
    private var mLoaderCallback: BaseLoaderCallback? = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    mLoaderCallbackInterfaceSUCCESSCallback?.invoke()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }

        }
    }

    // 初始化人脸级联分类器，必须先初始化
    fun initClassifier() {
        try {
            val inputStream = context.resources.openRawResource(R.raw.lbpcascade_frontalface)
            val cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE)
            val cascadeFile = File(cascadeDir, "lbpcascade_frontalface.xml")
            val fileOutputStream = FileOutputStream(cascadeFile)
            val buffer = ByteArray(4096)
            var bytesRead = 0
            while (inputStream.read(buffer).also {
                    bytesRead = it
                } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            fileOutputStream.close()
            classifier = CascadeClassifier(cascadeFile.absolutePath)

        } catch (e: Exception) {
            e.printStackTrace();
        }

    }

    fun recognizerFace(mRgba: Mat): Mat {

        val mRelativeFaceSize = 0.25
        val height = mRgba.rows()
        val width = mRgba.cols()
        val faces = MatOfRect()
        classifier?.detectMultiScale(
            mRgba,
            faces,
            1.1,
            3,
            0,
            Size(width * mRelativeFaceSize / 2, height * mRelativeFaceSize / 2),
            Size(width * mRelativeFaceSize * 2, height * mRelativeFaceSize * 2)
        )

        val facesArray = faces.toArray()
        val faceRectColor = Scalar(255.0, 0.0, 255.0, 255.0)//rgba

        for (faceRect in facesArray) {
            Imgproc.rectangle(mRgba, faceRect.tl(), faceRect.br(), faceRectColor, 10)
        }
        return mRgba
    }

    fun initOpenCv() {
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, mLoaderCallback)
        } else {
            mLoaderCallback?.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }

    }

    // 训练样本，这一步是在数据采集做的
    fun trainingPattern() {
        val faces: Vector<Mat> = Vector()//对应训练的样本
        val labels: Vector<Int> = Vector()//对应样本的id
        for (i in 1..5) {

            for (j in 1..5) {

                val face =
                    Imgcodecs.imread(String.format("/storage/emulated/0/s%d/%d.pgm", i, j), 0)
                if (face.empty()) {
                    Log.d(TAG, "face mat is empty")
                    continue
                }
                // 确保大小一致
                Imgproc.resize(face, face, Size(128.0, 128.0))
                faces.add(face)
                labels.add(i)
            }


        }
        for (i in 1..8) {
            val face =
                Imgcodecs.imread(String.format("/storage/emulated/0/face_%d.png", i), 0)
            if (face.empty()) {
                Log.d(TAG, "face mat is empty")
                continue
            }
            // 确保大小一致
            Imgproc.resize(face, face, Size(128.0, 128.0))
            faces.add(face)
            labels.add(11)
        }


    }
}