package com.hrawat.theicyphoenix.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hrawat.theicyphoenix.R;
import com.hrawat.theicyphoenix.activity.utils.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    private EditText editText;
    private String mediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText = (EditText) findViewById(R.id.et_text);
        Button buttonPreview = (Button) findViewById(R.id.btn_preview);
        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().length() > 0)
                    startCovertingImage();
                else
                    Toast.makeText(HomeActivity.this, "No text found!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCovertingImage() {
        View viewRoot;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewRoot = inflater.inflate(R.layout.template_t1, null);
        LinearLayout root = (LinearLayout) viewRoot.findViewById(R.id.parent_ti);
        TextView text = viewRoot.findViewById(R.id.tv_text);
        text.setText(editText.getText().toString());
        root.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        root.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
        root.buildDrawingCache(true);
        if (root.getDrawingCache() != null) {
            Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 566, 600, false);
            root.setDrawingCacheEnabled(false); // clear drawing cache
            showImageDialog(bitmap);
        } else {
            Toast.makeText(HomeActivity.this, "Please make your content short", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageDialog(final Bitmap bitmap) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Preview Image");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.preview_dialog);
        Button btnShare = dialog.findViewById(R.id.btn_share);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        ImageView imageView = dialog.findViewById(R.id.iv_preview);
        imageView.setImageBitmap(bitmap);
        dialog.show();
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPath = BitmapUtils.scaledImagePath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mediaPath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                createInstagramIntent("image/*", mediaPath);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void createInstagramIntent(String type, String mediaPath) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        share.setType(type);
        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);
        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
