package cn.ypz.com.rxwechattools;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 易庞宙 on 2018 2018/10/10 18:43
 * email: 1986545332@qq.com
 */
public class WeChatUserInfo {
    public WeChatUserInfo(String openid, String nickname, String sex, String province, String city, String country, String headimgurl, List<String> privilege, String unionid) {
        this.openid = openid;
        this.nickname = nickname;
        this.sex = sex;
        this.province = province;
        this.city = city;
        this.country = country;
        this.headimgurl = headimgurl;
        this.privilege = privilege;
        this.unionid = unionid;
    }

    @SerializedName("openid")
    private String openid;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("sex")
    private String sex;

    @SerializedName("province")
    private String province;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("headimgurl")
    private String headimgurl;

    @SerializedName("privilege")
    private List<String> privilege;

    @SerializedName("unionid")
    private String unionid;


    public String getOpenid() {
        return madeStringNotNull(openid);
    }

    public String getNickname() {
        return madeStringNotNull(nickname);
    }

    public String getSex() {
        return madeStringNotNull(sex);
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public List<String> getPrivilege() {
        return privilege;
    }

    public String getUnionid() {
        return unionid;
    }

    protected String madeStringNotNull(String bemake){
        return bemake==null?"":bemake;
    }
}
