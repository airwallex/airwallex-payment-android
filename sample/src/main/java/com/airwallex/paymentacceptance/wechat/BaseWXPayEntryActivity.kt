package com.airwallex.paymentacceptance.wechat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        weChatApi = WXAPIFactory.createWXAPI(this,
            Constants.APP_ID, true)
        weChatApi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        if (resp is PayResp) {
            val message = when (resp.errCode) {
                0 -> "Payment successful!"
                -1 -> "Error during payment"
                -2 -> "User canceled"
                else -> "Unknown result"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onReq(req: BaseReq?) {
        Toast.makeText(this, "Request type: " + req?.type, Toast.LENGTH_SHORT)
            .show()
    }
}
