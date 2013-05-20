package com.paad.ad2.surfacecamera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.net.Uri;
import com.paad.ad2.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResultPicture {
    private final Context context;
    private static final int TOP_IMAGE = R.drawable.top_snow;
    private static final int BOTTOM_IMAGE = R.drawable.bottom_snow;
    private int targetWidth;
    private int targetHeight;
    private int rotateAngle = -1;
    private final Uri imageUri;

    public ResultPicture(Context context, Uri imageUri) throws RenderException {
        this.context = context;
        this.imageUri = imageUri;

        try {
            initOriginalDimensions();
        } catch (IOException e) {
            throw new RenderException(e);
        }
    }

    private void initOriginalDimensions() throws IOException {
        InputStream input = context.getContentResolver().openInputStream(imageUri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        this.targetWidth = onlyBoundsOptions.outWidth; // 640
        this.targetHeight = onlyBoundsOptions.outHeight; // 480
    }

    public Bitmap render() throws RenderException {
        try {
            return draw();
        } catch (FileNotFoundException e) {
            throw new RenderException(e);
        }
    }

    private Bitmap draw() throws FileNotFoundException {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Bitmap photo = getPhoto();
        Bitmap topSnow = getTopImage();
        Bitmap bottomSnow = getBottomImage();

        Bitmap overlay = Bitmap.createBitmap(targetWidth, targetHeight, photo.getConfig());

        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(photo, 0, 0, paint);
        canvas.drawBitmap(topSnow, 0, 0, paint);
        canvas.drawBitmap(bottomSnow, 0, (targetHeight - bottomSnow.getHeight()), paint);

        return overlay;
    }

    private Bitmap getPhoto() throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();

        InputStream input = context.getContentResolver().openInputStream(imageUri);
        Bitmap photo =  BitmapFactory.decodeStream(input, null, options);

        if(rotateAngle == -1){
            return photo;
        }else {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateAngle);
            Bitmap rotatedPhoto = Bitmap.createBitmap(photo, 0, 0, targetWidth, targetHeight, matrix, true);
            return rotatedPhoto;
        }
    }

    private Bitmap getTopImage() {
        return getImage(TOP_IMAGE);
    }

    private Bitmap getBottomImage() {
        return getImage(BOTTOM_IMAGE);
    }

    private Bitmap getImage(int resourceID) {
        Resources resources = context.getResources();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resourceID, options);
        int originalHeight = options.outHeight;

        options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(resources,resourceID, options);
        return Bitmap.createScaledBitmap(bitmap, targetWidth, originalHeight, true);
    }

    public void setRotateAngle(int rotateAngle) {
        this.rotateAngle = rotateAngle;
    }

    public class RenderException extends Exception {
        public RenderException(Exception ex) {
            super(ex);
        }
    }
}
