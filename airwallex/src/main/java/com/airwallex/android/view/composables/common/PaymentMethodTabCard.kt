package com.airwallex.android.view.composables.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airwallex.android.R
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText

@Composable
internal fun PaymentMethodTabCard(
    isSelected: Boolean,
    selectedType: AvailablePaymentMethodType,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiaryContainer

    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(75.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AsyncImage(
                    model = selectedType.resources?.logos?.png ?: painterResource(id = R.drawable.airwallex_ic_card_default),
                    contentDescription = "payment method icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(32.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )

                Spacer(modifier = Modifier.height(4.dp))

                StandardText(
                    text = selectedType.displayName ?: selectedType.name,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else AirwallexColor.TextPrimary,
                    typography = if (isSelected) AirwallexTypography.Caption300Bold else AirwallexTypography.Caption300,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }
        }
    }
}