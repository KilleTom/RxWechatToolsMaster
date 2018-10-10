package cn.ypz.com.rxwechattoolsmaster.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.ypz.com.rxwechattoolsmaster.R
import cn.ypz.com.rxwechattoolsmaster.WeChatTools
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onResp(p0: BaseResp?)  = when (p0!!.type) {
        ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> {
            finish()
        }
        ConstantsAPI.COMMAND_SENDAUTH-> when(p0.errCode){
            0->{
                try {
                    WeChatTools.getWeChatTools().login(p0)
                } catch (e: Exception) {
                    Log.i("ypz",e.message)
                }
                finish()
            }
            -4->showErrorMessage("用户拒绝授权登录")
            -2->showErrorMessage("用户取消登录")
            else ->showErrorMessage("........")
        }

        else -> showErrorMessage(p0.errStr)
    }
    override fun onReq(p0: BaseReq?){
        Log.i("ypz",p0.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wxentry)
        WeChatTools.getWeChatTools().iwaxapiHandleIntent(intent,this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WeChatTools.getWeChatTools().iwaxapiHandleIntent(intent,this)
    }

    private fun showErrorMessage(message:String){
        Log.i("ypz",message)
        WeChatTools.getWeChatTools().iwaxapiHandleIntent(intent,this)
        Log.i("ypz",message)
        finish()
    }
}

