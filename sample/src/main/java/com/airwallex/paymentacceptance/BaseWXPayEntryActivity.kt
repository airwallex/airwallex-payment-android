package com.airwallex.paymentacceptance

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.support.v7.app.AppCompatActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

abstract class BaseWXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {

    companion object {
        const val TAG = "BaseWXPayEntryActivity"
    }

    private val weChatApi: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(this, Settings.weChatAppId, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weChatApi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        Log.d(TAG, "onResp")
        if (resp is PayResp) {
            WXPay.instance.onResp(resp.errCode, resp.errStr)
        }
        finish()
    }

    override fun onReq(req: BaseReq?) {
        Log.d(TAG, "onReq")
        Toast.makeText(this, "Request type: " + req?.type, Toast.LENGTH_SHORT).show()
    }
}
