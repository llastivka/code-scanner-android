package com.scanner.rmcode.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scanner.rmcode.MainActivity;
import com.scanner.rmcode.R;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback, BaseFragment {

    private static final Logger logger = Logger.getLogger(CameraFragment.class.getName());

    private Context mContext;
    private Activity mFragmentActivity;

    Camera camera;
    SurfaceView cameraView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback jpegCallback;
    View cameraFragmentView;

    Drawable background;
    Drawable buttonDrawable;
    ColorStateList accentColor;

    private final int CAMERA_REQUEST_CODE = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, container, false);
        cameraFragmentView = view;
        setTheme();

        final ProgressBar progressBar = view.findViewById(R.id.camera_progress_bar);

        cameraView = view.findViewById(R.id.camera_surface_view);
        surfaceHolder = cameraView.getHolder();

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mFragmentActivity, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        ImageButton mCapture = view.findViewById(R.id.capture_button);
        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                camera.takePicture(null, null, jpegCallback);

            }
        });

        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) mFragmentActivity).setImageBytes(bytes);
                ((MainActivity) mFragmentActivity).changeFragments(new CaptureFragment(), getString(R.string.code_scanner));
            }
        };

        return view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size bestSize;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width  * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }
        int bestHeight = bestSize.height * cameraView.getWidth() / cameraView.getHeight();
        int bestWidth = bestSize.height;
        parameters.setPreviewSize(bestWidth, bestHeight);

        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    surfaceHolder.addCallback(this);
                    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                } else {
                    Toast.makeText(getContext(), "Please provide the necessary permissions", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setThemeDetails(Drawable back, Drawable buttonBack, ColorStateList accent) {
        background = back;
        buttonDrawable = buttonBack;
        accentColor = accent;

        if (cameraFragmentView != null) {
            setTheme();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTheme() {
        cameraFragmentView.setBackground(background);
        ImageButton captureButton = cameraFragmentView.findViewById(R.id.capture_button);
        captureButton.setBackground(buttonDrawable);
        captureButton.setImageTintList(accentColor);
    }
}
