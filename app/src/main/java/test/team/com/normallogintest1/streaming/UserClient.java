package test.team.com.normallogintest1.streaming;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserClient {

    public static final String TESTURL = "http://192.168.0.110/exam/";  //  192.168.0.110     127.0.0.1

    @POST("logIn")
    Call<ResponseBody> isLoginValid(
            @Query("isMatch") byte[] idPsd
    );

    @POST("joinIn")
    Call<ResponseBody> sendEmail(
            @Query("isOk") byte[] email
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(TESTURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

}
