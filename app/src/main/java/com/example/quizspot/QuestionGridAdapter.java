package com.example.quizspot;

import static com.example.quizspot.DbQuery.ANSWERED;
import static com.example.quizspot.DbQuery.NOT_VISITED;
import static com.example.quizspot.DbQuery.REVIEW;
import static com.example.quizspot.DbQuery.UNANSWERED;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class QuestionGridAdapter extends BaseAdapter {

    private int numOfQues;
    private Context context;

    public QuestionGridAdapter(Context context, int numOfQues) {
        this.numOfQues = numOfQues;
        this.context =context;
    }

    @Override
    public int getCount() {
        return numOfQues;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View myView;

        if(view == null){
            myView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_grid_item, viewGroup, false);

        }else{
            myView= view;
        }

        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof QuestionsActivity)
                    ((QuestionsActivity)context).goToQuestion(i);
            }
        });

        TextView quesTV = myView.findViewById(R.id.ques_num);
        quesTV.setText(String.valueOf(i+1));

        switch (DbQuery.g_quesList.get(i).getStatus())
        {
            case NOT_VISITED :
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.grey)));
                break;
            case UNANSWERED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.red)));
                break;
            case ANSWERED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.green)));
                break;
            case REVIEW:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.pink)));
                break;

            default:
                break;
        }

        return myView;
    }
}
