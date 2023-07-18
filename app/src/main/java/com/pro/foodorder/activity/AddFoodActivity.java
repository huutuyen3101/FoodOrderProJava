package com.pro.foodorder.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pro.foodorder.ControllerApplication;
import com.pro.foodorder.R;
import com.pro.foodorder.constant.Constant;
import com.pro.foodorder.constant.GlobalFunction;
import com.pro.foodorder.databinding.ActivityAddFoodBinding;
import com.pro.foodorder.model.Food;
import com.pro.foodorder.model.FoodObject;
import com.pro.foodorder.model.Image;
import com.pro.foodorder.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gun0912.tedimagepicker.builder.TedImagePicker;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AddFoodActivity extends BaseActivity {

    private ActivityAddFoodBinding mActivityAddFoodBinding;
    private boolean isUpdate;
    private Food mFood;
    private String[] arr = {"Món Mới","Gà Rán - Gà Quay","Burger - Cơm - Mì Ý", "Thức Uống - Tráng Miệng"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddFoodBinding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(mActivityAddFoodBinding.getRoot());

        getDataIntent();
        initToolbar();
        initView();

        mActivityAddFoodBinding.btnAddOrEdit.setOnClickListener(v -> addOrEditFood());
        mActivityAddFoodBinding.imgAddProduct.setOnClickListener(view -> showGallery());
        mActivityAddFoodBinding.imgAddProductBanner.setOnClickListener(view -> showGalleryBanner());

    }

    private void showGalleryBanner() {
        TedImagePicker.with(this)
                .showCameraTile(false)
                .dropDownAlbum()
                .start(new Function1<Uri, Unit>() {
                    @Override
                    public Unit invoke(Uri uri) {
                        mActivityAddFoodBinding.edtImageBanner.setText(uri.toString());
                        setImageProductBanner(uri);
                        //uploadImage(uri);
                        return null;
                    }
                });
    }

    private void showGallery() {
        TedImagePicker.with(this)
                .showCameraTile(false)
                .dropDownAlbum()
                .start(new Function1<Uri, Unit>() {
                    @Override
                    public Unit invoke(Uri uri) {
                        mActivityAddFoodBinding.edtImage.setText(uri.toString());
                        setImageProduct(uri);
                        //uploadImage(uri);
                        return null;
                    }
                });
    }

    private void setImageProduct(Uri uri) {
        Glide.with(this).load(uri).into(mActivityAddFoodBinding.imgAddProduct);
    }

    private void setImageProductBanner(Uri uri) {
        Glide.with(this).load(uri).into(mActivityAddFoodBinding.imgAddProductBanner);
    }

    private Observable<String> uploadImageToFirestore(String imageUrl) {
        return Observable.create(emitter -> {
            if (imageUrl.contains("https")){
                emitter.onNext(imageUrl);
                emitter.onComplete();
                return;
            }
            String urlFile = new Date().getTime() + ".png";
            final StorageReference ref = FirebaseStorage.getInstance()
                    .getReference("/image_product").child(urlFile);
            UploadTask uploadTask = ref.putFile(Uri.parse(imageUrl));
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        emitter.onNext(downloadUri.toString());
                        emitter.onComplete();
                    } else {
                        Log.d("asdasdasdas", task.getException().toString());
                    }
                }
            });
        });
    }


    public void uploadImage(Uri uri) {
        String urlFile = new Date().getTime() + ".png";
        final StorageReference ref = FirebaseStorage.getInstance()
                .getReference("/image_product").child(urlFile);
        UploadTask uploadTask = ref.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    Log.d("asdasdasdas", task.getException().toString());
                }
            }
        });
    }

    private void getDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mFood = (Food) bundleReceived.get(Constant.KEY_INTENT_FOOD_OBJECT);
        }
    }

    private void initToolbar() {
        mActivityAddFoodBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityAddFoodBinding.toolbar.imgCart.setVisibility(View.GONE);

        mActivityAddFoodBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (isUpdate) {
            mActivityAddFoodBinding.toolbar.tvTitle.setText(getString(R.string.edit_food));
            mActivityAddFoodBinding.btnAddOrEdit.setText(getString(R.string.action_edit));
            setImageProduct(Uri.parse(mFood.getImage()));
            setImageProductBanner(Uri.parse(mFood.getBanner()));

            mActivityAddFoodBinding.edtName.setText(mFood.getName());
            mActivityAddFoodBinding.edtDescription.setText(mFood.getDescription());
            mActivityAddFoodBinding.edtPrice.setText(String.valueOf(mFood.getPrice()));
            mActivityAddFoodBinding.edtDiscount.setText(String.valueOf(mFood.getSale()));
            mActivityAddFoodBinding.edtImage.setText(mFood.getImage());
            mActivityAddFoodBinding.edtImageBanner.setText(mFood.getBanner());
            mActivityAddFoodBinding.chbPopular.setChecked(mFood.isPopular());
            mActivityAddFoodBinding.edtOtherImage.setText(getTextOtherImages());
        } else {
            mActivityAddFoodBinding.toolbar.tvTitle.setText(getString(R.string.add_food));
            mActivityAddFoodBinding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,arr);
        mActivityAddFoodBinding.spnType.setAdapter(adapter);
    }

    private String getTextOtherImages() {
        String result = "";
        if (mFood == null || mFood.getImages() == null || mFood.getImages().isEmpty()) {
            return result;
        }
        for (Image image : mFood.getImages()) {
            if (StringUtil.isEmpty(result)) {
                result = result + image.getUrl();
            } else {
                result = result + ";" + image.getUrl();
            }
        }
        return result;
    }

    private void addOrEditFood() {
        String strName = mActivityAddFoodBinding.edtName.getText().toString().trim();
        String strDescription = mActivityAddFoodBinding.edtDescription.getText().toString().trim();
        String strPrice = mActivityAddFoodBinding.edtPrice.getText().toString().trim();
        String strDiscount = mActivityAddFoodBinding.edtDiscount.getText().toString().trim();
        mActivityAddFoodBinding.edtDiscount.setText("0");
        String strImage = mActivityAddFoodBinding.edtImage.getText().toString().trim();
        String strImageBanner = mActivityAddFoodBinding.edtImageBanner.getText().toString().trim();
        boolean isPopular = mActivityAddFoodBinding.chbPopular.isChecked();
        String strOtherImages = mActivityAddFoodBinding.edtOtherImage.getText().toString().trim();
        List<Image> listImages = new ArrayList<>();
        if (!StringUtil.isEmpty(strOtherImages)) {
            String[] temp = strOtherImages.split(";");
            for (String strUrl : temp) {
                Image image = new Image(strUrl);
                listImages.add(image);
            }
        }

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_food_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strDescription)) {
            Toast.makeText(this, getString(R.string.msg_description_food_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_food_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_food_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImageBanner)) {
            Toast.makeText(this, getString(R.string.msg_image_banner_food_require), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] imageUrls = new String[]{strImage, strImageBanner};

        int type = 1;
        if (mActivityAddFoodBinding.spnType.getSelectedItem().toString().equals("Món Mới")){
             type = 1;
        }else if(mActivityAddFoodBinding.spnType.getSelectedItem().toString().equals("Gà Rán - Gà Quay")){
            type = 2;

        }else if(mActivityAddFoodBinding.spnType.getSelectedItem().toString().equals("Burger - Cơm - Mì Ý")){
            type = 3;

        }else{
            type = 4;
        }


        showProgressDialog(true);
        int finalType = type;
        Observable.fromArray(imageUrls)
                .flatMap(url -> uploadImageToFirestore(url).subscribeOn(Schedulers.io()))
                .toList()
                .subscribe(urls -> {
                    addOrEditProductToServer(strName, strDescription, strPrice, urls.get(0), urls.get(1), isPopular, finalType);
                }, throwable -> {
                    // Xử lý khi có lỗi xảy ra
                    throwable.printStackTrace();
                });
    }

    private void addOrEditProductToServer(String strName,
                                          String strDescription,
                                          String strPrice,
                                          String strImage,
                                          String strImageBanner,
                                          boolean isPopular,
                                          int type) {

        // Update food
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("description", strDescription);
            map.put("price", Integer.parseInt(strPrice));
            map.put("sale", 0);
            map.put("image", strImage);
            map.put("banner", strImageBanner);
            map.put("popular", isPopular);
            map.put("type", type);

            ControllerApplication.get(this).getFoodDatabaseReference()
                    .child(String.valueOf(mFood.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(AddFoodActivity.this,
                                getString(R.string.msg_edit_food_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add food
        showProgressDialog(true);
        long foodId = System.currentTimeMillis();
        FoodObject food = new FoodObject(foodId, strName, strDescription, Integer.parseInt(strPrice),
                0, strImage, strImageBanner, isPopular, type);

        ControllerApplication.get(this).getFoodDatabaseReference()
                .child(String.valueOf(foodId)).setValue(food, (error, ref) -> {
                    showProgressDialog(false);
                    mActivityAddFoodBinding.edtName.setText("");
                    mActivityAddFoodBinding.edtDescription.setText("");
                    mActivityAddFoodBinding.edtPrice.setText("");
                    mActivityAddFoodBinding.edtDiscount.setText("");
                    mActivityAddFoodBinding.edtImage.setText("");
                    mActivityAddFoodBinding.edtImageBanner.setText("");
                    mActivityAddFoodBinding.chbPopular.setChecked(false);
                    mActivityAddFoodBinding.edtOtherImage.setText("");
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_food_success), Toast.LENGTH_SHORT).show();
                });

    }
}