package com.airwallex.android.view.composables.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airwallex.android.R
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText

@Composable
internal fun PaymentMethodCard(
    paymentMethodType: AvailablePaymentMethodType,
    onClick: () -> Unit,
) {
    var isSelected by remember { mutableStateOf(false) }

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer
    val overlayColor = AirwallexColor.Transparent

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        onClick = {
            isSelected = true
            // TODO
            onClick()
        },
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(90.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = paymentMethodType.resources?.logos?.png ?: painterResource(id = R.drawable.airwallex_ic_card_default),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(40.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )

                Spacer(modifier = Modifier.height(8.dp))

                StandardText(
                    text = paymentMethodType.displayName ?: paymentMethodType.name,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else AirwallexColor.TextPrimary,
                    typography = if (isSelected) AirwallexTypography.Caption100Bold else AirwallexTypography.Caption100,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(overlayColor)
                )
            }
        }
    }
}