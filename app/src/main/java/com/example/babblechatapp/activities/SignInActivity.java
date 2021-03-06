package com.example.babblechatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.babblechatapp.databinding.ActivitySignInBinding;
import com.example.babblechatapp.utilities.Constants;
import com.example.babblechatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        // start the main screen once the sign in process is finished
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(startSignUpActivity());
        binding.buttonSignIn.setOnClickListener(signIn());
    }

    @NonNull
    private View.OnClickListener startSignUpActivity() {
        return v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }

    @NonNull
    private View.OnClickListener signIn() {
        return v -> {
            if (isValidSignInDetails()) processSignIn();
        };
    }

    private void processSignIn() {
        loading(true);
        FirebaseFirestore database = getDatabase();
        database.collection(Constants.KEY_COLLECTION_USERS)
                // from the input email and password, we search in the database matching result
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                // then get the result
                .get()
                // at the end of listening event, put all the account information/settings into the preference manager
                // this preference manager will be shared through an XML, and MainActivity will from there extract information from it to display
                // using preference manager is a good way to send data across different class (similar to storing database locally into XML for sharing between classes. instead of other types of database)
                .addOnCompleteListener(completeSignIn());
    }

    @NonNull
    private OnCompleteListener<QuerySnapshot> completeSignIn() {
        return task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                DocumentSnapshot documentSnapshot = getDocumentSnapshot(task);
                addUserToSharedPreferences(documentSnapshot);
                startMainActivity();
            } else {
                loading(false);
                showToast("Unable to sign in");
            }
        };
    }

    @NonNull
    private FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private DocumentSnapshot getDocumentSnapshot(com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot> task) {
        return task.getResult().getDocuments().get(0);
    }

    private void addUserToSharedPreferences(DocumentSnapshot documentSnapshot) {
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * shorten the Toasting process
     * @param message toast message
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * we only check the validity of the input here (not checking correct or not)
     * later we will do it in the sign in function
     * @return true by default, else false if input invalid
     */
    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Invalid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Input Password");
            return false;
        }
        return true;
    }
}