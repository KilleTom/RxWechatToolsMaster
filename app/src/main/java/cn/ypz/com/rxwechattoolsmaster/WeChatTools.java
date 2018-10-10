package cn.ypz.com.rxwechattoolsmaster;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import java.util.concurrent.TimeUnit;

import cn.ypz.com.rxwechattools.RxWeChatTools;
import cn.ypz.com.rxwechattools.WeChatUserInfo;
import cn.ypz.com.rxwechattools.WeChatLoginResult;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 易庞宙 on 2018 2018/10/10 18:38
 * email: 1986545332@qq.com
 */
public class WeChatTools extends RxWeChatTools {
    private static WeChatTools weChatTools;
    private WeChatApi weChatApi;

    private Callback<WeChatLoginResult> weChatLoginResultCallback = new BaseCallBack<WeChatLoginResult>().callback(new NetCallBack<WeChatLoginResult>() {
        @Override
        public void callBackSuccessful(WeChatLoginResult wechatLoginResult) {
            getUserInfo(wechatLoginResult.getAccess_token(), wechatLoginResult.getOpenid());
        }

        @Override
        public void error(int code, String message) {
            loginError(code, message);
        }
    });

    private Callback<WeChatUserInfo> weChatUserInfoCallback = new BaseCallBack<WeChatUserInfo>().callback(new NetCallBack<WeChatUserInfo>() {
        @Override
        public void callBackSuccessful(WeChatUserInfo weChatUserInfo) {
            weChatUserMessageToServer(weChatUserInfo);
        }

        @Override
        public void error(int code, String message) {
            loginGetUserMessageError(code, message);
        }
    });

    /**
     * 这里写将获取到的微信用户信息回调上传至服务器或者App自己保存相应信息
     */
    protected void weChatUserMessageToServer(WeChatUserInfo weChatUserInfo) {

    }

    public static WeChatTools getWeChatTools() {
        if (weChatTools == null) {
            synchronized (WeChatTools.class) {
                weChatTools = new WeChatTools();
            }
        }
        return weChatTools;
    }

    private WeChatTools() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weixin.qq.com/sns/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        weChatApi = retrofit.create(WeChatApi.class);
        Log.i("ypz", "next_0");
    }

    @Override
    public void login(BaseResp resp) {
        Call<WeChatLoginResult> weChatLoginResultCall = weChatApi.wechatLoginResultCall(appId, secret, ((SendAuth.Resp) resp).code, grant_type);
        weChatLoginResultCall.enqueue(weChatLoginResultCallback);
    }

    private void getUserInfo(String access_token, String openid) {
        Call<WeChatUserInfo> weChatUserInfoCall = weChatApi.weChatUserInforCall(access_token, openid);
        Log.i("ypz", "getUserInfo" + weChatUserInfoCall.request().url().toString());
        weChatUserInfoCall.enqueue(weChatUserInfoCallback);
    }

    @Override
    public void pay(BaseResp baseResp) {

    }

    protected void loginError(int code, String errorMessage) {
        Log.e("WeChatTools", "loginError:\nloginErrorCode：" + code + "\nloginErrorMessage" + errorMessage);
    }

    protected void loginGetUserMessageError(int code, String errorMessage) {

    }

    protected class BaseCallBack<callbackClass> {
        Callback<callbackClass> callback(final NetCallBack<callbackClass> netCallBack) {
            return new Callback<callbackClass>() {
                @Override
                public void onResponse(Call<callbackClass> call, Response<callbackClass> response) {
                    if (response.isSuccessful() && (response.body() != null)) {
                        if (netCallBack != null)
                            netCallBack.callBackSuccessful(response.body());
                    } else {
                        if (netCallBack != null)
                            netCallBack.error(response.code(), response.message());
                    }
                }

                @Override
                public void onFailure(Call<callbackClass> call, Throwable t) {
                    if (netCallBack != null)
                        netCallBack.error(call.hashCode(), t.getMessage());
                    if (!call.isCanceled()) call.cancel();
                }
            };
        }
    }

    protected interface NetCallBack<C> {

        void callBackSuccessful(C c);

        void error(int code, String message);

    }
}
