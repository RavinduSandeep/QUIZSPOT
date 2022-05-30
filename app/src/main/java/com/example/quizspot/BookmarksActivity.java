package com.example.quizspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizspot.Adapters.AnswersAdapter;
import com.example.quizspot.Adapters.BookmarkAdapter;

public class BookmarksActivity extends AppCompatActivity {
    //This is bookmarks activity
    private RecyclerView questionView;
    private Dialog progressDialog;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);


        Toolbar toolbar= findViewById(R.id.ba_toolbar);
        questionView = findViewById(R.id.ba_recycler_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Saved Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog= new Dialog(BookmarksActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog. setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText= progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading...");
        progressDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        questionView.setLayoutManager(layoutManager);

        DbQuery.loadBookmarks(new MyCompleteListener() {
            @Override
            public void OnSuccess() {

                BookmarkAdapter adapter= new BookmarkAdapter(DbQuery.g_bookmarkList);
                questionView.setAdapter(adapter);

                progressDialog.dismiss();

            }

            @Override
            public void OnFailure() {

                progressDialog.dismiss();

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            BookmarksActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}