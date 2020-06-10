package com.example.login.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signUpActivity extends AppCompatActivity {
    private static final String TAG = "signUpActivity";
    private FirebaseAuth mAuth;                                 // 파이어베이스 이메일로그인 객체
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);   //이미 로그인이 되어있는 상태면 바로 메인으로 넘어감
            startActivity(intent);
            finish();
        }

        findViewById(R.id.checkButton).setOnClickListener(onClickLitsener);
        findViewById(R.id.getLoginbutton).setOnClickListener(onClickLitsener);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());     //뒤로가기 버튼 누르면 앱 종료되게 하는 함수
        System.exit(1);
    }


    View.OnClickListener onClickLitsener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    Log.e("클릭", "클릭");
                    signUp();
                    break;
                case R.id.getLoginbutton:
                    startActivity(loginActivity.class);
                    break;
            }
        }
    };


    private void signUp(){
        String email = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.phoneEditText)).getText().toString();
        String passworCheck = ((EditText)findViewById(R.id.passwordCheckeditText)).getText().toString();  //이메일, 비밀번호, 확인용비밀번호 입력받음

        if(email.length() > 0 && password.length() > 0 && passworCheck.length() > 0){                   //3개의 string이 모두 입력되어야 버튼입력이 가능
            if(password.equals(passworCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {                                              //이메일 가입 성공시
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();                         //유저 객체 생성
                                    startToast("회원가입을 성공했습니다.");
                                    startActivity(MainActivity.class);                                  //메인액티비티로 넘어감
                                    //UI
                                } else {
                                    if(task.getException() != null){
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        startToast(task.getException().toString());
                                    }
                                }
                            }
                        });
            } else{
                startToast("비밀번호가 일치하지 않습니다.");
            }
        }else{
            startToast("이메일 또는 비밀번호를 입력해주세요");
        }


    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
