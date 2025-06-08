package com.airwallex.android.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airwallex.android.ui.R

@Suppress("LongParameterList")
@Composable
fun StandardIcon(
    @DrawableRes drawableRes: Int,
    size: Dp,
    padding: Dp,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
) {
    Box(modifier = modifier.padding(padding)) {
        Icon(
            painter = painterResource(id = drawableRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
            tint = tint,
        )
    }
}

@Composable
@Preview
private fun StandardIconPreview() {
    StandardIcon(
        drawableRes = R.drawable.airwallex_ic_card_default,
        size = 24.dp,
        padding = 0.dp,
    )
}