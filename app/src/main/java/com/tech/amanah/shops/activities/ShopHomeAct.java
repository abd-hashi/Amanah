package com.tech.amanah.shops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tech.amanah.Constent.BaseClass;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.Compress;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.RealPathUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.activities.SelectService;
import com.tech.amanah.adapters.AdapterSupports;
import com.tech.amanah.adapters.CarAdapter;
import com.tech.amanah.databinding.ActivityShopHomeBinding;
import com.tech.amanah.databinding.AddItemsDialogBinding;
import com.tech.amanah.databinding.SupportDialogBinding;
import com.tech.amanah.devliveryservices.activities.NotificationAct;
import com.tech.amanah.devliveryservices.models.GraphModel;
import com.tech.amanah.devliveryservices.models.ModelSupport;
import com.tech.amanah.shops.adapters.AdapterShopItems;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.activities.RideOptionActivity;
import com.tech.amanah.taxiservices.models.ModelCar;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class ShopHomeAct extends AppCompatActivity implements ShopListener {

    private static final int PERMISSION_ID = 101;
    Context mContext = ShopHomeAct.this;
    ActivityShopHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    Dialog mDialog;
    AddItemsDialogBinding dialogBinding;
    File mFile;
    private final int GALLERY = 0, CAMERA = 1;
    private String str_image_path="";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    ArrayList<GraphModel.Result>arrayList = new ArrayList<>();
    String discount="";
    Bitmap oneBitmap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        Log.e("shopiIDIDID", "Shop Id = " + modelLogin.getResult().getId());
        itit();

    }

    private void itit() {


        binding.rvMyProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Scrolling down
                    binding.ivAddItems.setVisibility(View.VISIBLE);
                } else if (dy < 0) {
                    // Scrolling up
                    binding.ivAddItems.setVisibility(View.GONE);
                }
            }
        });

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());


        binding.tvUsername.setText("Name : "+modelLogin.getResult().getName());
        binding.tvShopName.setText("Shop Name : "+modelLogin.getResult().getUser_name());
        binding.ivAddItems.setOnClickListener(v -> {
            openAddItemDialog();
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnChangePass.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ChangePassAct.class));

        });

        binding.childNavDrawer.btnOrders.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ShopOrdersAct.class));
        });

        binding.childNavDrawer.btnMyProducts.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnSupport.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            supportApi();
        });

        binding.childNavDrawer.btnAddInfo.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, AddAccountInfoAct.class));

        });


        binding.childNavDrawer.btnMoney.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, EarningAct.class));

        });


        binding.ivBell.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationAct.class));
        });


    }


    private void logout()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("login_status", "1");
        param.put("user_id", modelLogin.getResult().getId());

        ApiCallBuilder.build(this)
                .setUrl(BaseClass.get().logout())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("GetCar", "==>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {

                                sharedPref.clearAllPreferences();
                                sharedPref.setlanguage("lan", "so");
                                ProjectUtil.updateResources(mContext,"so");
                                Intent loginscreen = new Intent(mContext, LoginActivity.class);
                                loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ProjectUtil.clearNortifications(mContext);
                                startActivity(loginscreen);
                                finishAffinity();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "catch :"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void Failed(String error) {
                        Toast.makeText(mContext, "Error :"+error, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void openAddItemDialog() {
        mFile = null;
        mDialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.setCancelable(true);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.add_items_dialog, null, false);
        mDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivItemImg.setOnClickListener(v -> {
           // if (checkPermisssionForReadStorage()) showImageSelection();

            if (Build.VERSION.SDK_INT >= 33) {
                if(checkPermissionFor12Above()) showImageSelection();
            }
            else {
                if (checkPermisssionForReadStorage()) showImageSelection();
            }
        });


        dialogBinding.etItemDiscount.setOnClickListener(v -> showDropDownDiscount(v,dialogBinding.etItemDiscount,ProjectUtil.getPercent()));

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etItemName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etItemPrice.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etDescription.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(dialogBinding.etItemQuantity.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(discount)) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            }
            else if (oneBitmap == null) {
                Toast.makeText(mContext, getString(R.string.please_add_item_img), Toast.LENGTH_SHORT).show();
            } else {
                double withDiscount = Double.parseDouble(dialogBinding.etItemPrice.getText().toString().trim()) - (Double.parseDouble(dialogBinding.etItemPrice.getText().toString().trim()) * Double.parseDouble(discount))/100;

                addItemApiCall(mDialog,
                        dialogBinding.etItemName.getText().toString().trim(),
                        dialogBinding.etItemPrice.getText().toString().trim(),
                        dialogBinding.etItemQuantity.getText().toString().trim(),
                        discount+"",
                        withDiscount+"",
                        dialogBinding.etDescription.getText().toString().trim()

                );
            }
        });




        mDialog.show();

    }


    private void EditItemDialog(ModelShopItems.Result modelShopItems) {
        mFile   = new File(modelShopItems.getItem_image());
        mDialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.setCancelable(true);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.add_items_dialog, null, false);
        mDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivItemImg.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 33) {
                if(checkPermissionFor12Above()) showImageSelection();
            }
            else {
                if (checkPermisssionForReadStorage()) showImageSelection();
            }
        });

        dialogBinding.etItemName.setText(modelShopItems.getItem_name());
        dialogBinding.etItemPrice.setText(modelShopItems.getItem_price());
        dialogBinding.etItemQuantity.setText(modelShopItems.getQuantity());
        dialogBinding.etItemDiscount.setText(modelShopItems.getDiscount());
        dialogBinding.etDescription.setText(modelShopItems.getItem_description());

        Glide.with(ShopHomeAct.this)
                .load(modelShopItems.getItem_image())
                .into(dialogBinding.ivItemImg);

        dialogBinding.etItemDiscount.setOnClickListener(v -> showDropDownDiscount(v,dialogBinding.etItemDiscount,ProjectUtil.getPercent()));

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etItemName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etItemPrice.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etDescription.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(dialogBinding.etItemQuantity.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(discount)) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            }
            else if (oneBitmap == null) {
                Toast.makeText(mContext, getString(R.string.please_add_item_img), Toast.LENGTH_SHORT).show();
            } else {
                double withDiscount = Double.parseDouble(dialogBinding.etItemPrice.getText().toString().trim()) - (Double.parseDouble(dialogBinding.etItemPrice.getText().toString().trim()) * Double.parseDouble(discount))/100;

                editItemApiCall(mDialog,
                        modelShopItems.getId(),
                        dialogBinding.etItemName.getText().toString().trim(),
                        dialogBinding.etItemPrice.getText().toString().trim(),
                        dialogBinding.etItemQuantity.getText().toString().trim(),
                        discount+"",
                        withDiscount+"",
                        dialogBinding.etDescription.getText().toString().trim()

                );
            }
        });




        mDialog.show();

    }


    private void showDropDownDiscount(View v, TextView textView, List<String> stringList) {
        PopupMenu popupMenu = new PopupMenu(ShopHomeAct.this, v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i));
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            textView.setText(menuItem.getTitle());
            String dis[] =  String.valueOf(menuItem.getTitle()).split("%");
            discount = dis[0];
            return true;
        });
        popupMenu.show();
    }






    public void showImageSelection() {

        final Dialog dialog = new Dialog(ShopHomeAct.this);
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
                Uri photoURI = FileProvider.getUriForFile(ShopHomeAct.this,
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
         str_image_path = image.getAbsolutePath();


        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
              /*  str_image_path = ProjectUtil.getRealPathFromURI(ShopHomeAct.this, data.getData());
                mFile   = new File(str_image_path);
                Glide.with(ShopHomeAct.this)
                        .load(str_image_path)
                        .into(dialogBinding.ivItemImg);*/

                str_image_path = ProjectUtil.getRealPathFromURI(ShopHomeAct.this, data.getData());
                try {
                    oneBitmap =    MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Glide.with(ShopHomeAct.this)
                        .load(oneBitmap)
                        .centerCrop()
                        .into(dialogBinding.ivItemImg);


            } else if (requestCode == REQUEST_CAMERA) {
               /* mFile     = new File(str_image_path);
                Glide.with(ShopHomeAct.this)
                        .load(str_image_path)
                        .into(dialogBinding.ivItemImg);*/

                oneBitmap = (Bitmap) data.getExtras().get("data");   //
                Log.e("=======",oneBitmap+"");
                Glide.with(ShopHomeAct.this)
                        .load(oneBitmap)
                        .centerCrop()
                        .into(dialogBinding.ivItemImg);

            }


        }



    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(ShopHomeAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(ShopHomeAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(ShopHomeAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShopHomeAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(ShopHomeAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ShopHomeAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(ShopHomeAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ShopHomeAct.this,
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
        if (ContextCompat.checkSelfPermission(ShopHomeAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(ShopHomeAct.this,
                        Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED

        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShopHomeAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(ShopHomeAct.this,
                            Manifest.permission.READ_MEDIA_IMAGES)
            ) {


                ActivityCompat.requestPermissions(ShopHomeAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ShopHomeAct.this,
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
                        Toast.makeText(ShopHomeAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopHomeAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ShopHomeAct.this, "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopHomeAct.this, "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }






    private void getShopItemsApiCall() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllShopItems(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("getShopItemsApiCall", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        ModelShopItems modelShopItems = new Gson().fromJson(responseString, ModelShopItems.class);

                        AdapterShopItems adapterShopItems = new AdapterShopItems(mContext, modelShopItems.getResult(),ShopHomeAct.this);
                        binding.rvMyProducts.setAdapter(adapterShopItems);

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

    private void addItemApiCall(Dialog dialog, String name, String price,String quantity11,String discountMain,String withDis, String description) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        MultipartBody.Part itemImgfilePart;

        mFile =  persistImage(oneBitmap,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),ShopHomeAct.this);


        itemImgfilePart = MultipartBody.Part.createFormData("item_image", mFile.getName(), RequestBody.create(MediaType.parse("item_image/*"), mFile));

        RequestBody shop_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody item_name = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody item_price = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), quantity11);
        RequestBody dis = RequestBody.create(MediaType.parse("text/plain"), discountMain);
        RequestBody DizWithPr = RequestBody.create(MediaType.parse("text/plain"), withDis);

        RequestBody item_description = RequestBody.create(MediaType.parse("text/plain"), description);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addShopsItems(shop_id, item_name, item_price,quantity,dis,DizWithPr, item_description, itemImgfilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("addItemApiCall", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        mFile = null;
                        str_image_path = "";
                        getShopItemsApiCall();
                        dialog.dismiss();
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


    private void editItemApiCall(Dialog dialog,String itemId, String name, String price,String quantity11,String discountMain,String withDis, String description) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        MultipartBody.Part itemImgfilePart;

        if(oneBitmap!=null){
            mFile = persistImage(oneBitmap,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),ShopHomeAct.this);
            itemImgfilePart = MultipartBody.Part.createFormData("item_image", mFile.getName(), RequestBody.create(MediaType.parse("item_image/*"), mFile));
        }
        else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            itemImgfilePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody item_id = RequestBody.create(MediaType.parse("text/plain"), itemId);
        RequestBody item_name = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody item_price = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), quantity11);
        RequestBody dis = RequestBody.create(MediaType.parse("text/plain"), discountMain);
        RequestBody DizWithPr = RequestBody.create(MediaType.parse("text/plain"), withDis);

        RequestBody item_description = RequestBody.create(MediaType.parse("text/plain"), description);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.editShopsItems(item_id, item_name, item_price,quantity,dis,DizWithPr, item_description, itemImgfilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("addItemApiCall", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        mFile = null;
                        str_image_path = "";
                        getShopItemsApiCall();
                        dialog.dismiss();
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



    private void logoutAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();

                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                PERMISSION_ID
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    private void drawLineChart() {
        LineChart lineChart = findViewById(R.id.lineChart);
        List<Entry> lineEntries = getDataSet();
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Sales in this week");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(Color.CYAN);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(Color.CYAN);
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(lineDataSet);
        lineChart.getDescription().setTextSize(12);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.setData(lineData);

        // Setup X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setGranularityEnabled(true);
       // xAxis.setGranularity(1.0f);
      //  xAxis.setXOffset(1f);
       // xAxis.setLabelCount(25);
        xAxis.setAxisMinimum(0);
    //    xAxis.setAxisMaximum(24);

        // Setup Y Axis
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
      //  yAxis.setAxisMaximum(300000000);
        //yAxis.setGranularity(1f);

        ArrayList<String> yAxisLabel = new ArrayList<>();
       // yAxisLabel.add(" ");
      //  yAxisLabel.add("Rest");
      //  yAxisLabel.add("Work");
      //  yAxisLabel.add("2-up");

       // lineChart.getAxisLeft().setCenterAxisLabels(true);
       /* lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if(value == -1 || value >= yAxisLabel.size()) return "";
                return yAxisLabel.get((int) value);
            }
        });*/

        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate();
    }

    private List<Entry> getDataSet() {
        List<Entry> lineEntries = new ArrayList<>();
        if(arrayList.size()>0) {
            lineEntries.add(new Entry(0, Float.parseFloat(arrayList.get(0).getDay())));
            lineEntries.add(new Entry(1, Float.parseFloat(arrayList.get(1).getDay())));
            lineEntries.add(new Entry(2,  Float.parseFloat(arrayList.get(2).getDay())));
            lineEntries.add(new Entry(3,  Float.parseFloat(arrayList.get(3).getDay())));
            lineEntries.add(new Entry(4,  Float.parseFloat(arrayList.get(4).getDay())));
            lineEntries.add(new Entry(5,  Float.parseFloat(arrayList.get(5).getDay())));
            lineEntries.add(new Entry(6,  Float.parseFloat(arrayList.get(6).getDay())));
        }
        else {
            lineEntries.add(new Entry(0, 0));
            lineEntries.add(new Entry(1, 0));
            lineEntries.add(new Entry(2, 0));
            lineEntries.add(new Entry(3, 0));
            lineEntries.add(new Entry(4, 0));
            lineEntries.add(new Entry(5, 0));
            lineEntries.add(new Entry(6, 0));
        }

       /* lineEntries.add(new Entry(6, 2));
        lineEntries.add(new Entry(7, 2));
        lineEntries.add(new Entry(8, 2));
        lineEntries.add(new Entry(9, 2));
        lineEntries.add(new Entry(10, 2));

        lineEntries.add(new Entry(11, 1));
        lineEntries.add(new Entry(12, 1));

        lineEntries.add(new Entry(13, 2));
        lineEntries.add(new Entry(14, 2));
        lineEntries.add(new Entry(15, 2));

        lineEntries.add(new Entry(16, 1));
        lineEntries.add(new Entry(17, 1));

        lineEntries.add(new Entry(18, 2));
        lineEntries.add(new Entry(19, 2));
        lineEntries.add(new Entry(20, 2));
        lineEntries.add(new Entry(21, 2));

        lineEntries.add(new Entry(22, 1));
        lineEntries.add(new Entry(23, 1));
        lineEntries.add(new Entry(24, 1));*/
        return lineEntries;
    }



    private void getAllGraph() {
        HashMap<String, String> param = new HashMap<>();
        param.put("shop_id", modelLogin.getResult().getShop_id());
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getGraph(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    getShopItemsApiCall();

                    Log.e("get graph response", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        GraphModel model = new Gson().fromJson(responseString, GraphModel.class);
                        arrayList.clear();
                        arrayList.addAll(model.getResult());
                        drawLineChart();

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


    @Override
    public void onShop(int position,ModelShopItems.Result modelShopItems) {
       EditItemDialog(modelShopItems);
    }


    private void supportApi() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.getSupportApi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("supportApi", "supportApi = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        ModelSupport modelSupport = new Gson().fromJson(stringResponse, ModelSupport.class);
                        supportDialog(modelSupport);
                    } else {
                        Toast.makeText(ShopHomeAct.this, "Support is not available at this time", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });
    }


    private void CounterApi() {
       // ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        Call<ResponseBody> call = api.getCounter(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("CounterApi", "supportApi = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                          binding.tvCount.setVisibility(View.VISIBLE);
                          binding.tvCount.setText(jsonObject.getString("result"));

                    } else {
                        binding.tvCount.setVisibility(View.GONE);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });
    }



    private void supportDialog(ModelSupport modelSupport) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SupportDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.support_dialog,
                        null, false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterSupports adapterSupports = new AdapterSupports(mContext, modelSupport.getResult());
        dialogBinding.rvSupport.setAdapter(adapterSupports);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(modelLogin!=null){
            getAllGraph();
            CounterApi();
        }
    }

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