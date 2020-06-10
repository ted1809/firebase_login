package com.example.login.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login.Info.memberinfo;
import com.example.login.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    memberinfo sendmember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(signUpActivity.class);
        }else{
            Intent intent = getIntent();
            memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");

            if(memberRef == null) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                memberinfo member = dataSnapshot.getValue(memberinfo.class);
                                if (member == null) {
                                    Log.d(TAG, "No such document");
                                    startActivity(memberActivity.class);
                                } else
                                    sendpack(member);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "get failed ");
                            }
                        });
            }else{
                sendpack(memberRef);
            }
        }

        findViewById(R.id.logout).setOnClickListener(onClickListener);
        findViewById(R.id.documentButton).setOnClickListener(onClickListener);
        findViewById(R.id.memberConfirmButton).setOnClickListener(onClickListener);
        findViewById(R.id.logLookupButton).setOnClickListener(onClickListener);

    }

    private void sendpack(memberinfo memberinfo){
        sendmember = new memberinfo(memberinfo);
        Log.d(TAG, memberinfo.getName());
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch ((v.getId())){
                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(signUpActivity.class);
                    finish();
                    break;
                case R.id.documentButton:
                    startDActivity(selectdocumentActivity.class);
                    break;
                case R.id.memberConfirmButton:
                    startDActivity(userInfoActivity.class);
                    break;
                case R.id.logLookupButton:
                    startDActivity(logLookupActivity.class);
                    break;
            }
        }
    };

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }

    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish();
        }
    }

    private void startDActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.putExtra("memberRef", sendmember);
        startActivity(intent);
    }
}