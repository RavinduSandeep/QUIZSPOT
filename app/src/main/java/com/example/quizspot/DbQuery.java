package com.example.quizspot;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quizspot.Models.CategoryModel;
import com.example.quizspot.Models.ProfileModel;
import com.example.quizspot.Models.QuestionModel;
import com.example.quizspot.Models.RankModel;
import com.example.quizspot.Models.TestModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static FirebaseFirestore g_firestore;
    public static List<CategoryModel> g_catList = new ArrayList<>();

    public static int g_selected_cat_index=0;

    public static List<TestModel> g_testList = new ArrayList<>();

    public static int g_selected_test_index=0;

    public static List<String> g_bmIdList = new ArrayList<>();

    public static List<QuestionModel>g_bookmarkList = new ArrayList<>();

    public static List<QuestionModel> g_quesList = new ArrayList<>();

    public static List<RankModel> g_usersList = new ArrayList<>();
    public static int g_usersCount = 0;
    public static boolean isMeOnTopList= false;

    public static ProfileModel myProfile = new ProfileModel("NA",null, null, 0);
    public static RankModel myPerformance = new RankModel("NULL",0,-1);

    public static final int NOT_VISITED =0;
    public static final int UNANSWERED =1;
    public static final int ANSWERED =2;
    public static final int REVIEW =3;

    static  int tmp;

    public static void createUserData(String email, String name, MyCompleteListener completeListener ){

        Map<String, Object> userData = new ArrayMap<>();

        userData.put("EMAIL_ID" , email);
        userData.put("NAME", name);
        userData.put("TOTAL_SCORE",0);
        userData.put("BOOKMARKS",0);

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();

        batch.set(userDoc,userData);

        DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        batch.update(countDoc, "COUNT", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        completeListener.OnSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        completeListener.OnFailure();

                    }
                });
    }

    //27.30
    public static void saveProfileData(String name, String phone, MyCompleteListener completeListener)
    {
        Map<String, Object> profileData = new ArrayMap<>();

        profileData.put("NAME", name);

        if (phone != null){
            profileData.put("PHONE",phone);

            g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
            .update(profileData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            myProfile.setName(name);

                            if(phone != null)
                                myProfile.setPhone(phone);

                            completeListener.OnSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            completeListener.OnFailure();
                        }
                    });
        }
    }


    public static void getUserData (MyCompleteListener completeListener){
        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myProfile.setName(documentSnapshot.getString("NAME"));
                myProfile.setEmail(documentSnapshot.getString("EMAIL_ID"));

                if(documentSnapshot.getString("PHONE")!= null)
                    myProfile.setPhone(documentSnapshot.getString("PHONE"));

                if(documentSnapshot.get("BOOKMARKS")!= null)
                    myProfile.setBookmarkCount(documentSnapshot.getLong("BOOKMARKS").intValue());

                myPerformance.setScore(documentSnapshot.getLong("TOTAL_SCORE").intValue());
                myPerformance.setName(documentSnapshot.getString("NAME"));

                completeListener.OnSuccess();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                completeListener.OnFailure();

            }
        });
    }

    //26
    public static void loadMyScores( MyCompleteListener completeListener){

        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        for( int i=0; i < g_testList.size(); i++)
                        {
                            int top =0;

                            if(documentSnapshot.get(g_testList.get(i).getTestId()) != null)
                            {
                                top =documentSnapshot.getLong(g_testList.get(i).getTestId()).intValue();
                            }
                            g_testList.get(i).setTopScore(top);
                        }
                            completeListener.OnSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.OnFailure();
                    }
                });
    }


    //29.34

    public static void loadBmIds(MyCompleteListener completeListener)
    {
        g_bmIdList.clear();

        g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("BOOKMARKS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int count= myProfile.getBookmarkCount();

                        for(int i=0; i < count; i++)
                        {
                            String bmID = documentSnapshot.getString("BM"+ String.valueOf(i+1)+ "_ID");
                            g_bmIdList.add(bmID);
                        }

                        completeListener.OnSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.OnFailure();
                    }
                });

    }


    public static void loadBookmarks(MyCompleteListener completeListener)
    {
        g_bookmarkList.clear();

         tmp = 0 ;

         if(g_bmIdList.size() == 0)
             completeListener.OnSuccess();

        for(int i=0; i < g_bmIdList.size(); i++)
        {
            String docID = g_bmIdList.get(i);

            g_firestore.collection("Questions").document(docID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists())
                            {
                                g_bookmarkList.add(new QuestionModel(
                                        documentSnapshot.getId(),
                                        documentSnapshot.getString("QUESTION"),
                                        documentSnapshot.getString("A"),
                                        documentSnapshot.getString("B"),
                                        documentSnapshot.getString("C"),
                                        documentSnapshot.getString("D"),
                                        documentSnapshot.getLong("ANSWER").intValue(),
                                        0,-1, false

                                ));
                            }

                            tmp++;

                            if (tmp == g_bmIdList.size())
                            {
                                completeListener.OnSuccess();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            completeListener.OnFailure();
                        }
                    });


        }
    }


    //31

    public static void getTopUsers (MyCompleteListener completeListener)
    {
        g_usersList.clear();

        String myUID = FirebaseAuth.getInstance().getUid();


        g_firestore.collection("USERS")
                .whereGreaterThan("TOTAL_SCORE", 0)
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        int rank = 1;
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            g_usersList.add(new RankModel(
                                    doc.getString("NAME"),
                                    doc.getLong("TOTAL_SCORE").intValue(),
                                    rank
                            ));

                            if(myUID.compareTo(doc.getId()) == 0)
                            {
                                isMeOnTopList = true;
                                myPerformance.setRank(rank);
                            }

                            rank++;
                        }
                        completeListener.OnSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        completeListener.OnFailure();
                    }
                });
    }

    public static void getUsersCount(MyCompleteListener completeListener)
    {
        g_firestore.collection("USERS").document("TOTAL_USERS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        g_usersCount =documentSnapshot.getLong("COUNT").intValue();

                        completeListener.OnSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.OnFailure();
                    }
                });
    }


    //25
    public static void saveResult(int score, MyCompleteListener myCompleteListener)
    {
        WriteBatch batch = g_firestore.batch();

        //Bookmarks 34
        Map<String, Object> bmData = new ArrayMap<>();

        for(int i=0; i < g_bmIdList.size(); i++)
        {
            bmData.put("BM" + String.valueOf(i+1) + "_ID", g_bmIdList.get(i));
        }

        DocumentReference bmDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                     .collection("USER_DATA").document("BOOKMARKS");

        batch.set(bmDoc, bmData);

        Log.i("info", "book mark");





        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getUid());

        Map<String, Object> userData = new ArrayMap<>();
        userData.put("TOTAL_SCORE", score);
        userData.put("BOOKMARKS",g_bmIdList.size());

        batch.update(userDoc ,userData);


        if (score > g_testList.get(g_selected_test_index).getTopScore())
        {
            DocumentReference scoreDoc =userDoc.collection("USER_DATA").document("MY_SCORE");

            Map<String, Object> testData = new ArrayMap<>();
            testData.put(g_testList.get(g_selected_test_index).getTestId(), score);

            batch.set(scoreDoc, testData , SetOptions.merge());
        }

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                if(score > g_testList.get(g_selected_test_index).getTopScore())g_testList.get(g_selected_test_index).setTopScore(score);

                myPerformance.setScore(score);

                myCompleteListener.OnSuccess();

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.OnFailure();
                    }
                });

    }


    public static void loadCategories(MyCompleteListener completeListener){
        g_catList.clear();
        Log.i("info", "localCategories");

        g_firestore.collection("QUIZ").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.i("info", "Success");


                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            docList.put((doc.getId()), doc);
                        }
                        QueryDocumentSnapshot catListDoc = docList.get("categories");

                        long catCount = catListDoc.getLong("COUNT");

                        for(int i=1; i <= catCount; i++){

                            String catID = catListDoc.getString("CAT"+ String.valueOf(i)+ "_ID");
                            QueryDocumentSnapshot catDoc = docList.get(catID);


                            int numOfTest = catDoc.getLong("NO_OF_TESTS").intValue();

                            String catName = catDoc.getString("NAME");

                            g_catList.add(new CategoryModel(catID, catName, numOfTest));

                        }

                        completeListener.OnSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("info", "Fail");

                        completeListener.OnFailure();

                    }
                });

    }

    public static void loadQuestions(MyCompleteListener completeListener){

        Log.i("info", "Load");

        g_quesList.clear();
        g_firestore.collection("Questions")
                .whereEqualTo("CATEGORY", g_catList.get(g_selected_cat_index).getDocID())
                .whereEqualTo("TEST", g_testList.get(g_selected_test_index).getTestId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Log.i("info", "Success");

                        for(DocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            boolean isBookmarked = false;

                            if(g_bmIdList.contains(doc.getId()))
                                isBookmarked = true;

                            g_quesList.add(new QuestionModel(
                                    doc.getId(),
                                    doc.getString("QUESTION"),
                                    doc.getString("A"),
                                    doc.getString("B"),
                                    doc.getString("C"),
                                    doc.getString("D"),
                                    doc.getLong("ANSWER").intValue(),
                                    -1,
                                    NOT_VISITED,
                                    isBookmarked

                            ));

                        }

                        completeListener.OnSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("info", "Fail");

                        completeListener.OnFailure();
                    }
                });

    }

    public static void loadTestData(final MyCompleteListener completeListener){
        g_testList.clear();
        g_firestore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocID())
                .collection("TESTS_LIST").document("TESTS_INFO").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int noOfTest= g_catList.get(g_selected_cat_index).getNoOftest();

                        for(int i=1; i<=noOfTest; i++){
                         g_testList.add(new TestModel(
                               documentSnapshot.getString("TEST"+ String.valueOf(i)+ "_ID"),0,
                                 documentSnapshot.getLong("TEST"+String.valueOf(i)+ "_TIME").intValue()
                         )) ;
                        }

                        completeListener.OnSuccess();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                     @Override
                    public void onFailure(@NonNull Exception e) {

                         completeListener.OnFailure();
            }
        });
    }

    public static void loadData(MyCompleteListener completeListener){
        loadCategories(new MyCompleteListener() {
            @Override
            public void OnSuccess() {
                getUserData(new MyCompleteListener() {
                    @Override
                    public void OnSuccess() {
                        getUsersCount(new MyCompleteListener() {
                            @Override
                            public void OnSuccess() {
                                loadBmIds(completeListener);
                            }

                            @Override
                            public void OnFailure() {
                                completeListener.OnFailure();

                            }
                        });

                    }

                    @Override
                    public void OnFailure() {
                        completeListener.OnFailure();
                    }
                });
            }

            @Override
            public void OnFailure() {
                completeListener.OnFailure();

            }
        });
    }

}
