package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object DeviceFixtures {
    val DEVICE: Device = AirwallexPlugins.gson.fromJson(
        """
        {
            "browser_info": "Chrome/76.0.3809.100",
            "cookies_accepted": "true",
            "device_id": "IMEI-4432fsdafd31243244fdsafdfd653",
            "host_name": "www.airwallex.com",
            "http_browser_email": "jim631@sina.com",
            "http_browser_type": "chrome",
            "ip_address": "123.90.0.1",
            "ip_network_address": "128.0.0.0"
        }
        """.trimIndent(),
        Device::class.java
    )
}
