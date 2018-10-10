package cn.ypz.com.rxwechattools;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 易庞宙 on 2018 2018/10/10 18:40
 * email: 1986545332@qq.com
 */
public class WeChatLoginResult {
    @SerializedName("errmsg")
    private String errmsg;

    @SerializedName("errcode")
    private int errcode;

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("expires_in")
    private String expires_in;

    @SerializedName("refresh_token")
    private String refresh_token;

    @SerializedName("openid")
    private String openid;

    @SerializedName("scope")
    private String scope;

    @SerializedName("unionid")
    private String unionid;

    public String getErrmsg() {
        return errmsg;
    }

    public int getErrcode() {
        return errcode;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public String getScope() {
        return scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public WeChatLoginResult(String errmsg, int errcode, String access_token, String expires_in, String refresh_token, String openid, String scope, String unionid) {
        this.errmsg = errmsg;
        this.errcode = errcode;
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.openid = openid;
        this.scope = scope;
        this.unionid = unionid;
    }
}
