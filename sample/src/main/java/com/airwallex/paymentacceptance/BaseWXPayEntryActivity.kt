package com.airwallex.paymentacceptance

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

abstract class BaseWXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private lateinit var weChatApi: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weChatApi = WXAPIFactory.createWXAPI(this, Settings.weChatAppId, true)
        weChatApi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        if (resp is PayResp) {
            WXPay.instance.onResp(resp.errCode, resp.errStr)
        }
        finish()
    }

    override fun onReq(req: BaseReq?) {
    }
}
