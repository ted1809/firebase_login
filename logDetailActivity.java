package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class logDetailActivity extends AppCompatActivity {
    private static final String TAG = "logDetailActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);

        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
        logInfo log = (logInfo)intent.getSerializableExtra("log");

        TextView RFIDText = (TextView)findViewById(R.id.RFIDText);
        TextView timeText = (TextView)findViewById(R.id.timeText);

        TextView inOutText = (TextView)findViewById(R.id.inOutText);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("locker").child(get_member_lockerID(memberRef)).child("goods")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            documentInfo documentInfo = snapshot.getValue(documentInfo.class);
                            Log.d(TAG, "->"+ documentInfo.getGoodsName());

                            TextView goodsNameText = (TextView)findViewById(R.id.goodsNameText);
                            TextView detailText = (TextView)findViewById(R.id.detailText);

                            goodsNameText.setText("이름: "+documentInfo.getGoodsName());
                            detailText.setText("상세정보: "+documentInfo.getDetail());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        timeText.setText(rename_time(log));
        RFIDText.setText(log.getContentsRFID());

        if(log.getInOUT()) {
            if(log.getContentsRFID().equals("none")){
                inOutText.setText("열림");
                inOutText.setTextColor(Color.parseColor("#FF4444"));
            }else {
                inOutText.setText("출납");
                inOutText.setTextColor(Color.parseColor("#FF4444"));
            }
        }
        else {
            if(log.getContentsRFID().equals("none")){
                inOutText.setText("닫힘");
                inOutText.setTextColor(Color.parseColor("#009900"));
            }else {
                inOutText.setText("수납");
                inOutText.setTextColor(Color.parseColor("#009900"));
            }
        }
    }

    private String get_member_lockerID(memberinfo memberinfo){
        return memberinfo.getLockerID();
    }

    private String rename_time(logInfo log){
        SimpleDateFormat td = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat td2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date Cdate = null;
        try {
            Cdate = td.parse(log.getLogTime());
            cal.setTime(Cdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(Cdate != null)
            return td2.format(cal.getTime());
        else
            return log.getLogTime();
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

