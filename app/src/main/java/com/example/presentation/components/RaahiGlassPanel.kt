package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassSurfaceBackground
import com.example.ui.theme.GlassSurfaceBorder

@Composable
fun RaahiGlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassSurfaceBackground,
    borderColor: Color = GlassSurfaceBorder,
    borderWidth: Dp = 0.5.dp,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x14000000),
                spotColor = Color(0x14000000)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}
