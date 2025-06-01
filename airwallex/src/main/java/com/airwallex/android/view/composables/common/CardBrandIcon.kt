package com.airwallex.android.view.composables.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand

@Composable
internal fun CardBrandIcon(
    brand: CardBrand,
    modifier: Modifier = Modifier,
) {
    when (brand) {
        CardBrand.Visa -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_visa),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.MasterCard -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_mastercard),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.Amex -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_amex),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.UnionPay -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_unionpay),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.JCB -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_jcb),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.DISCOVER -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_discover),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.DINERS -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_diners),
            contentDescription = "card",
            modifier = modifier,
        )
        CardBrand.Unknown -> Image(
            painter = painterResource(id = R.drawable.airwallex_ic_unsupported),
            contentDescription = "card",
            modifier = modifier,
        )
    }
}

@Composable
@Preview
private fun CardBrandIconPreview() {
    CardBrandIcon(brand = CardBrand.Visa)
}