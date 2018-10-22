# RxWechatToolsMaster(微信集成开发库)
## How to used?
used way：https://jitpack.io/#KilleTom/RxWechatToolsMaster this web can telled your how many versions can provide to be used;
but if you don't want to chose , you can copy this to use.
### First
Pasue this message in the builde.gradle(project)file:
```gradle
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}

```
### Second
#### rxwechatclient or rxwechattools
##### rxwechatclient
this rxwechatclient is base on rxwechattools . It was finshed loginWeChat  and payWeChat voids ; you just to set the call to clieht. 
```gradle
	dependencies {
	        implementation 'com.github.KilleTom.RxWechatToolsMaster:rxwechatclient:v1.1_release'
	}
```
##### rxwechattools
this tools is abstract class, because login is a abstract void.You must be overwrite this void to finsh WeChat login.
If you don't need to login,you can create the util class to extends,just overwrite login void and make this do nothing.
```gradle
	dependencies {
	        implementation 'com.github.KilleTom.RxWechatToolsMaster:rxwechattools:v1.1_release'
	}
```
### End
#### rxwechatclient init
```Kotlin
//in you Application
        RxWeChatClient.getWeChatTools().initApplication(this,"appId","secret","grant_type");
//init WeChatLogin result call to server
        val loginCallBack = object : RxWeChatClient.WeChatLoginCallBack {
            override fun weChatUserTokenAndOpenidCall(access_token: String, openid: String) {

            }

            override fun weChatUserMessageToServer(weChatUserInfo: WeChatUserInfo) {

            }

            override fun loginError(code: Int, errorMessage: String) {

            }
        }
//set this call to client
        RxWeChatClient.getWeChatTools().setLoginCallBack(loginCallBack)
//when you don't need this call，you can remove
        RxWeChatClient.getWeChatTools().removeLoginCallBack(loginCallBack)
//init payCall
        val payCallback = object : RxWeChatTools.PayCallback {
            override fun queryServerPurchaseOrder() {

            }

            override fun notFinshServerPurchaseOrder() {

            }

            override fun cancleServerPurchaseOrder() {

            }
        }
//set this pay call to client
        RxWeChatClient.getWeChatTools().addPayCallback(payCallback)
//when you don't need this pay call，you can remove
        RxWeChatClient.getWeChatTools().removePayCallback(payCallback)
```
#### rxwechattools init
```java
//look like rxwechatclient
public class RxWeChatClient extends RxWeChatTools {
//just override this void
    @Override
    public void login(BaseResp resp) {
        Call<WeChatLoginResult> weChatLoginResultCall = weChatApi.wechatLoginResultCall(appId, secret, ((SendAuth.Resp) resp).code, grant_type);
        weChatLoginResultCall.enqueue(weChatLoginResultCallback);
    }
    //
  }
//other used look like rxwechatclient
```
