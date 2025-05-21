package com.airwallex.android.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText

@Composable
fun WarningBanner(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(
                color = AirwallexColor.Yellow10,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        Image(
            painter = painterResource(id = R.drawable.airwallex_ic_warning),
            contentDescription = "Warning Icon",
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(24.dp),
        )
        StandardText(
            text = message,
            typography = AirwallexTypography.Body200,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}