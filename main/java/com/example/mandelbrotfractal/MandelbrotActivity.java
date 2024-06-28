package com.example.mandelbrotfractal;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MandelbrotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandelbrot);

        MandelbrotView mandelbrotView = findViewById(R.id.mandelbrotView);

        findViewById(R.id.btnZoomIn).setOnClickListener(v -> mandelbrotView.zoomOut());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> mandelbrotView.zoomIn());
    }
}
