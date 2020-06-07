package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class memberActivity extends AppCompatActivity {
    private static final String TAG = "memberActivity";
    private DatabaseReference mDatabase;
    memberinfo sendmember;

    Spinner boxspinner;
    Spinner datespinner;
    ArrayAdapter<String> boxAdapter;
    ArrayAdapter<String> dateAdapter;

    ArrayList<String> lockerID = new ArrayList<>();
    String lockerIDD = null;

    SimpleDateFormat td = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final ArrayList<String> date = new ArrayList<>();
        date.clear();
        date.add("1개월");
        date.add("3개월");
        date.add("6개월");
        date.add("1학기(4개월)");

        final ArrayList<String> box = new ArrayList<>();
        box.clear();
        /*mDatabase.child("locker").orderByChild("userID").equalTo("none").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String a = snapshot.getKey();
                            box.add(a);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });*/

        boxspinner = (Spinner)findViewById(R.id.boxSpinner);
        datespinner = (Spinner)findViewById(R.id.dateSpinner);

        mDatabase.child("locker").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            lockerinfo lockerinfo = snapshot.getValue(lockerinfo.class);
                            if(lockerinfo.getUserID().equals("none")) {
                                lockerID.add(snapshot.getKey());
                                box.add(lockerinfo.getLocal());

                                boxAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,box);

                                boxspinner.setAdapter(boxAdapter);

                                boxspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        String boxnum = boxspinner.getSelectedItem().toString();
                                        if(boxnum != "값을 선택해주세요."){
                                            startToast(box.get(i)+" 선택되었습니다.");
                                            lockerIDD = lockerID.get(i-1);
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        cal.setTime(new Date());
        startToast(td.format(cal.getTime()));

        box.add("값을 선택해주세요.");

        findViewById(R.id.checkButton).setOnClickListener(onClickLitsener);

        dateAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,date);

        datespinner.setAdapter(dateAdapter);

        datespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startToast(date.get(i)+" 선택되었습니다.");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }


    View.OnClickListener onClickLitsener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    Log.e("클릭", "클릭");
                    String boxnum = boxspinner.getSelectedItem().toString();
                    if(boxnum != "값을 선택해주세요."){
                        profileUpdate();
                        finish();
                    }else{
                        startToast("사물함을 선택해주세요.");
                    }
                    break;
            }
        }
    };

    private void profileUpdate(){
        String name = ((EditText)findViewById(R.id.nameeditText)).getText().toString();
        String phoneNumber = ((EditText)findViewById(R.id.PhoneEditText)).getText().toString();
        String Local = boxspinner.getSelectedItem().toString();
        String Pdate = null;

        switch (datespinner.getSelectedItem().toString()){
            case "1개월":
                cal.add(Calendar.MONTH,1);
                Pdate = td.format(cal.getTime());
            case "3개월":
                cal.add(Calendar.MONTH,3);
                Pdate = td.format(cal.getTime());
            case "6개월":
                cal.add(Calendar.MONTH,6);
                Pdate = td.format(cal.getTime());
            case "1학기(4개월)":
                cal.add(Calendar.MONTH,4);
                Pdate = td.format(cal.getTime());
        }

        if(name.length() > 0 && Local.length() > 0 && Pdate.length() != 0 && phoneNumber.length() > 0){
            /*for(String temp : box){
                if(temp == boxnum){
                    memberinfo memberref = new memberinfo(name, boxnum, Pdate, phoneNumber,false);
                    sendpack(memberref);
                    startDActivity(memberInsertActivity.class);
                }
            }*/

            memberinfo memberref = new memberinfo(name, Local, lockerIDD, Pdate, phoneNumber,false);
            sendpack(memberref);
            startDActivity(memberInsertActivity.class);
            /*if (user != null) {
                db.collection("users").document(user.getEmail()).set(memberinfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("회원정보 등록을 성공하였습니다.");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("회원정보 등록 실패");
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
            mDatabase.child("users").child(user.getUid()).setValue(memberinfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("회원정보 등록을 성공하였습니다.");
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    startToast("회원정보 등록 실패");
                    Log.w(TAG, "Error adding document", e);
                }
            });*/
        }else{
            startToast("회원정보를 입력해주세요.");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void sendpack(memberinfo memberinfo){
        sendmember = new memberinfo(memberinfo);
        Log.d(TAG, memberinfo.getName());
    }

    private String get_locker_Local(lockerinfo lockerinfo){
        return lockerinfo.getLocal();
    }

    private void startDActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.putExtra("memberRef", sendmember);
        startActivity(intent);
    }
}

