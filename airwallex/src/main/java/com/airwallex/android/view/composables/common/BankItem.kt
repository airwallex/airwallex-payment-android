package com.airwallex.android.view.composables.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airwallex.android.core.model.Bank
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText

@Composable
internal fun BankItem(
    bank: Bank,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier
                        .background(
                            color = AirwallexColor.Ultraviolet10,
                            shape = RoundedCornerShape(8.dp),
                        )
                } else {
                    Modifier
                }
            )
            .clickable(
                onClick = onClick,
            )
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(24.dp))

        StandardText(
            text = bank.displayName,
            color = AirwallexColor.TextPrimary,
            typography = AirwallexTypography.Body100,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        bank.resources?.logos?.png?.let { icon ->
            Spacer(Modifier.weight(1f))

            AsyncImage(
                model = icon.trim(),
                contentDescription = "bank icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(32.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        }

        Spacer(modifier = Modifier.width(24.dp))
    }
}