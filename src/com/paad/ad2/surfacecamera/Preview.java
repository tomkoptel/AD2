package com.paad.ad2.surfacecamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.paad.ad2.R;

import java.io.IOException;

public class Preview extends RelativeLayout implements SurfaceHolder.Callback, Camera.PictureCallback {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private FrameLayout mFrameLayout;
    private ImageView previewContainer;

    public Preview(Context context) {
        super(context);
        initSurface(context);
    }

    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurface(context);
    }

    public Preview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSurface(context);
    }

    private void initSurface(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.surface, this, false);

        mFrameLayout = (FrameLayout) layout.findViewById(R.id.frameLayout);
        previewContainer = (ImageView) layout.findViewById(R.id.previewContainer);

        mSurfaceView = (SurfaceView) layout.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        addView(layout);
    }

    public void takePicture() {
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap correctedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        Drawable drawable = new BitmapDrawable(getResources(), correctedBmp);
        previewContainer.setBackgroundDrawable(drawable);

        togglePreviewContainer(true);
    }

    public void resetPreview() {
        togglePreviewContainer(false);
        startPreview();
    }

    private void togglePreviewContainer(boolean show) {
        if(show) {
            mSurfaceView.setVisibility(View.GONE);
            previewContainer.setVisibility(View.VISIBLE);
        } else {
            mSurfaceView.setVisibility(View.VISIBLE);
            previewContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview();
    }

    private void startPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);

            requestLayout();

            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCamera.startPreview();
        }
    }

    public void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void makeScreenShot() {
        if(mSurfaceView.getVisibility() == View.GONE) {
            mFrameLayout.setDrawingCacheEnabled(true);
            mFrameLayout.buildDrawingCache();
            Bitmap bm = mFrameLayout.getDrawingCache();

            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bm, "", "");

            mFrameLayout.setDrawingCacheEnabled(false);
        }
    }
}
