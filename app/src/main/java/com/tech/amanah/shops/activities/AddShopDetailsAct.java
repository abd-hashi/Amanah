package com.tech.amanah.shops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.Compress;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.RealPathUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.activities.PinLocationActivity;
import com.tech.amanah.activities.SelectService;
import com.tech.amanah.databinding.ActivityAddShopDetailsBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterShopType;
import com.tech.amanah.devliveryservices.adapters.ShopCateAdapter;
import com.tech.amanah.devliveryservices.models.ModelShopCat;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.Tools;
import com.tech.amanah.utility.onDateSetListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddShopDetailsAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 102;
    private final int GALLERY = 0, CAMERA = 1;
    Context mContext = AddShopDetailsAct.this;
    ActivityAddShopDetailsBinding binding;
    File idCardFile, businessFile, frontFile;
    int imageCapturedCode;
    private LatLng latLng;
    ArrayList<String> typeIds = new ArrayList<>();
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String str_image_path1="",str_image_path2="",str_image_path3="",cityId="";
    ArrayList<ModelShopCat.Result>shopArrayList;


   // String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    double lati =0.0;
   double longi =0.0;
    public Bitmap oneBitmap=null,twoBitmap=null,threeBitmap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop_details);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        shopArrayList = new ArrayList<>();

        getCategoryApiCall();
        typeIds.add("1");
        typeIds.add("2");
        typeIds.add("3");
        typeIds.add("4");
        typeIds.add("5");
        typeIds.add("6");
        typeIds.add("7");
        typeIds.add("8");

        itit();

    }

    private void setImageFromCameraGallery(File file) {
        if (imageCapturedCode == 0) {
            businessFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    binding.ivbusinessLicenseImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
            Log.e("filefilefile", "After file Size = " + file.length() / 1024);
        } else if (imageCapturedCode == 1) {
            idCardFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    binding.ividCardImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            frontFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    binding.ivShopFrontImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        }
    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, GALLERY);
                                break;
                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null)
                                    startActivityForResult(cameraIntent, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

//    private void showPictureDialog() {
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
//        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                startActivityForResult(galleryIntent, GALLERY);
//                                break;
//                            case 1:
//                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(intent, CAMERA);
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }

    private void itit() {

        binding.address.setOnClickListener(v -> {
            sharedPref.setScreenType("type","");
            startActivityForResult(new Intent(mContext, PinLocationActivity.class), 222);
//           List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
//           Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                  .build(this);
//           startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.ivShopFrontImg.setOnClickListener(v -> {
          /*  if (checkPermisssionForReadStorage()) {
                imageCapturedCode = 0;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                // showPictureDialog();
                showImageSelection();
            }*/

            imageCapturedCode = 0;
           if (Build.VERSION.SDK_INT >= 33) {
                 if(checkPermissionFor12Above()) showImageSelection();
            }
            else {
                if (checkPermisssionForReadStorage()) showImageSelection();
            }
        });


        binding.ivbusinessLicenseImg.setOnClickListener(v -> {
        /*    if (checkPermisssionForReadStorage()) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                // showPictureDialog();
                showImageSelection();
            }*/

            imageCapturedCode = 1;
            if (Build.VERSION.SDK_INT >= 33) {
                if(checkPermissionFor12Above()) showImageSelection();
            }
            else {
                if (checkPermisssionForReadStorage()) showImageSelection();
            }
        });

        binding.ividCardImg.setOnClickListener(v -> {
          /*  if (checkPermisssionForReadStorage()) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                // showPictureDialog();
                showImageSelection();
            }*/

            imageCapturedCode = 2;
            if (Build.VERSION.SDK_INT >= 33) {
                if(checkPermissionFor12Above()) showImageSelection();
            }
            else {
                if (checkPermisssionForReadStorage()) showImageSelection();
            }
        });


        binding.etCloseTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String AM_PM;
                    if (selectedHour >= 0 && selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                    }
                    binding.etCloseTime.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        binding.etOpenTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String AM_PM;
                    if (selectedHour >= 0 && selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                    }
                    binding.etOpenTime.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });


        binding.spShopType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(shopArrayList.size()!=0) {
                    cityId = shopArrayList.get(i).getId();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etShopName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etOpenTime.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etCloseTime.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.address.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.landAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etDescription.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (oneBitmap == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_shop_front_copy), Toast.LENGTH_SHORT).show();
            } else if (twoBitmap == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_busi_license_copy), Toast.LENGTH_SHORT).show();
            } else if (threeBitmap == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_id_card_copy), Toast.LENGTH_SHORT).show();
            } else {
                addShopApiCall();
            }
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(AddShopDetailsAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void getPhotoFromGallary() {
    /*    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);*/


        Intent intent = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);

    }

    private void openCamera () {

          /*  File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/VeryCycle/Images/");

            if (!dirtostoreFile.exists()) {
                dirtostoreFile.mkdirs();
            }

            String timestr = DataManager.getInstance().convertDateToString(Calendar.getInstance().getTimeInMillis());

            File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/VeryCycle/Images/" + "IMG_" + timestr + ".jpg");

            str_image_path = tostoreFile.getPath();

            uriSavedImage = FileProvider.getUriForFile(Register.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    tostoreFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

            startActivityForResult(intent, REQUEST_CAMERA);*/



      /*  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddShopDetailsAct.this,
                        "com.tech.amanah.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }*/

        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" /*+ timeStamp + "_"*/;
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
         if(imageCapturedCode==0)  str_image_path1 = image.getAbsolutePath();
        if(imageCapturedCode==1)  str_image_path2 = image.getAbsolutePath();
        if(imageCapturedCode==2)  str_image_path3 = image.getAbsolutePath();


        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
                if(imageCapturedCode == 0) {
                  /*  str_image_path1 = ProjectUtil.getRealPathFromURI(AddShopDetailsAct.this, data.getData());
                    frontFile = new File(str_image_path1);
                    Glide.with(AddShopDetailsAct.this)
                            .load(str_image_path1)
                            .centerCrop()
                            .into(binding.ivShopFrontImg);*/


                    str_image_path1 = ProjectUtil.getRealPathFromURI(AddShopDetailsAct.this, data.getData());
                    try {
                        oneBitmap =    MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Glide.with(AddShopDetailsAct.this)
                            .load(oneBitmap)
                            .centerCrop()
                            .into(binding.ivShopFrontImg);

                }
                if(imageCapturedCode == 1) {
                 /*   str_image_path2 = ProjectUtil.getRealPathFromURI(AddShopDetailsAct.this, data.getData());
                    businessFile = new File(str_image_path2);
                    Glide.with(AddShopDetailsAct.this)
                            .load(str_image_path2)
                            .centerCrop()
                            .into(binding.ivbusinessLicenseImg);*/


                    str_image_path2 = ProjectUtil.getRealPathFromURI(AddShopDetailsAct.this, data.getData());
                    try {
                        twoBitmap =    MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Glide.with(AddShopDetailsAct.this)
                            .load(twoBitmap)
                            .centerCrop()
                            .into(binding.ivbusinessLicenseImg);


                }

                if(imageCapturedCode == 2) {
                  /*  str_image_path3 = ProjectUtil.getRealPathFromURI(AddShopDetailsAct.this, data.getData());
                    idCardFile = new File(str_image_path3);
                    Glide.with(AddShopDetailsAct.this)
                            .load(str_image_path3)
                            .centerCrop()
                            .into(binding.ividCardImg);*/

                    str_image_path3 = ProjectUtil.getRealPathFromURI(AddShopDetailsAct.this, data.getData());
                    try {
                        threeBitmap =    MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Glide.with(AddShopDetailsAct.this)
                            .load(threeBitmap)
                            .centerCrop()
                            .into(binding.ividCardImg);
                }


            } else if (requestCode == REQUEST_CAMERA) {
              if(imageCapturedCode == 0) {
                /*  frontFile = new File(str_image_path1);
                  Glide.with(AddShopDetailsAct.this)
                          .load(str_image_path1)
                          .centerCrop()
                          .into(binding.ivShopFrontImg);*/

                  oneBitmap = (Bitmap) data.getExtras().get("data");   //
                  Log.e("=======",oneBitmap+"");
                  Glide.with(AddShopDetailsAct.this)
                          .load(oneBitmap)
                          .centerCrop()
                          .into(binding.ivShopFrontImg);

              }

               else if(imageCapturedCode == 1) {
                /*   businessFile = new File(str_image_path2);
                  Glide.with(AddShopDetailsAct.this)
                          .load(str_image_path2)
                          .centerCrop()
                          .into(binding.ivbusinessLicenseImg);*/

                  twoBitmap = (Bitmap) data.getExtras().get("data");   //
                  Log.e("=======",twoBitmap+"");
                  Glide.with(AddShopDetailsAct.this)
                          .load(twoBitmap)
                          .centerCrop()
                          .into(binding.ivbusinessLicenseImg);

                }
                else if(imageCapturedCode == 2) {
                 /* idCardFile = new File(str_image_path3);
                  Glide.with(AddShopDetailsAct.this)
                          .load(str_image_path3)
                          .centerCrop()
                          .into(binding.ividCardImg);*/

                  threeBitmap = (Bitmap) data.getExtras().get("data");   //
                  Log.e("=======",threeBitmap+"");
                  Glide.with(AddShopDetailsAct.this)
                          .load(threeBitmap)
                          .centerCrop()
                          .into(binding.ividCardImg);


                }



            }



        }

        else if (resultCode == 222) {
            String add = data.getStringExtra("add");
            Log.e("sfasfdas", "fdasfdas = 222 = " + add);
            Log.e("sfasfdas", "fdasfdas = lat = " + data.getDoubleExtra("lat", 0.0));
            Log.e("sfasfdas", "fdasfdas = lon = " + data.getDoubleExtra("lon", 0.0));
             lati = data.getDoubleExtra("lat", 0.0);
             longi = data.getDoubleExtra("lon", 0.0);
            latLng = new LatLng(lati, longi);
            binding.address.setText(add);
        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(AddShopDetailsAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(AddShopDetailsAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(AddShopDetailsAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddShopDetailsAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(AddShopDetailsAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(AddShopDetailsAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(AddShopDetailsAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AddShopDetailsAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }

    public boolean checkPermissionFor12Above() {
        if (ContextCompat.checkSelfPermission(AddShopDetailsAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(AddShopDetailsAct.this,
                        Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED

        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddShopDetailsAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(AddShopDetailsAct.this,
                            Manifest.permission.READ_MEDIA_IMAGES)
            ) {


                ActivityCompat.requestPermissions(AddShopDetailsAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AddShopDetailsAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(AddShopDetailsAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopDetailsAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }

            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(AddShopDetailsAct.this, "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddShopDetailsAct.this, "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }

        }
    }

    private void getCategoryApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getShopCategory();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if(jsonObject.getString("status").equals("1")) {
                            Log.e("asfddasfasdf","response = " + response);
                            ModelShopCat modelShopCat = new Gson().fromJson(stringResponse,ModelShopCat.class);
                            shopArrayList.clear();
                            shopArrayList.addAll(modelShopCat.getResult());
                            binding.spShopType.setAdapter(new ShopCateAdapter(AddShopDetailsAct.this,modelShopCat.getResult()));
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception-->",t.getMessage());
            }

        });

    }




    private void addShopApiCall() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part idCardfilePart;
        MultipartBody.Part businessfilePart;
        MultipartBody.Part frontfilePart;

        idCardFile =  persistImage(oneBitmap,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),AddShopDetailsAct.this);
        businessFile =  persistImage(twoBitmap,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),AddShopDetailsAct.this);
        frontFile =  persistImage(threeBitmap,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),AddShopDetailsAct.this);


        idCardfilePart = MultipartBody.Part.createFormData("id_card_image", idCardFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), idCardFile));
        businessfilePart = MultipartBody.Part.createFormData("business_license_image", businessFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), businessFile));
        frontfilePart = MultipartBody.Part.createFormData("shop_front_image", frontFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), frontFile));

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody shop_name = RequestBody.create(MediaType.parse("text/plain"), binding.etShopName.getText().toString().trim());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), binding.etDescription.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.address.getText().toString().trim() +
                " " + binding.landAddress.getText().toString());
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lati));
        RequestBody lon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longi));
        RequestBody open_time = RequestBody.create(MediaType.parse("text/plain"), binding.etOpenTime.getText().toString());
        RequestBody close_time = RequestBody.create(MediaType.parse("text/plain"), binding.etCloseTime.getText().toString());
        RequestBody shopTypeId = RequestBody.create(MediaType.parse("text/plain"),cityId /*typeIds.get(binding.spShopType.getSelectedItemPosition())*/);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addShops(user_id, shop_name, description, address, shopTypeId
                , lat, lon, open_time, close_time, idCardfilePart, businessfilePart, frontfilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        modelLogin.getResult().setShop_status("1");
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                        startActivity(new Intent(mContext, LoginActivity.class));
                        // startActivity(new Intent(mContext, ShopHomeAct.class));
                        finish();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                // Glide.with(mContext).load(resultUri).into(binding.profileImage);
                File file = new File(RealPathUtil.getRealPath(mContext, resultUri));
                setImageFromCameraGallery(file);
                Log.e("asfasdasdad", "resultUri = " + resultUri);

                // binding.profileImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

        } else if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = RealPathUtil.getRealPath(mContext, data.getData());
                setImageFromCameraGallery(new File(path));
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                try {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());

                        Uri tempUri = getImageUri(mContext, imageBitmap);

                        String image = RealPathUtil.getRealPath(mContext, tempUri);
                        setImageFromCameraGallery(new File(image));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == 222) {
            String add = data.getStringExtra("add");
            Log.e("sfasfdas", "fdasfdas = 222 = " + add);
            Log.e("sfasfdas", "fdasfdas = lat = " + data.getDoubleExtra("lat", 0.0));
            Log.e("sfasfdas", "fdasfdas = lon = " + data.getDoubleExtra("lon", 0.0));
            double lat = data.getDoubleExtra("lat", 0.0);
            double lon = data.getDoubleExtra("lon", 0.0);
            latLng = new LatLng(lat, lon);
            binding.address.setText(add);
        }

    }*/


    private  File persistImage(Bitmap bitmap, String name, Context cOntext) {
        File filesDir = cOntext.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "persistImage: "+e.getMessage() );
        }

        return  imageFile;

    }



}


