package test.team.com.normallogintest1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = "TESTLOG";


    //선언
    Button ckUserBtn;
    Button logoutBtn;
    TextView userProfileTV;

    String username;
    String useremail;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //유저 프로필을 가져옴
        loadUserProfile();

        //찾기
        ckUserBtn = findViewById(R.id.ckUserBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        userProfileTV = findViewById(R.id.userProfileTV);

        //유저 정보 띄우기 버튼
        ckUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username != null){
                    userProfileTV.setText(username + ", " + useremail);
                }else {
                    userProfileTV.setText("유저 프로필 없음");
                }
            }
        });

        //로그아웃 버튼. 정보를 삭제하도록.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseProfile();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    //로그아웃 버튼을 누르면 SharedPreferences에서 정보를 지우는 메소드
    private void eraseProfile(){
//        sp = getSharedPreferences("userProfile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        username = sp.getString("username", "로그인 유저 없음");
        useremail = sp.getString("useremail", "로그인 유저 없음");
        Log.d(TAG, username+ ", "+useremail);
    }

    //SharedPrefereces에서 유저 정보를 가져오는 메소드
    private void loadUserProfile(){
        sp = getSharedPreferences("userProfile", Activity.MODE_PRIVATE);

        if(sp != null){
            username = sp.getString("username", "로그인 유저 없음");
            useremail = sp.getString("useremail", "로그인 유저 없음");
        }
    }
}
