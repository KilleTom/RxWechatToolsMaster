package cn.ypz.com.rxwechatclient;

import cn.ypz.com.rxwechattools.WeChatUserInfo;
import cn.ypz.com.rxwechattools.WeChatLoginResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 易庞宙 on 2018 2018/10/10 18:47
 * email: 1986545332@qq.com
 */
public interface WeChatApi {
    @GET("oauth2/access_token")
    Call<WeChatLoginResult> wechatLoginResultCall(@Query("appid") String appid, @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grant_type) ;

    @GET("userinfo")
    Call<WeChatUserInfo> weChatUserInforCall(@Query("access_token") String accessToken, @Query("openid") String openId);
}
