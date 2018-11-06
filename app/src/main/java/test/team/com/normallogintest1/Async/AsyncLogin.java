package test.team.com.normallogintest1.Async;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import test.team.com.normallogintest1.DTO.MemberDTO;

public class AsyncLogin extends AsyncTask<Call, Void, String> {

    public static final String TAG = "TESTLOG";
    MemberDTO dto;

    @Override
    protected String doInBackground(Call... parmas) {

        try{
            Call<ResponseBody> loginCall = parmas[0];
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
                break;
            default:
                dto = userInfo(s);
                Log.d(TAG, "유저 정보 : "+ dto.getUsername()+ ", " + dto.getUseremail());
                break;

        }
    }

    private MemberDTO userInfo(String s){
        Gson gson = new GsonBuilder().create();
        dto = gson.fromJson(s, MemberDTO.class);
        return dto;
    }
}
