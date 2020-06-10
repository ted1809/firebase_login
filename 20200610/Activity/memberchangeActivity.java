package com.example.login.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.Info.memberinfo;
import com.example.login.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class memberchangeActivity extends AppCompatActivity {
    private static final String TAG = "memberchangeActivity";
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_change);

        mAuth = FirebaseAuth.getInstance();                                         //유저 객체를 받아온다

        findViewById(R.id.checkButton).setOnClickListener(onClickLitsener);
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    View.OnClickListener onClickLitsener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    Log.e("클릭", "클릭");
                    memberChange();
                    startToast("정보변경 완료");
                    break;
            }
        }
    };

    private void memberChange(){
        String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String phone = ((EditText)findViewById(R.id.phoneEditText)).getText().toString();

        if(name.length() > 0 && phone.length() > 0){
            Intent intent = getIntent();
            final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");

            mDatabase = FirebaseDatabase.getInstance().getReference();

            setValue(memberRef, name, phone);

            mDatabase.child("users").child(user.getUid()).setValue(memberRef)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                             startToast("회원 정보 변경에 성공하였습니다");
                             startActivity(MainActivity.class, memberRef);
                             finish();
                         }
                     })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("정보 등록 실패");
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

        }else{
            startToast("변경할 이름과 전화번호를 입력해주세요");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startActivity(Class c, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.putExtra("memberRef", memberinfo);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setValue(memberinfo memberinfo, String a, String b){
        memberinfo.setName(a);
        memberinfo.setPhoneNumber(b);
    }
}
