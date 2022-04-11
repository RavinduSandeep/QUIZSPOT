package com.example.quizspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, email, password, confirmPass;
    private Button signUpB;
    private ImageView backB;
    private FirebaseAuth mAuth;
    private String emailStr, passStr, confirmPassStr, nameStr;
    private Dialog progressDialog;
    private TextView dialogText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name= findViewById(R.id.username);
        email= findViewById(R.id.email);
        password= findViewById(R.id.password);
        confirmPass= findViewById(R.id.confirm_pass);
        signUpB= findViewById(R.id.signupB);
        backB= findViewById(R.id.backB);

        progressDialog= new Dialog(SignUpActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog. setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText= progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Registering User...");

        mAuth = FirebaseAuth.getInstance();

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()){
                    signUpNewUser();
                }

            }
        });
    }

    private boolean validate(){
        nameStr = name.getText().toString().trim();
        passStr = password.getText().toString().trim();
        emailStr = email.getText().toString().trim();
        confirmPassStr = confirmPass.getText().toString().trim();

        if (nameStr.isEmpty()){
            name.setError("Enter Your Name");
            return false;
        }
        if (emailStr.isEmpty()){
            email.setError("Enter Your Email Address");
            return false;
        }
        if (passStr.isEmpty()){
            password.setError("Enter Password");
            return false;
        }
        if (confirmPassStr.isEmpty()){
            confirmPass.setError("Enter Password");
            return false;
        }
        if (passStr.compareTo(confirmPassStr)!=0){
            Toast.makeText(SignUpActivity.this, "Password and Confirm Password Should be Same !", Toast.LENGTH_SHORT).show();
                    return false;
        }

        return  true;

    }


    private void signUpNewUser(){

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, task ->  {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Sign Up Successful",Toast.LENGTH_SHORT).show();

                            DbQuery.createUserData(emailStr, nameStr, new MyCompleteListener() {
                                @Override
                                public void OnSuccess() {

                                    DbQuery.loadData(new MyCompleteListener() {
                                        @Override
                                        public void OnSuccess() {
                                            progressDialog.dismiss();
                                            Intent intent= new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            SignUpActivity.this.finish();
                                        }

                                        @Override
                                        public void OnFailure() {
                                            Toast.makeText(SignUpActivity.this, "Something Went Wrong ! Please Try Again Shortly !", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });


                                }

                                @Override
                                public void OnFailure() {
                                    Toast.makeText(SignUpActivity.this, "Something Went Wrong ! Please Try Again Shortly !", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                });
    }
}