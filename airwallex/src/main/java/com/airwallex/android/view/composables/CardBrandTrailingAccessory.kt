package com.airwallex.android.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.CardScheme
import kotlinx.coroutines.delay

private const val MAX_FIXED_SCHEMES = 3
private const val CARD_BRAND_SWITCH_INTERVAL = 2000L

@Suppress("ComplexMethod", "LongMethod")
@Composable
internal fun CardBrandTrailingAccessory(
    schemes: List<CardScheme>,
    brand: CardBrand = CardBrand.Unknown,
    displayAllSchemes: Boolean,
) {
    if (displayAllSchemes) {
        Row {
            schemes.take(MAX_FIXED_SCHEMES).map { CardBrand.fromType(it.name)?.icon ?: R.drawable.airwallex_ic_card_default }.forEach { icon ->
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "card",
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
            if (schemes.size > MAX_FIXED_SCHEMES) {
                val cardBrandImages = remember(schemes) {
                    schemes.drop(MAX_FIXED_SCHEMES).map { CardBrand.fromType(it.name)?.icon ?: R.drawable.airwallex_ic_card_default }
                }
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
                        modifier = Modifier
                            .padding(horizontal = 2.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    } else {
        when (brand) {
            CardBrand.Visa -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_visa),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.MasterCard -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_mastercard),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.Amex -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_amex),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.UnionPay -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_unionpay),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.JCB -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_jcb),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.DISCOVER -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_discover),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.DINERS -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_diners),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            CardBrand.Unknown -> Image(
                painter = painterResource(id = R.drawable.airwallex_ic_unsupported),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    }

}