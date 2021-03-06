package com.hrawat.theicyphoenix.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    Spinner spinnerFontSize;
    Spinner spinnerFont;

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
        spinnerFont = (Spinner) findViewById(R.id.spinner_font);
// Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapterFont = ArrayAdapter.createFromResource(this,
                R.array.font_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterFont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerFont.setAdapter(adapterFont);
        spinnerFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem() != null) {
                    Typeface typeface = getFontTypeFace(adapterView.getSelectedItem().toString());
                    if (typeface != null)
                        editText.setTypeface(typeface);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerFontSize = (Spinner) findViewById(R.id.spinner_font_size);
// Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapterFontSize = ArrayAdapter.createFromResource(this,
                R.array.font_size_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterFontSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerFontSize.setAdapter(adapterFontSize);
        spinnerFontSize.setSelection(2);
        spinnerFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem() != null) {
                    editText.setTextSize(Float.parseFloat(adapterView.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private Typeface getFontTypeFace(String font) {
        Typeface typeface = null;
        switch (font) {
            case "Open Sans":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_open_sans.ttf");
                break;
            case "Quicksand":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_quicksand.otf");
                break;
            case "Raleway":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_raleway.ttf");
                break;
            case "Aller":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_aller.ttf");
                break;
            case "GoodDog":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_good_dog.otf");
                break;
            case "Lobster":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_lobster.otf");
                break;
            case "Allura":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_allura.otf");
                break;
            case "Kaushan Script":
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_kaushan.otf");
                break;
            case "Roboto":
            default:
                typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/font_roboto.ttf");
                break;
        }
        return typeface;
    }

    private void startCovertingImage() {
        View viewRoot;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewRoot = inflater.inflate(R.layout.template_t1, null);
        LinearLayout root = viewRoot.findViewById(R.id.parent_ti);
        TextView text = viewRoot.findViewById(R.id.tv_text);
        TextView textBottom = viewRoot.findViewById(R.id.tv_bottom);
        Typeface typeface = getFontTypeFace(spinnerFont.getSelectedItem().toString());
        Float textSize = Float.parseFloat(spinnerFontSize.getSelectedItem().toString());
        Float bottomTextSize = textSize - 4;
        if (typeface != null) {
            text.setTypeface(typeface);
//            textBottom.setTypeface(typeface);
//            textBottom.setTextSize(bottomTextSize);
            text.setTextSize(textSize);
        }
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
