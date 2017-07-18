package com.multitv.yuv.animations;

import android.graphics.Camera;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by mkr on 11/17/2016.
 */

public class VerticallyFlipAnimation extends Animation {
    public static final int VERTICAL_FLIP_OPEN = 1001;
    public static final int VERTICAL_FLIP_CLOSE_BOTTOM = 1002;
    public static final int VERTICAL_FLIP_CLOSE_TOP = 1003;
    private Camera camera;
    private int center;
    private int type;

    public VerticallyFlipAnimation(int type) {
        this.type = type;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
//        center = (int) (height / 2);
        switch (type) {
            case VERTICAL_FLIP_OPEN:
            case VERTICAL_FLIP_CLOSE_BOTTOM:
                center = 0;
                break;
            case VERTICAL_FLIP_CLOSE_TOP:
                center = height;
                break;
            default:
                center = height / 2;
        }
        camera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        android.graphics.Matrix matrix = transformation.getMatrix();
        camera.save();
        float degrees = 0;
        switch (type) {
            case VERTICAL_FLIP_OPEN:
                degrees = -90 + ((float) (90 * interpolatedTime));
                break;
            case VERTICAL_FLIP_CLOSE_BOTTOM:
                degrees = -((float) (90 * interpolatedTime));
                break;
            case VERTICAL_FLIP_CLOSE_TOP:
                degrees = ((float) (90 * interpolatedTime));
                break;
            default:
                degrees = -90 + ((float) (90 * interpolatedTime));

        }
        camera.rotateX(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(0, -center);
        matrix.postTranslate(0, center);
    }
}
