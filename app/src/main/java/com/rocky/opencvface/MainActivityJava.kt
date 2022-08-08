package com.rocky.opencvface

import android.Manifest
import android.hardware.Camera
import android.os.Bundle
import android.view.OrientationEventListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rocky.opencvface.databinding.ActivityMainBinding
import com.tbruyelle.rxpermissions3.RxPermissions
import org.opencv.android.*
import org.opencv.core.*

class MainActivityJava : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {


    private lateinit var binding: ActivityMainBinding
    private var mOpenCvCameraView: JavaCamera2View? = null
    private var mIntermediateMat: Mat? = null
    private var mRgba: Mat? = null
    private var orientationRoate = 1
    private var faceDetection: FaceDetectionJava? = null

    private fun getPermission() {
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe {
                if (it) {
                    faceDetection?.initClassifier()
                    mOpenCvCameraView?.setCameraPermissionGranted()
                } else {
                    Toast.makeText(this, "您还没有开启相机权限,请前往设置->应用管理->开启", Toast.LENGTH_SHORT).show();
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

    }

    private var cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT

    private fun initView() {
        mOpenCvCameraView = binding.cjv
        mOpenCvCameraView?.apply {
            setCameraIndex(cameraId)
            setCvCameraViewListener(this@MainActivityJava)
        }

        val orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                val defaultPortrait = 0
                val upsideDownPortrait = 180
                val rightLandscape = 90
                val leftLandscape = 270
                when {
                    isWithinOrientationRange(orientation, defaultPortrait) -> {
                        //竖屏
                        this@MainActivityJava.orientationRoate = 1
                    }
                    isWithinOrientationRange(orientation, leftLandscape) -> {
                        //竖屏左转90 此时是横屏 手机顶部在 左边
                        this@MainActivityJava.orientationRoate = 2
                    }
                    isWithinOrientationRange(orientation, upsideDownPortrait) -> {
                        //竖屏倒着
                        this@MainActivityJava.orientationRoate = 3

                    }
                    isWithinOrientationRange(orientation, rightLandscape) -> {
                        //竖屏右转90  此时是横屏 手机顶部在 右边
                        this@MainActivityJava.orientationRoate = 4

                    }
                }
            }

            private fun isWithinOrientationRange(
                currentOrientation: Int, targetOrientation: Int, epsilon: Int = 10,
            ): Boolean {
                return currentOrientation > targetOrientation - epsilon
                        && currentOrientation < targetOrientation + epsilon
            }
        }
        orientationEventListener.enable()

        faceDetection = FaceDetectionJava(this)
        faceDetection?.mLoaderCallbackInterfaceSUCCESSCallback = {
            getPermission()
            mOpenCvCameraView?.enableView()

            faceDetection?.trainingPattern()
        }


    }

    override fun onPause() {
        super.onPause()
        mOpenCvCameraView?.disableView()
    }

    override fun onResume() {
        super.onResume()
        faceDetection?.initOpenCv()

    }

    override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView?.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
        mIntermediateMat?.release()
        mIntermediateMat = null
        mRgba?.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        mRgba = inputFrame?.rgba() ?: Mat()
        when (cameraId) {
            android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT -> {
                //0 顺时针90  2  逆时针 90
                // 1 --> 2
                // 2 --> 0
                // 3 --> 2
                // 4 --> 0
                val roate = when (orientationRoate) {
                    1 -> 2
                    2 -> 0
                    3 -> 2
                    4 -> 0
                    else -> 1
                }
                Core.rotate(mRgba,
                    mRgba,
                    roate)

            }
            android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK -> {
                val roate = when (orientationRoate) {
                    1, 2, 3, 4 -> 0
                    else -> 1
                }
                Core.rotate(mRgba,
                    mRgba,
                    roate)
            }
            else -> {}
        }

        faceDetection?.recognizerFace( mRgba!!)

//        //相机拍照是反的  输转反过来
//        val rows = mRgba!!.rows()
//        val cols = mRgba!!.cols()
//        val haf = cols.shr(1)
//        for (row in 0 until rows) {
//            for (col in cols - 1 downTo haf) {
//                val temp = mRgba!!.at<UByte>(row, col).v4c
//                mRgba!!.at<UByte>(row, col).v4c = mRgba!!.at<UByte>(row, cols - 1 - col).v4c
//                mRgba!!.at<UByte>(row, cols - 1 - col).v4c = temp
//            }
//        }
        //镜像处理
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Core.flip(mRgba!!, mRgba!!, 1)
        }


        return mRgba!!
    }

}


