#include <jni.h>
#include <string>

extern "C"
JNIEXPORT void JNICALL
Java_com_rocky_opencvface_FaceDetection_loadCascade(JNIEnv *env, jobject thiz, jstring file_path) {

}
extern "C"
JNIEXPORT void JNICALL
Java_com_rocky_opencvface_FaceDetection_faceDetection(JNIEnv *env, jobject thiz, jlong native_obj) {

}
extern "C"
JNIEXPORT void JNICALL
Java_com_rocky_opencvface_FaceDetection_trainingPattern(JNIEnv *env, jobject thiz) {

}
extern "C"
JNIEXPORT void JNICALL
Java_com_rocky_opencvface_FaceDetection_loadPattern(JNIEnv *env, jobject thiz,
                                                    jstring pattern_path) {

}