package cn.ypz.com.rxwechattoolsmaster;

import android.app.Application;

import cn.ypz.com.rxwechatclient.RxWeChatClient;

/**
 * Created by 易庞宙 on 2018 2018/10/2 21:37
 * email: 1986545332@qq.com
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxWeChatClient.getWeChatTools().initApplication(this, "", "","");
//        RxWeChatClient.getWeChatTools().addPayCallback(new RxWeChatTools.PayCallback() {
//            @Override
//            public void queryServerPurchaseOrder() {
//
//            }
//
//            @Override
//            public void notFinshServerPurchaseOrder() {
//
//            }
//
//            @Override
//            public void cancleServerPurchaseOrder() {
//
//            }
//        });
    }
}
