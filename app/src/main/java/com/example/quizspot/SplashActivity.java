package com.example.quizspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash);

        logo= findViewById(R.id.imageView2);

        Animation anim= AnimationUtils.loadAnimation(this, R.anim.myanim);
        logo.setAnimation(anim);

        mAuth= FirebaseAuth.getInstance();

        DbQuery.g_firestore = FirebaseFirestore.getInstance();

        new Thread(){

            @Override
            public void run()
            {
                try {
                    sleep(3000);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                if(mAuth.getCurrentUser() != null){

                    DbQuery.loadData(new MyCompleteListener() {
                        @Override
                        public void OnSuccess() {
                            Intent intent =new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();

                        }

                        @Override
                        public void OnFailure() {
                            Toast.makeText(SplashActivity.this, "Something Went Wrong ! Please Try Again Shortly !", Toast.LENGTH_SHORT).show();
                        }
                    });


                }else{
                    Intent intent =new Intent(SplashActivity.this, Login_Activity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }



            }
        }.start();
    }
}