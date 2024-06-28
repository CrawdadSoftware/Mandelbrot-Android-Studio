package com.example.mandelbrotfractal;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MandelbrotView extends GLSurfaceView {

    private MandelbrotRenderer renderer;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    public MandelbrotView(Context context) {
        super(context);
        init(context);
    }

    public MandelbrotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);

        renderer = new MandelbrotRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void zoomIn() {
        renderer.zoomIn();
        requestRender();
    }

    public void zoomOut() {
        renderer.zoomOut();
        requestRender();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (scaleFactor > 1.0f) {
                renderer.zoomOut();
            } else {
                renderer.zoomIn();
            }
            requestRender();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            renderer.pan(distanceX / getWidth(), -distanceY / getHeight());
            requestRender();
            return true;
        }
    }
}
