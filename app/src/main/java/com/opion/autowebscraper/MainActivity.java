package com.opion.autowebscraper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText urlEditText;
    private TextView titleTextView, linkTv, warn;
    LinearLayout websiteDetails;
    private TextView descriptionTextView;
    ProgressBar progressBar;
    private ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = findViewById(R.id.urlEditText);
        linkTv = findViewById(R.id.linkTv);
        warn = findViewById(R.id.warn);
        titleTextView = findViewById(R.id.titleTextView);
        progressBar = findViewById(R.id.progress_circular);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        imageView = findViewById(R.id.imageView);
        websiteDetails = findViewById(R.id.websiteDetails);

        urlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String url = editable.toString();
                if (isValidUrl(url)) {
                    fetchWebsiteData(url);
                    websiteDetails.setVisibility(View.VISIBLE);
                    warn.setVisibility(View.GONE);
                } else {
                    websiteDetails.setVisibility(View.GONE);
                    warn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean isValidUrl(String url) {
        // You can add your URL validation logic here
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void fetchWebsiteData(String url) {
        // Perform network operations in a background thread
        new Thread(() -> {
            try {
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                String description = document.select("meta[name=description]").attr("content");
                String imageUrl = document.select("meta[property=og:image]").attr("content");

                runOnUiThread(() -> {
                    titleTextView.setText(title);
                    if (description == null || description.equals("")){
                        descriptionTextView.setVisibility(View.GONE);
                    } else {
                        descriptionTextView.setText(description);
                    }
                    linkTv.setText(urlEditText.getText().toString().trim());
                    // Load and display the image using Glide
                    Glide.with(this)
                            .load(imageUrl)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    imageView.setImageResource(R.drawable.failed);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(imageView);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
