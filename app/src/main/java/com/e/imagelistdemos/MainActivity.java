package com.e.imagelistdemos;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.e.imagelistdemos.databinding.ActivityMainBinding;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    protected static final int PERMISSION_REQUEST_CODE = 999;
    private static final int IMAGE_PICK_REQUEST = 100;
    private ArrayList<String> imagesList = new ArrayList<>();
    private ImagesAdapter imagesAdapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        imagesAdapter = new ImagesAdapter(new ItemOnClick() {
            @Override
            public void onClicked(Integer position) {
                imagesAdapter.removeAt(position);
                imagesList.remove(position);
            }
        });
        imagesAdapter.setDataItemList(imagesList);
        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImages.setAdapter(imagesAdapter);
        binding.rvImages.setItemAnimator(new DefaultItemAnimator());
        binding.rvImages.setNestedScrollingEnabled(false);
    }

    public void addImages(View view) {
        if (!checkPermission()) {
            requestPermission();
        } else {
            ImagePicker.with(MainActivity.this)
                    .setFolderMode(true)
                    .setMultipleMode(true)
                    .setShowNumberIndicator(true)
                    .setMaxSize(10)
                    .setLimitMessage("You can select up to 10 images")
                    .setRequestCode(100)
                    .start();
            /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Pictures: "), 1);*/
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        uri.add(data.getClipData().getItemAt(i).getUri());
                    }
                    imagesAdapter.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            ArrayList<Image> selectedImagesList = ImagePicker.getImages(data);
            for (int i = 0; i < selectedImagesList.size(); i++) {
//                ImageCompression(selectedImagesList.get(i).getPath());
                imagesList.add(selectedImagesList.get(i).getPath());
            }
            imagesAdapter.setDataItemList(imagesList);
        }
    }

    private void ImageCompression(final String mediaPath) {
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mediaPath).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                //return the compressed file path
                imagesList.add(outfile);
                imagesAdapter.setDataItemList(imagesList);
            }
        });
    }

    public boolean checkPermission() {
        int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int internalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int externalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && internalStorage == PackageManager.PERMISSION_GRANTED
                && externalStorage == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean internalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean externalStorage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && internalStorage && externalStorage) {
//                        successToast("Permission Granted");
                        ImagePicker.with(MainActivity.this)
                                .setFolderMode(true)
                                .setMultipleMode(true)
                                .setShowNumberIndicator(true)
                                .setMaxSize(10)
                                .setLimitMessage("You cannot select more than 10 images. Please deselect another image before trying to select again.")
                                .setRequestCode(IMAGE_PICK_REQUEST)
                                .start();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA) && shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                                    && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }
}