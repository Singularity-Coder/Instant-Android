
// importing libraries
#include <jni.h>
#include <string>

// Method convention: Java_PackageName_ActivityName_MethodName
extern "C" JNIEXPORT jstring JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
//    __android_log_write(ANDROID_LOG_DEBUG, "API123", "Debug Log");
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL    // jstring is the return type
Java_com_singularitycoder_javanativeinterface1_MainActivity_sendYourName(JNIEnv *env, jobject thiz, jstring firstName, jstring lastName) {
    char returnString[20];
    const char *fN = env->GetStringUTFChars(firstName, NULL);
    const char *lN = env->GetStringUTFChars(lastName, NULL);

    strcpy(returnString, fN); // copy string one into the result.
    strcat(returnString, lN); // append string two to the result.

    env->ReleaseStringUTFChars(firstName, fN);
    env->ReleaseStringUTFChars(lastName, lN);

//    __android_log_write(ANDROID_LOG_DEBUG, "API123", returnString);

    return env->NewStringUTF(returnString);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_stringArrayFromJNI(JNIEnv *env, jobject thiz) {
    char *days[] = {"Java",
                    "Android",
                    "Django",
                    "SQL",
                    "Swift",
                    "Kotlin",
                    "Springs"};

    jstring str;
    jobjectArray day = 0;
    jsize len = 7;
    int i;

    day = env->NewObjectArray(len, env->FindClass("java/lang/String"), 0);

    for (i = 0; i < 7; i++) {
        str = env->NewStringUTF(days[i]);
        env->SetObjectArrayElement(day, i, str);
    }

    // To convert from JNI strings to a native char array, you can use the GetStringUTFChars method of the env. But it needs to be released after use
    return day;
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_anyArrayFromJNI(JNIEnv *env, jobject thiz) {
    bool myBool = true;

    char myChar = 'A';
    char16_t myChar16 = 'B';

    int myInt = 1;
    int8_t myInt8 = 2;
    int_fast8_t myIntFast8 = 3;

    long myLong = 1000000;
    u_long myUlong = 200000;

    short myShort = 5367;
    u_short myUshort = 2948;

    float myFloat = 293.20;
    float_t myFloatT = 6373.33;

    double myDouble = 393.393;
    double_t myDoubleT = 653736.38;

    // byte
    // string

    //-------------------------------

    jbyte myjByte = 93;
    jshort myjShort = 44;
    jint myjInt = 628393;
    jlong myjLong = 45678;
    jfloat myjFloat = 345678.00;
    jdouble myjDouble = 34567.32;
    jboolean myjBoolean = true;
    jchar myjChar = 'A';
//    jstring myjString = "asdfghj";

}

//Addition function
extern "C" JNIEXPORT jint JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_add( JNIEnv *env, jobject, jint x, jint y) {

    //return an integer
    return x + y;
}

//Subtraction function
extern "C" JNIEXPORT jint JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_sub( JNIEnv *env, jobject, jint x, jint y) {

    //return an integer
    return x - y;
}

//Multiplication function
extern "C" JNIEXPORT jint JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_multiply( JNIEnv *env, jobject, jint x, jint y) {

    //return an integer
    return x * y;
}

//Division function
extern "C" JNIEXPORT jint JNICALL
Java_com_singularitycoder_javanativeinterface1_MainActivity_divide( JNIEnv *env, jobject, jint x, jint y) {

    //return an integer
    return x / y;
}

void func() {
    // Void function
}