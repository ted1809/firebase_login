package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class logLookupActivity extends AppCompatActivity {
    private static final String TAG = "logLookupActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<logInfo> logList;

    RawLogInfo Raw = new RawLogInfo();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
        Log.d(TAG, " => " + get_member_lockerID(memberRef));
        mDatabase = FirebaseDatabase.getInstance().getReference();


        recyclerView = findViewById(R.id.logRecyclerView);
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager((this));
        recyclerView.setLayoutManager(layoutManager);
        logList = new ArrayList<>();

        logList.clear();

        /*db.collection("inOutList")
                .whereEqualTo("boxnum", get_member_boxnum(memberRef))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                DocumentReference docRef = db.collection("inOutList").document(document.getId());
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        RawLogInfo RawLog = documentSnapshot.toObject(RawLogInfo.class);
                                        if (RawLog != null) {
                                            logInfo log = new logInfo(document.getId(),get_log_goodsName(RawLog), get_log_inOut(RawLog));
                                            Log.d(TAG, " => " + get_log_logTime(log));
                                            logList.add(log);
                                            adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });*/

        mDatabase.child("inOutList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            RawLogInfo RawLog = snapshot.getValue(RawLogInfo.class);
                            if (RawLog != null) {
                                if(get_log_lockerID(RawLog).equals(get_member_lockerID(memberRef))) {
                                    logInfo log = new logInfo(snapshot.getKey(), get_log_ID(RawLog), get_log_inOut(RawLog));
                                    Log.d(TAG, " => " + get_log_logTime(log));
                                    logList.add(log);
                                }
                            }else{
                                startToast("아직 아무 기록도 없어요.");
                                finish();
                            }
                        }
                        /*if(logList != null){
                            Log.d(TAG, "SUCCESS");
                            for(int j = 0; j < logList.size();j++ ) {
                                final int finalJ = j;
                                mDatabase.child("locker").child(get_member_lockerID(memberRef)).child("goods")
                                        .child(get_log_ContentsRFID(logList.get(j)))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                logInfo log = logList.get(finalJ);
                                                log = set_log_goodsName(log, (String)dataSnapshot.getValue());
                                                logList.set(finalJ, log);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }*/
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });

        adapter = new logAdapter(logList, this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "SUCCESS");
                startDActivity(logDetailActivity.class, logList.get(position), memberRef);
            }
        }));

    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private logLookupActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final logLookupActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }




    private String get_member_lockerID(memberinfo memberinfo){
        return memberinfo.getLockerID();
    }

    private String get_log_ID(RawLogInfo rawLogInfo){
        return rawLogInfo.getID();
    }

    private String get_log_lockerID(RawLogInfo rawLogInfo){
        return rawLogInfo.getLockerID();
    }

    private boolean get_log_inOut(RawLogInfo rawLogInfo){
        return rawLogInfo.isInOut();
    }

    private String get_log_logTime(logInfo loginfo){
        return loginfo.getLogTime();
    }

    private void set_time(logInfo log, String a){
        log.setLogTime(a);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startDActivity(Class c, logInfo log, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.putExtra("log", log);
        intent.putExtra("memberRef", memberinfo);
        startActivity(intent);
    }
}

