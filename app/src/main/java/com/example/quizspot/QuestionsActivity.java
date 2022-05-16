package com.example.quizspot;

import static com.example.quizspot.DbQuery.ANSWERED;
import static com.example.quizspot.DbQuery.NOT_VISITED;
import static com.example.quizspot.DbQuery.REVIEW;
import static com.example.quizspot.DbQuery.UNANSWERED;
import static com.example.quizspot.DbQuery.g_catList;
import static com.example.quizspot.DbQuery.g_quesList;
import static com.example.quizspot.DbQuery.g_selected_cat_index;
import static com.example.quizspot.DbQuery.g_selected_test_index;
import static com.example.quizspot.DbQuery.g_testList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView questionView;
    private TextView tvQuesID, timeTV, catNameTV;
    private Button submitB, markB, clearSelB;
    private ImageButton prevQuesB, nextQuesB;
    private ImageView quesListB;
    private int quesID;
    QuestionAdapter quesAdapter;
    private DrawerLayout drawer;
    private ImageButton drawerCloseB;
    private GridView quesListGV;
    private ImageView markImage;
    private QuestionGridAdapter gridAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_list_layout);

        init();

        quesAdapter = new QuestionAdapter(g_quesList);
        questionView.setAdapter(quesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionView.setLayoutManager(layoutManager);

        gridAdapter = new QuestionGridAdapter(this, g_quesList.size());
        quesListGV.setAdapter(gridAdapter);

        setSnapHelper();

        setClickListener();

        startTimer();

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
        quesListB  = findViewById(R.id.ques_list_gridB);
        drawer = findViewById(R.id.drawer_layout);
        markImage = findViewById(R.id.mark_image);
        quesListGV = findViewById(R.id.ques_list_gv);

        drawerCloseB = findViewById(R.id.drawerCloseB);

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

                if(g_quesList.get(quesID).getStatus() == NOT_VISITED){
                    g_quesList.get(quesID).setStatus(UNANSWERED);
                }

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
            public void onClick(View view) {

                if (quesID< g_quesList.size()-1)
                {
                    questionView.smoothScrollToPosition(quesID+1);
                }

            }
        });

        clearSelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g_quesList.get(quesID).setSelectedAns(-1);
                quesAdapter.notifyDataSetChanged();


            }
        });

        quesListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ! drawer.isDrawerOpen(GravityCompat.END) ){
                    gridAdapter.notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        drawerCloseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END);
                }

            }
        });

        markB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (markImage.getVisibility() != View.VISIBLE)
                 {
                     markImage.setVisibility(View.VISIBLE);
                     g_quesList.get(quesID).setStatus(REVIEW);
                 }
                 else
                 {
                    markImage.setVisibility(View.GONE);

                    if ( g_quesList.get(quesID).getSelectedAns() != -1)
                    {
                        g_quesList.get(quesID).setStatus(ANSWERED);
                    }else
                    {
                        g_quesList.get(quesID).setStatus(UNANSWERED);
                    }
                 }
            }
        });
    }

    public void goToQuestion( int position)
    {
        questionView.smoothScrollToPosition(position);

        if( drawer.isDrawerOpen(GravityCompat.END))
            drawer.closeDrawer(GravityCompat.END);

    }



    private void startTimer(){

        long totalTime = g_testList.get(g_selected_test_index).getTime()*60*1000;

        CountDownTimer timer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long remainingTime) {
               String time = String.format("%2d:%2d min" ,
                       TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                       TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))

                       );

               timeTV.setText(time);

            }

            @Override
            public void onFinish() {


            }
        };

        timer.start();
    }
}