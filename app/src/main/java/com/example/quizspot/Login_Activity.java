package com.example.quizspot;

import static android.content.ContentValues.TAG;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login_Activity extends AppCompatActivity {

    private EditText email, password;
    private Button loginB;
    private TextView forgotPassB, signupB;
    private FirebaseAuth mAuth;
    private Dialog progressDialog;
    private TextView dialogText;
    private ImageView g_signB;

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN= 104;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginB = findViewById(R.id.loginB);
        forgotPassB = findViewById(R.id.forgot_pass);
        signupB = findViewById(R.id.signupB);
        g_signB = findViewById(R.id.g_signB);

        progressDialog= new Dialog(Login_Activity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog. setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText= progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing...");

        mAuth= FirebaseAuth.getInstance();

        // config Google Sign In
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);


        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    login();
                }
            }
        });

        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login_Activity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    g_signB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            googleSignIn();

        }
    });

    }


    private boolean validateData(){

        if (email.getText().toString().isEmpty()){
            email.setError("Enter Email Address");
            return  false;
        }
        if (password.getText().toString().isEmpty()){
            password.setError("Enter Password");
            return false;

        }

        return true;

    }

    private void login(){
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login_Activity.this,"Login Success",Toast.LENGTH_SHORT).show();

                            DbQuery.loadData(new MyCompleteListener() {
                                @Override
                                public void OnSuccess() {
                                    progressDialog.dismiss();

                                    Intent intent = new Intent(Login_Activity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void OnFailure() {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login_Activity.this, "Something Went Wrong Please Try Again Shortly !",
                                            Toast.LENGTH_SHORT).show();

                                }
                            });



                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login_Activity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);


    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == RC_SIGN_IN){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    //Log.d(TAG,"fireBaseAuthWithGoogle:"+account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(Login_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Login_Activity.this,"Google Sign In Success...",Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                        DbQuery.createUserData(user.getEmail(), user.getDisplayName(), new MyCompleteListener() {
                            @Override
                            public void OnSuccess() {

                                DbQuery.loadData(new MyCompleteListener() {
                                    @Override
                                    public void OnSuccess() {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                                        startActivity(intent);
                                        Login_Activity.this.finish();

                                    }

                                    @Override
                                    public void OnFailure() {
                                        progressDialog.dismiss();
                                        Toast.makeText(Login_Activity.this, "Something Went Wrong Please Try Again Shortly !",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            @Override
                            public void OnFailure() {
                                progressDialog.dismiss();
                                Toast.makeText(Login_Activity.this, "Something Went Wrong Please Try Again Shortly !",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else{
                        DbQuery.loadData(new MyCompleteListener() {
                            @Override
                            public void OnSuccess() {
                                progressDialog.dismiss();

                                Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                                startActivity(intent);
                                Login_Activity.this.finish();
                            }

                            @Override
                            public void OnFailure() {
                                progressDialog.dismiss();
                                Toast.makeText(Login_Activity.this, "Something Went Wrong Please Try Again Shortly !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }else{
                    progressDialog.dismiss();
                    Toast.makeText(Login_Activity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}


