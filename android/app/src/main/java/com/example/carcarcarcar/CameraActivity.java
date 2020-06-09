package com.example.carcarcarcar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";

    // variables for android xml and intent
    Button cameraButton;
    Button flashButton;
    Boolean mFlashOn = false;
    LayoutInflater frameInflater = null;
    List<Integer> listXmlId = Arrays.asList( // list of xml
            R.layout.frontal1,
            R.layout.frontal2,
            R.layout.profile1,
            R.layout.profile2,
            R.layout.back1,
            R.layout.back2,
            R.layout.profile3,
            R.layout.profile4);
    ArrayList<String> permissions = new ArrayList<String>();
    int index = 0;
    private String userid, rentid, carid;
    private int state; // 0 : before, 1 : after


    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mCaptureState = STATE_PREVIEW;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private int mState;
    private TextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // TextureListener 에서 surfaceTexture를 사용가능한 경우, 카메라 오픈

            setupCamera(width, height);
            connectCamera();
            configureTransform(width, height);
//            openCamera();
//            configureTransform(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // surfaceTexture 파괴. false 반환시 release()를 호출함.
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        //카메라 상태 콜백
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            //Toast.makeText(getApplicationContext(), "Camera Opened", Toast.LENGTH_SHORT).show();
            mCameraDevice = camera;
            // preview 생성

            startPreview();
//            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String mCameraId;
    private Size mPreviewSize;
    private Size mImageSize;
    private ImageReader mImageReader;



    private CameraCaptureSession mPreviewCaptureSession;
    private CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void process(CaptureResult captureResult) {
            switch (mState) {
                case STATE_PREVIEW:
                    // Do nothing
                    break;
                case STATE_WAIT_LOCK:
                    mCaptureState = STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        //Toast.makeText(getApplicationContext(), "Focus Lock Success", Toast.LENGTH_SHORT).show();
                        startStillCaptureRequest(); // 촬영

                    }
                    break;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);

            Toast.makeText(getApplicationContext(), "Focus Lock UnSuccess", Toast.LENGTH_SHORT).show();

        }
    };

    private CaptureRequest.Builder mCaptureRequestBuilder;
    private File mImageFolder;
    private String mImageFileName;
    private Boolean flashAvailable;


    public String saveBitmapToPNG(Context context, byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        File storage = context.getCacheDir();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMAGE_" + timestamp + "_";
        String fileName = prepend + ".jpg";
        File tempFile = new File(storage, fileName);

        try {
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();
    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
        //camera session으로부터 데이터를 획득하면 해당 정보를 바이트정보로 가져온 후 파일로 저장
                @Override
                public void onImageAvailable(ImageReader reader) {
                    mBackgroundHandler.post(new ImageSender(reader.acquireNextImage()));

                }
            };
    private class ImageSender implements Runnable {

        private final Image mImage;

        public ImageSender(Image image){
            mImage = image;
        }

        public void run(){
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);

            String tempPath = saveBitmapToPNG(getApplicationContext(), bytes);
            Intent saveintent = new Intent(getApplicationContext(), SaveImageActivity.class);
            saveintent.setAction("load temp image");
            saveintent.putExtra("temppath", tempPath);
            saveintent.putExtra("index", index);
            saveintent.putExtra("state", state);
            mImage.close();
            startActivity(saveintent); // 사진
        }
    }



    private static class CompareSizeByArea implements Comparator<Size> {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() /
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        boolean back_flag = intent.getBooleanExtra("back", false);
        if (back_flag)
            mFlashOn = false;
        //String imagepath = intent.getStringExtra("temppath");

        index = intent.getIntExtra("index", 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        state = getIntent().getIntExtra("state", 0);

        // 화면 켜진 상태를 유지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        //createImageFolder();

        mTextureView = (TextureView) findViewById(R.id.textureView);


        cameraButton = (Button) findViewById(R.id.button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                lockFocus();
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(getApplicationContext(), "플래시 기능을 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();

        }

        flashButton = (Button) findViewById(R.id.flash);
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFlash();

            }
        });

        // layout 적용
        final View[] viewControl = new View[1];
        if (viewControl[0] != null)
            viewControl[0].setVisibility(View.GONE);
        frameInflater = LayoutInflater.from(getBaseContext());
        viewControl[0] = frameInflater.inflate(listXmlId.get(index), null);
        WindowManager.LayoutParams layoutParamsControl
                = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        addContentView(viewControl[0], layoutParamsControl);


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();

        if (mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();
//            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) { // skip frontal camera
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size largestImageSize = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new Comparator<Size>() {
                            @Override
                            public int compare(Size lhs, Size rhs) {
                                return Long.signum(lhs.getWidth() + lhs.getHeight() -
                                        rhs.getWidth() * rhs.getHeight());
                            }
                        }
                );
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), width, height);
                mImageReader = ImageReader.newInstance(largestImageSize.getWidth(),
                        largestImageSize.getHeight(),
                        ImageFormat.JPEG,
                        1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
                        mBackgroundHandler);//Images 호출
                //mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if(Surface.ROTATION_90 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation + 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // 카메라 오픈. StateCallback은 카메라가 제대로 연결되어있는지 확인. 카메라 프리뷰 생성
            cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            // openCamera() 에 넘겨주는 stateCallback 에서 카메라가 제대로 연결되었으면
            // startPreview() 메서드를 호출해서 카메라 미리보기를 만들어준다
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("Camera2 background thread");
        mBackgroundHandlerThread.start();
        //mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
        mBackgroundHandler = new Handler(Looper.getMainLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void lockFocus(){
        try{
            mState = STATE_WAIT_LOCK;
            mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, mFlashOn ? CaptureRequest.FLASH_MODE_TORCH : CaptureRequest.FLASH_MODE_OFF);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
            mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(),
                    mPreviewCaptureCallback, mBackgroundHandler);
        } catch(CameraAccessException e){
            e.printStackTrace();
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPreview(){

        // 캡쳐세션을 만들기 전에 프리뷰를 위한 Surface 를 준비한다
        // 레이아웃에 선언된 textureView 로부터 surfaceTexture 를 얻을 수 있다
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);   // 미리보기를 시작하기 위해 필요한 출력표면인 surface
        try {
            // 미리보기 화면을 요청하는 RequestBuilder 를 만들어준다.
            // 이 요청은 위에서 만든 surface 를 타겟으로 한다

            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            //onConfigured 상태가 확인되면 CameraCaptureSession을 통해 미리보기를 보여줌.
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),//surface로 이루어진 list
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            Log.d(TAG, "onConfigured: startPreview");

                            // session 이 준비되면 미리보기를 화면에 보여줌줌
                            mPreviewCaptureSession = session;
                            try {
                                mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                        null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.d(TAG, "onConfigureFailed: startPreview");
                        }
                    }, null);

//            CaptureRequest.Builder captureStillBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            captureStillBuilder.addTarget(mImageReader.getSurface());
//
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
//                    ORIENTATIONS.get(rotation));
//
//            CameraCaptureSession.CaptureCallback captureCallback =
//                    new CameraCaptureSession.CaptureCallback() {
//                        @Override
//                        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
//                            super.onCaptureStarted(session, request, timestamp, frameNumber);
//
////                            try {
////
////                                createImageFileName();
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
//                        }
//                        @Override
//                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
//                            super.onCaptureCompleted(session, request, result);
//
//
//                        }
//
//                    };
//            mCameraCaptureSession.capture(
//                    captureStillBuilder.build(), captureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startStillCaptureRequest(){
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
        mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

        CameraCaptureSession.CaptureCallback stillCaptureCallback = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);

            }
        };
        try {
            mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
//        if (mCameraCaptureSession != null) {
//            mCameraCaptureSession.close();
//            mCameraCaptureSession = null;
//        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
//        if(mImageReader != null){
//            mImageReader.close();
//            mImageReader = null;
//        }
    }




    // 내 texture view의 비율에 맞춰주기
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Size getPreferredPreviewSize(Size[] choices, int width, int height) {
        List<Size> collectorSizes = new ArrayList<Size>();
        for (Size option : choices) {
            if(option.getHeight() == option.getWidth() * height / width)
                if (width > height) {
                    if (option.getWidth() > width && option.getHeight() > height) {
                        collectorSizes.add(option);
                    }
                } else {
                    if (option.getWidth() > height && option.getHeight() > width) {
                        collectorSizes.add(option);
                    }
                }
        }
        if (collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }
    private File createImageFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMAGE_" + timestamp + "_";
        File imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
        mImageFileName = imageFile.getAbsolutePath();
        return imageFile;
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void openCamera() {
//        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try { // 권한 허용 확인
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//
//                return;
//            }
////            for(String id : cameraManager.getCameraIdList()) {
////                CameraCharacteristics c = cameraManager.getCameraCharacteristics(id);
////                flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
////                Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
////                if (flashAvailable != null && flashAvailable && lensFacing != null
////                        && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
////                    mCameraId = id;
////                    break;
////                }
////            }
//            // 카메라 오픈. StateCallback은 카메라가 제대로 연결되어있는지 확인. 카메라 프리뷰 생성
//            cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
//        } catch (CameraAccessException e){
//            e.printStackTrace();
//        }
//    }
    // openCamera() 에 넘겨주는 stateCallback 에서 카메라가 제대로 연결되었으면
    // createCameraPreviewSession() 메서드를 호출해서 카메라 미리보기를 만들어준다
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void createCameraPreviewSession(){
//        try{
//            // 캡쳐세션을 만들기 전에 프리뷰를 위한 Surface 를 준비한다
//            // 레이아웃에 선언된 textureView 로부터 surfaceTexture 를 얻을 수 있다
//            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
//            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//            Surface previewSurface = new Surface(surfaceTexture); // 미리보기를 시작하기 위해 필요한 출력표면인 surface
//            // 미리보기 화면을 요청하는 RequestBuilder 를 만들어준다.
//            // 이 요청은 위에서 만든 surface 를 타겟으로 한다
//            mPreviewCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mPreviewCaptureRequestBuilder.addTarget(previewSurface);
//
//            //onConfigured 상태가 확인되면 CameraCaptureSession을 통해 미리보기를 보여줌.
//            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),//surface로 이루어진 list
//                    new CameraCaptureSession.StateCallback() {
//                        @Override
//                        public void onConfigured(@NonNull CameraCaptureSession session) {
//                            if(mCameraDevice == null){
//                                return;
//                            }
//                            try{
//                                mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
//                                // session 이 준비되면 미리보기를 화면에 보여줌
//                                mCameraCaptureSession = session;
//                                mCameraCaptureSession.setRepeatingRequest(
//                                        mPreviewCaptureRequest,
//                                        null,
//                                        mBackgroundHandler);
//                            } catch (CameraAccessException e){
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                            Toast.makeText(
//                                    getApplicationContext(),
//                                    "create camera session failed",
//                                    Toast.LENGTH_SHORT
//                            ).show();
//                        }
//                    }, null);
//
//
//
//
//        }catch (CameraAccessException e){
//            e.printStackTrace();
//        }
//    }

    private void switchFlash(){

        try{
            if(mFlashOn){
                Toast.makeText(getApplicationContext(), "flash off", Toast.LENGTH_SHORT).show();
                mFlashOn = false;
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureResult.FLASH_MODE_OFF);
                mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);

            }
            else{
                Toast.makeText(getApplicationContext(), "flash on", Toast.LENGTH_SHORT).show();
                mFlashOn = true;
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);

            }
        } catch(CameraAccessException e){
            e.printStackTrace();
        }

    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices) {
            if(option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if(bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }










}

