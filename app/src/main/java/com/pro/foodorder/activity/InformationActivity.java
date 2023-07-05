package com.pro.foodorder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.foodorder.R;
import com.pro.foodorder.constant.Constant;
import com.pro.foodorder.databinding.ActivityInformationBinding;
import com.pro.foodorder.databinding.ActivityOrderHistoryBinding;
import com.pro.foodorder.model.User;
import com.pro.foodorder.prefs.DataStoreManager;

import java.util.HashMap;
import java.util.Map;

public class InformationActivity extends AppCompatActivity {

    private ActivityInformationBinding activityInformationBinding;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInformationBinding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(activityInformationBinding.getRoot());

        initToolbar();
        loadDataCart();

        activityInformationBinding.tvSendChange.setOnClickListener(v-> onUpload());
    }


    private void initToolbar() {
        activityInformationBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        activityInformationBinding.toolbar.imgCart.setVisibility(View.GONE);
        activityInformationBinding.toolbar.tvTitle.setText(getString(R.string.information));

        activityInformationBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

    }

    private void onUpload(){
        EditText editText = findViewById(R.id.edt_address);
        String textValue = editText.getText().toString();

        EditText editTen = findViewById(R.id.edt_name);
        String textTen = editTen.getText().toString();

        EditText editSdt = findViewById(R.id.edt_phone);
        String textSdt = editSdt.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();


        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("address", textValue);
        data.put("name", textTen);
        data.put("phone", textSdt);

        databaseRef.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thay đổi dữ liệu thành công
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi trong quá trình thay đổi dữ liệu
                    }
                });






    }

    private void getData(User user){
        EditText editText = findViewById(R.id.edt_address);
        String textValue = editText.getText().toString();

        EditText editTen = findViewById(R.id.edt_name);
        String textTen = editTen.getText().toString();

        EditText editSdt = findViewById(R.id.edt_phone);
        String textSdt = editSdt.getText().toString();

    }

    private void loadDataCart() {
        FirebaseDatabase.getInstance(Constant.FIREBASE_URL).getReference().child("User")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user.getId().equals(DataStoreManager.getUser().getId())) {
                                setDataUser(user);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setDataUser(User user) {
        activityInformationBinding.edtName.setText(user.getName());
        activityInformationBinding.edtAddress.setText(user.getAddress());
        activityInformationBinding.edtPhone.setText(user.getPhone());
        activityInformationBinding.edtEmail.setText(user.getEmail());
    }


}