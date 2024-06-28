package com.example.mandelbrotfractal;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MandelbrotRenderer implements GLSurfaceView.Renderer {

    private float zoomFactor = 1.0f;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private int width;
    private int height;

    private int program;
    private int positionHandle;
    private int resolutionHandle;
    private int zoomHandle;
    private int offsetHandle;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec2 resolution;" +
                    "uniform float zoom;" +
                    "uniform vec2 offset;" +
                    "void main() {" +
                    "  vec2 coord = (gl_FragCoord.xy / resolution - 0.5) * zoom + offset;" +
                    "  vec2 z = coord;" +
                    "  float n = 0.0;" +
                    "  float maxIter = 1000.0;" +
                    "  for (int i = 0; i < 1000; i++) {" +
                    "    float x = (z.x * z.x - z.y * z.y) + coord.x;" +
                    "    float y = (z.y * z.x + z.x * z.y) + coord.y;" +
                    "    if ((x * x + y * y) > 4.0) break;" +
                    "    z.x = x;" +
                    "    z.y = y;" +
                    "    n += 1.0;" +
                    "  }" +
                    "  float color = n / maxIter;" +
                    "  vec3 rgb;" +
                    "  if (n == maxIter) {" +
                    "    rgb = vec3(0.0, 0.0, 0.0);" +
                    "  } else {" +
                    "    float t = mod(color * 20.0, 1.0);" +
                    "    rgb = vec3(0.5 + 0.5 * cos(6.28 * t), 0.5 + 0.5 * sin(6.28 * t), 0.5 - 0.5 * cos(6.28 * t));" +
                    "  }" +
                    "  gl_FragColor = vec4(rgb, 1.0);" +
                    "}";

    private float[] vertices = {
            -1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f,  1.0f, 0.0f
    };

    private short[] indices = { 0, 1, 2, 0, 2, 3 };

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        resolutionHandle = GLES20.glGetUniformLocation(program, "resolution");
        GLES20.glUniform2f(resolutionHandle, width, height);

        zoomHandle = GLES20.glGetUniformLocation(program, "zoom");
        GLES20.glUniform1f(zoomHandle, zoomFactor);

        offsetHandle = GLES20.glGetUniformLocation(program, "offset");
        GLES20.glUniform2f(offsetHandle, offsetX, offsetY);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    public void zoomIn() {
        zoomFactor *= 1.1f;
    }

    public void zoomOut() {
        zoomFactor /= 1.1f;
    }

    public void pan(float dx, float dy) {
        offsetX += dx * zoomFactor;
        offsetY += dy * zoomFactor;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
