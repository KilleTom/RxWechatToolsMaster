package cn.ypz.com.rxwechattoolsmaster.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.ypz.com.rxwechatclient.RxWeChatClient
import cn.ypz.com.rxwechattoolsmaster.R
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler


class WXPayEntryActivity : Activity(), IWXAPIEventHandler {
    override fun onResp(p0: BaseResp?) {
        try {
            Log.i("ypz", "onPayFinish, errCode = " + p0!!.errCode)
           RxWeChatClient.getWeChatTools().payCall(p0)
        } catch (e: Exception) {
            Log.i("ypz","onResp"+e.message)
        }finally {
            finish()
        }
    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun onNewIntent(intent: Intent?) {
        try {
            super.onNewIntent(intent)
            setIntent(intent!!)
           RxWeChatClient.getWeChatTools().iwaxapiHandleIntent(intent,this)
        } catch (e: Exception) {
            Log.i("ypz",e.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wxpay_entry)
        try {
            RxWeChatClient.getWeChatTools().iwaxapiHandleIntent(intent,this)
        } catch (e: Exception) {
            Log.i("ypz","onCreate"+e.message)
        }
    }
}
