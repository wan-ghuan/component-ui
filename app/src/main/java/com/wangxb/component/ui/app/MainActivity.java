package com.wangxb.component.ui.app;

import com.wangxb.component.ui.view.SignatureView;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignatureView signature = findViewById(R.id.signature_view);
        Button clear = findViewById(R.id.signature_clear);
        Button confirm = findViewById(R.id.signature_confirm);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signature.clearSignature();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signature.getSignature();
            }
        });
    }
}
