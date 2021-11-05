package com.example.babblechatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.babblechatapp.databinding.ActivityMainBinding;
import com.example.babblechatapp.utilities.Constants;
import com.example.babblechatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // like any UI framework, everytime a View is created, framework automatically generates a Binding class for binding logic with GUI
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout inflater: converting the appearance definition (.xml file) into Java View object
        // inflate is the function that do the conversion recursively, traversing from parent down to child and convert each to sub-member of the Java View Object
        // After conversion, child in XML will become member of the Java Object (equivalent to child)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // binding.getRoot() != R.layout.activity_main, it's the converted version to Java object, which is what we are interacting with. R.layout... is just the initial appearance file in @res, not impacted by any codes
        // the R.layout.activity_main is still the XML design View (non-converted version of the layout and thus not having any update that's done to "binding" object)
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        // bind the button at the start of the program
        setListeners();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
    }

    /**
     * Get data from preference manager and bind the data to the interface (set text the user name and set image from image data)
     */
    private void loadUserDetails() {
        loadName();
        loadImage();
    }

    private void loadName() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
    }

    private void loadImage() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    /**
     * shorten the Toasting process
     * @param message the message we want to toast to screen
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    /**
     * add FireCloudMessaging (FCM) Token after log in
     * @param token - the token received from firebase
     */
    private void updateToken(String token) {
        FirebaseFirestore database = getDatabase();
        DocumentReference documentReference = getDocumentReference(database);
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token update successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token! Going back to Login Screen"));
    }

    @NonNull
    private FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }

    @NonNull
    private DocumentReference getDocumentReference(FirebaseFirestore database) {
        return database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
    }

    private void signOut() {
        showToast("Signing out...");
        // accessing current database (truy vấn database, bước này sẽ được làm ở mọi chỗ cần sử dụng dữ liệu trong database
        FirebaseFirestore database = getDatabase();
        // accessing array of key "users" in the database
        DocumentReference documentReference = getDocumentReference(database);
        cleanUserDataAndBackToSignIn(documentReference);
    }

    private void cleanUserDataAndBackToSignIn(DocumentReference documentReference) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                // if delete token function is executed -> the database is updated, then:
                //      1. Clear the current shared preferences (the preference Manager)
                //      2. Go back to Sign In screen
                .addOnSuccessListener(startSignInActivity())
                .addOnFailureListener(handleException());
    }

    @NonNull
    private OnFailureListener handleException() {
        return e -> showToast("Unable to sign out");
    }

    @NonNull
    private OnSuccessListener<Void> startSignInActivity() {
        return unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        };
    }
}