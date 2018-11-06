package test.team.com.normallogintest1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmEmail;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import test.team.com.normallogintest1.Async.AsyncLogin;
import test.team.com.normallogintest1.DTO.MemberDTO;
import test.team.com.normallogintest1.streaming.UserClient;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener{

    //선언
    public static final String TAG = "TESTLOG";



//유효성 검사 S
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(message = "유효하지 않은 이메일입니다.")
    @Length(min = 5, max = 30, message = "이메일은 최소 5자,\n최대 30자로만 적어주세요.")
    EditText emailEdit;
    @ConfirmEmail(message = "이메일이 일치하지 않습니다.")
    EditText emailEditCk;
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, message = "비밀번호는 6자 이상, 알파벳 대·소문자,\n 숫자, 특수기호를 포함해야됩니다.")
    EditText psdEdit;
    @ConfirmPassword(message = "비밀번호가 일치하지 않습니다.")
    EditText psdEditCk;
    @NotEmpty(message = "별명을 입력해주세요.")
    @Length(min = 2, max = 20, message = "별명은 최소 2자, 최대 20자 입니다.")
    EditText nameEdit;
    @Checked(message = "이용약관 및 개인정보 보호정책에\n동의해 주세요.")
    CheckBox checkBox;
    Button submitBtn;
    private Validator validator;
//유효성 검사 E

//로그인
    EditText loginIdEdit;
    EditText loginPsdEdit;
    Button loginSubmitBtn;
//로그인 끝

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로그인 시작
        loginIdEdit = findViewById(R.id.loginIdEdit);
        loginPsdEdit = findViewById(R.id.loginPsdEdit);
        loginSubmitBtn = findViewById(R.id.loginSubmitBtn);
        //로그인 끝


        //로긴 버튼 이벤트 리스터
        loginSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeALogin();
            }
        });
        //로긴 끝


        //회원가입 시작
        //찾기
        emailEdit = findViewById(R.id.emailEdit);
        emailEditCk = findViewById(R.id.emailEditCk);
        psdEdit = findViewById(R.id.psdEdit);
        psdEditCk = findViewById(R.id.psdEditCk);
        nameEdit = findViewById(R.id.nameEdit);
        checkBox = findViewById(R.id.checkBox);
        submitBtn = findViewById(R.id.submitBtn);

        //회갑 유효성 검사
        validator = new Validator(this);
        validator.setValidationListener(this);
        //버튼 리스너
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //유효성 검사
                validator.validate();
            }
        });//버튼
        //회원가입 끝

    }//onCreate




    //로긴 메소드

    //SharedPreferrence에 로긴 유저 정보 저장
    private void saveUserProfile( String useremail, String username ){
        SharedPreferences sp = getSharedPreferences("user" +
                "Profile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString("useremail", useremail);
        editor.putString("username", username);
        editor.commit();
    }

    //AsyncLogin
    private class AsyncLogin4 extends AsyncTask<Call, Void, String> {

        public static final String TAG = "TESTLOG";
        MemberDTO dto;

        @Override
        protected String doInBackground(Call... params) {

            try{
                Call<ResponseBody> loginCall = params[0];
                Response<ResponseBody> response = loginCall.execute();

                if (response.body().contentLength() > 0) {
                    return response.body().string().trim();
                }else return null;

            }catch (Exception e){
                Log.e(TAG, "서버 응답 수신 실패 : " + e.getLocalizedMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "수신 값: " + s);

            switch (s == null ? -1 : 1){
                case -1:
                    Log.d(TAG, "없는 유저");
                    Snackbar.make(findViewById(R.id.mainLinear), "아이디 또는 비밀번호를 다시 확인해주세요!", Snackbar.LENGTH_LONG).show();
                    break;
                default:
                    dto = userInfo(s);
                    Log.d(TAG, "유저 정보 : "+ dto.getUsername()+ ", " + dto.getUseremail());
                    Toast.makeText(MainActivity.this, "환영합니다, " + dto.getUsername() + " 님!", Toast.LENGTH_SHORT).show();
                    saveUserProfile(dto.getUsername(), dto.getUseremail());
                    Intent toNext = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(toNext);
                    break;

            }
        }

        private MemberDTO userInfo(String s){
            Gson gson = new GsonBuilder().create();
            dto = gson.fromJson(s, MemberDTO.class);
            return dto;
        }
    }


    //네트워킹
    private void executeALogin(){
        byte[] byteJson = LoginDTOtoJSON();
        UserClient userClient = UserClient.retrofit.create(UserClient.class);
        final Call<ResponseBody> loginCall = userClient.isLoginValid(byteJson);
        try{
            new AsyncLogin4().execute(loginCall);
        }catch (Exception e){Log.e(TAG, "AsyncTask 작동 실패 : " + e.getLocalizedMessage());}

    }


    //이멜과 비번만 담은 DTO를 JSON으로 파싱
    private byte[] LoginDTOtoJSON(){
        MemberDTO dto = new MemberDTO(loginIdEdit.getText().toString(), loginPsdEdit.getText().toString());
        Gson gson = new GsonBuilder().create();
        byte[] byteJson = null;
        try{
            String parsedDTO = gson.toJson(dto);
            Log.d(TAG, "로긴 DTO 파싱 전 : " + parsedDTO);
            byteJson = parsedDTO.getBytes("utf-8");
            Log.d(TAG, "로긴 DTO 파싱 결과 : " + byteJson);

        }catch (Exception e){
            Log.e(TAG, "파싱 실패");
        }

        return byteJson;

    }

    //로긴 메소드 끝



    //회원가입 시작
    //유효할 때
    @Override
    public void onValidationSucceeded() {
        Log.d(TAG, "유효성 검사 통과");

        //중복검사 및 일반회원가입
        byte[] dtoJson = DTOtoJSON();
        UserClient userClient = UserClient.retrofit.create(UserClient.class);

        Call<ResponseBody> call = userClient.sendEmail(dtoJson);
        try {

            new AEmailCkz().execute(call);


        } catch (Exception e) {            Log.e(TAG, "통신 실패 : " + e.getLocalizedMessage());        }

    }

    //DTO > JSON
    public byte[] DTOtoJSON(){
        String testEmail = emailEdit.getText().toString();
        String testPsd = psdEdit.getText().toString();
        String testName = nameEdit.getText().toString();
        MemberDTO dto = new MemberDTO(testEmail, testPsd, testName);

        Gson gson = new GsonBuilder().create();
        byte[] byteJson = null;
        try{

            byteJson = gson.toJson(dto).getBytes("utf-8");
            Log.d(TAG, "파싱 결과 : "+ gson.toJson(dto));
            Log.d(TAG, "byte코드로 인코딩 결과 : "+ byteJson);


        }catch (Exception e){Log.d(TAG, "변환이 안되..");}

        return byteJson;
    }

    //유효하지 않을 때
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    //AsyncTask
    private class AEmailCkz extends AsyncTask<Call, Void, String> {

        //Retrofit의 작업은 비동기 네트워킹이고, 안드로이드는 activity의 main Thread에서 비동기 네트워킹을 금지하고 있다.
        //따라서 결과값을 추출하려면 다른 쓰레드에서 받아와야 한다.

        @Override
        protected String doInBackground(Call... params) {
            try {
                Call<ResponseBody> call = params[0];
                Response<ResponseBody> response = call.execute();

                //body에 수신값이 있음. ResponseBody는 반드시 종료되는 시점이 필요하다. string()도 종료 중 하나.
                return response.body().string().trim();
                //이 후 response는 종료되어 더 이상 사용불가.

            } catch (Exception e) {
                Log.e(TAG, "수신 실패 : " + e.getLocalizedMessage());
                return null;
            }
        }
        //onPostExecute()는 doInBackground()로부터 결과값을 받아 UI Thread에서 작동한다.
        //여기서 Snackbar 메세지를 띄운다.
        @Override
        protected void onPostExecute(final String result) {
            Log.d(TAG, "수신값 : "+ result);

            //switch
            switch (result!=null ? Integer.parseInt(result) : 0){
                case 1:
                    Toast.makeText(MainActivity.this, "회원가입 축하", Toast.LENGTH_LONG).show();
                    Intent goMain2 = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(goMain2);
                    break;
                case -1:
                    Snackbar.make(findViewById(R.id.mainLinear), "중복된 이메일", Snackbar.LENGTH_LONG).show();
                    break;
                default:
                    Snackbar.make(findViewById(R.id.mainLinear), "서버 응답이 없습니다", Snackbar.LENGTH_LONG).show();
                    break;
            }//switch
        }//Post
    }//AsyncTask

    //자원회수
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AEmailCkz aEmailCkz = new AEmailCkz();
        if (aEmailCkz.getStatus() == AsyncTask.Status.RUNNING)aEmailCkz.cancel(true);

        //앱이 꺼질 때 자동으로 로그아웃되도록.
        SharedPreferences sp = getSharedPreferences("userProfile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

    }

    //회원가입 끝

}//c




