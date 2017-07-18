package com.multitv.yuv.animations;

import android.graphics.Camera;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by mkr on 11/17/2016.
 */

public class HorizontalFlipAnimation extends Animation {
    public static final int HORIZONTAL_FLIP_OPEN = 1001;
    public static final int HORIZONTAL_FLIP_CLOSE = 1002;
    private Camera camera;
    private int center;
    private int type;

    public HorizontalFlipAnimation(int type) {
        this.type = type;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        center = width;
        camera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        android.graphics.Matrix matrix = transformation.getMatrix();
        camera.save();
        float degrees = 0;
//        switch (type) {
//            case HORIZONTAL_FLIP_OPEN:
//                degrees = -90 + ((float) (90 * interpolatedTime));
//                break;
//            case HORIZONTAL_FLIP_CLOSE:
//                degrees = -((float) (90 * interpolatedTime));
//                break;
//            default:
//                degrees = -90 + ((float) (90 * interpolatedTime));
//        }
        switch (type) {
            case HORIZONTAL_FLIP_OPEN:
                degrees = -90 + ((float) (90 * interpolatedTime));
                break;
            case HORIZONTAL_FLIP_CLOSE:
                degrees = -((float) (90 * interpolatedTime));
                break;
            default:
                degrees = -90 + ((float) (90 * interpolatedTime));
        }
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-center, 0);
        matrix.postTranslate(center, 0);
    }
}
