package com.airwallex.android.model

import com.airwallex.android.model.parser.WechatParser
import org.json.JSONObject

internal object WechatFixtures {
    val WECHAT: WeChat = WechatParser().parse(
        JSONObject(
            """
        {
            "appId": "wx123456",
            "partnerId": "1",
            "prepayId": "1",
            "`package`": "com.airwallex.android",
            "nonceStr": "100000",
            "sign": "100000",
            "timestamp": "1371112223331"
        }
            """.trimIndent()
        )
    )
}
