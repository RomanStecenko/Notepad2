package ua.stetsenko.Notepad2.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.R;

import java.io.FileNotFoundException;

public class FullScreenActivity extends Activity implements View.OnTouchListener {
    private int screenHeight, screenWidth;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private int mode = Constants.STATE_NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screnn_activity);
        ImageView imageView = (ImageView) findViewById(R.id.fullScreenImageView);
        Uri fileUri = getIntent().getParcelableExtra(Constants.IMG_URI);
        try {
            imageView.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
        }
        imageView.setOnTouchListener(this);
        getScreenSize();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(Constants.LOG, "mode=STATE_DRAG");
                mode = Constants.STATE_DRAG;
                break;
            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = Constants.STATE_NONE;
                Log.d(Constants.LOG, "mode=STATE_NONE");
                Log.d(Constants.LOG, "screenH=" + screenHeight + "screenW=" + screenWidth);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //second finger down
                oldDist = spacing(event); // calculates the distance between two points where user touched.
                Log.d(Constants.LOG, "oldDist=" + oldDist);
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
                    mode = Constants.STATE_ZOOM;
                    Log.d(Constants.LOG, "mode=STATE_ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == Constants.STATE_DRAG) { //movement of first finger

                    Rect bounds = view.getDrawable().getBounds();
                    int width = bounds.right - bounds.left;
                    int height = bounds.bottom - bounds.top;

                    Log.d(Constants.LOG, "bounds.right=" + bounds.right + ", bounds.left=" + bounds.left + ", width=" + width);
                    Log.d(Constants.LOG, "bounds.bottom=" + bounds.bottom + ", bounds.top=" + bounds.top + ", height=" + height);

                    float dX = event.getX() - start.x;
                    float dY = event.getY() - start.y;
                    Log.d(Constants.LOG, "dX=" + dX + ", dY=" + dY);
//                    if ((dX < 0 || dX > screenWidth) || (dY < 0 || dY > screenHeight))
//                        break;
                    matrix.set(savedMatrix);
                    Log.d(Constants.LOG, "INTERESTING: view.getLeft()=" + view.getLeft() + ", event.getX()=" + event.getX() + ", event.getY()=" + event.getY() + ",  start.x=" + start.x + ", start.y=" + start.y);
                    matrix.postTranslate(dX, dY);

                } else if (mode == Constants.STATE_ZOOM) { //pinch zooming
                    float newDist = spacing(event);
//                    Log.d(Constants.LOG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; //thinking I need to play around with this value to limit it**
                        Log.d(Constants.LOG, "scale=" + scale);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void getScreenSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    private Bitmap decodeFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        final int longest = options.outHeight > options.outWidth ? options.outHeight : options.outWidth;
        int required = screenHeight > screenWidth ? screenHeight : screenWidth;
        int inSampleSize = 1;
        if (longest > required) {
            while ((longest / inSampleSize) > required) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
