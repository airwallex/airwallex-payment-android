package com.airwallex.android.view.composables.addcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.CardBrand
import com.airwallex.android.view.composables.common.CardBrandIcon
import kotlinx.coroutines.delay

private const val FIXED_ICON_NUMBER = 3
private const val CARD_BRAND_SWITCH_INTERVAL = 2000L

@Suppress("ComplexMethod", "LongMethod")
@Composable
internal fun CardBrandTrailingAccessory(
    modifier: Modifier = Modifier,
    icons: List<Int>,
    brand: CardBrand = CardBrand.Unknown,
    displayAllSchemes: Boolean,
) {
    if (displayAllSchemes) {
        Row {
            icons.take(FIXED_ICON_NUMBER).forEach { icon ->
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "card",
                    modifier = modifier,
                )
            }
            if (icons.size > FIXED_ICON_NUMBER) {
                val cardBrandImages = remember(icons) { icons.drop(FIXED_ICON_NUMBER) }
                if (cardBrandImages.isNotEmpty()) {
                    val currentImageIndex = remember { mutableIntStateOf(0) }
                    LaunchedEffect(currentImageIndex.intValue) {
                        while (true) {
                            delay(CARD_BRAND_SWITCH_INTERVAL)
                            currentImageIndex.intValue = (currentImageIndex.intValue + 1) % cardBrandImages.size
                        }
                    }
                    Image(
                        painter = painterResource(id = cardBrandImages[currentImageIndex.intValue]),
                        contentDescription = "card",
                        modifier = modifier,
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    } else {
        CardBrandIcon(
            brand = brand,
            modifier = modifier,
        )
    }
}