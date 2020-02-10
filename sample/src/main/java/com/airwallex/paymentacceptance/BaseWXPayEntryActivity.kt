package com.airwallex.paymentacceptance

import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

abstract class BaseWXPayEntryActivity : PaymentBaseActivity(), IWXAPIEventHandler {
    private lateinit var weChatApi: IWXAPI

    override val inPaymentFlow: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weChatApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true)
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
