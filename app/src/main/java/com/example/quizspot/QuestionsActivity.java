package com.example.quizspot;

import static com.example.quizspot.DbQuery.g_catList;
import static com.example.quizspot.DbQuery.g_quesList;
import static com.example.quizspot.DbQuery.g_selected_cat_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView questionView;
    private TextView tvQuesID, timeTV, catNameTV;
    private Button submitB, markB, clearSelB;
    private ImageButton prevQuesB, nextQuesB;
    private ImageView quesList;
    private int quesID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        init();

        QuestionAdapter quesAdapter = new QuestionAdapter(g_quesList);
        questionView.setAdapter(quesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionView.setLayoutManager(layoutManager);

        setSnapHelper();

        setClickListener();

    }

    private void init(){

        questionView = findViewById(R.id.question_view);
        tvQuesID = findViewById(R.id.tv_quesID);
        timeTV = findViewById(R.id.tv_timer);
        catNameTV = findViewById(R.id.qa_catName);
        submitB = findViewById(R.id.submitB);
        markB = findViewById(R.id.markB);
        clearSelB = findViewById(R.id.clear_selB);
        prevQuesB = findViewById(R.id.prev_quesB);
        nextQuesB = findViewById(R.id.next_quesB);
        quesList  = findViewById(R.id.ques_list_gridB);

        quesID = 0;

        tvQuesID.setText("1/"+ String.valueOf(g_quesList.size()));
        catNameTV.setText(g_catList.get(g_selected_cat_index).getName());


    }

    private void setSnapHelper(){

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionView);

        questionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quesID = recyclerView.getLayoutManager().getPosition(view);

                tvQuesID.setText(String.valueOf(quesID + 1)+ "/" + String.valueOf(g_quesList.size()));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    private void setClickListener(){
        prevQuesB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (quesID > 0){
                    questionView.smoothScrollToPosition(quesID-1);
                }

            }
        });

        nextQuesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}