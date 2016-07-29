package pe.com.orbis.runtimepermissions_sample.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import pe.com.orbis.runtimepermissions_sample.R;
import pe.com.orbis.runtimepermissions_sample.util.CameraParameter;
import pe.com.orbis.runtimepermissions_sample.util.Constant;
import pe.com.orbis.runtimepermissions_sample.util.RuntimePermissionUtils;

import static android.Manifest.permission.CAMERA;

/**
 * Created by carlos on 15/06/16.
 * Alias: CarlitosDroid
 */
public class CameraFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback{

    private static final String TAG = CameraFragment.class.getSimpleName();
    private FrameLayout fmlShowCamera;
    private FloatingActionButton picture;
    private LinearLayout lnlNotPermission;
    private AppCompatButton btnActivatePermission;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final String STORAGE_PERMISSION_DIALOG_OPEN_KEY = "already_open_dialog_permission";

    private static final String[] PERMS_ALL={
            CAMERA
    };

    SurfaceView surfaceView;

    private SurfaceHolder previewHolder=null;
    private Camera camera=null;
    private boolean inPreview=false;

    private Camera.Parameters parameters;
    private static Camera.Size pictureSize,previewSize;

    // true if dialog already open
    private boolean alreadyAskedForStoragePermission = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            alreadyAskedForStoragePermission = savedInstanceState.getBoolean(STORAGE_PERMISSION_DIALOG_OPEN_KEY, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STORAGE_PERMISSION_DIALOG_OPEN_KEY, alreadyAskedForStoragePermission);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        surfaceView = (SurfaceView) view.findViewById(R.id.cameraPreview);
        picture = (FloatingActionButton) view.findViewById(R.id.picture);
        lnlNotPermission = (LinearLayout) view.findViewById(R.id.lnlNotPermission);
        fmlShowCamera = (FrameLayout) view.findViewById(R.id.fmlShowCamera);
        btnActivatePermission = (AppCompatButton) view.findViewById(R.id.btnActivatePermission);

        picture.setOnClickListener(this);
        fmlShowCamera.setOnClickListener(this);
        btnActivatePermission.setOnClickListener(this);

        showCamera();

        return view;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // do something for phones running an SDK before lollipop
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void initPreview() {

        if (camera!=null && previewHolder.getSurface()!=null) {
            try {
                setCameraDisplayOrientation(getActivity(), currentCameraId, camera);
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("PreviewD-surfaceCallba",
                        "Except in setPreviewDisplay()", t);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

            if(inPreview){
                camera.stopPreview();
                parameters=camera.getParameters();
                List<String> focusModes = parameters.getSupportedFocusModes();
                if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else
                if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }

                previewSize =  CameraParameter.getInstance().getPictureSize(
                        parameters.getSupportedPreviewSizes(), 400);

                pictureSize =  CameraParameter.getInstance().getPictureSize(
                        parameters.getSupportedPictureSizes(), 600);

                if(previewSize!=null)
                    parameters.setPreviewSize(previewSize.width,previewSize.height);
                if(pictureSize!=null)
                    parameters.setPictureSize(pictureSize.width,pictureSize.height);
                camera.setParameters(parameters);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initPreview();
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void startPreview() {
        if (camera!=null) {
            camera.startPreview();
            inPreview=true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (camera == null && RuntimePermissionUtils.hasPermission(getActivity(), CAMERA)) {
            showCamera();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(camera!=null){
            camera.release();
            camera=null;
            inPreview=false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == Constant.RESULT_PERMS_ALL){
            // the request returned a result so the dialog is closed
            alreadyAskedForStoragePermission = false;
            if(RuntimePermissionUtils.hasPermission(getActivity(), CAMERA)){
                showCamera();
            }else {
                showNotPermission();
            }
        }
    }

    public void showCamera(){
        if(camera==null && RuntimePermissionUtils.hasPermission(getActivity(), CAMERA)){
            lnlNotPermission.setVisibility(View.GONE);
            fmlShowCamera.setVisibility(View.VISIBLE);
            previewHolder = surfaceView.getHolder();
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            previewHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            camera=Camera.open(currentCameraId);
            startPreview();
        }
    }

    public void showNotPermission(){
        lnlNotPermission.setVisibility(View.VISIBLE);
        fmlShowCamera.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.picture:
                Toast.makeText(getActivity(), "TAKE PICTURE", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnActivatePermission:
                boolean showRationale = shouldShowRequestPermissionRationale(CAMERA);
                if(!showRationale){//User check "NEVER ASK AGAIN" in permission dialog
                    DetailsSettingsDialogFragment detailsSettingsDialogFragment = DetailsSettingsDialogFragment.newInstance(
                            getActivity().getString(R.string.profile_camera_permission));
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(detailsSettingsDialogFragment, "layout_filter_checkbox_dialog");
                    detailsSettingsDialogFragment.setTargetFragment(this, 123);//communication between fragment and dialgofragment
                    fragmentTransaction.commit();
                }else{
                    tryGetPermission();
                }
                break;
            default:
                break;
        }
    }

    public void tryGetPermission(){
        if (!RuntimePermissionUtils.hasPermission(getActivity(), CAMERA)
                && RuntimePermissionUtils.useRuntimePermissions()) {
            if(!alreadyAskedForStoragePermission){
                // don't check again because the dialog is still open
                alreadyAskedForStoragePermission = true;
                requestPermissions(PERMS_ALL, Constant.RESULT_PERMS_ALL);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.REQUEST_PERMISSION_SETTING){
            if(RuntimePermissionUtils.hasPermission(getActivity(), CAMERA)){
                showCamera();
            }
        }
    }
}
