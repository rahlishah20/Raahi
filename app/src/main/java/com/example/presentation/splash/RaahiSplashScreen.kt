package com.example.presentation.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

/**
 * RAAHI launch / splash screen reproducing the approved design from stitch_raahi_dynamic_safety_navigation.
 *
 * Visual hierarchy:
 * 1. Desaturated Karachi street grid map background
 * 2. Liquid Glass frosted overlay
 * 3. Elevated RAAHI app icon with subtle emerald ambient glow and rounded glass card container
 * 4. "RAAHI" brand title with uppercase tracking
 * 5. "DYNAMIC SAFETY INTELLIGENCE" bottom status descriptor
 */
@Composable
fun RaahiSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
    displayDurationMillis: Long = 1600L
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(displayDurationMillis)
        onSplashFinished()
    }

    val easeCurve = remember { CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f) }

    val iconAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = easeCurve),
        label = "iconAlpha"
    )
    val iconTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 24f,
        animationSpec = tween(durationMillis = 700, easing = easeCurve),
        label = "iconTranslationY"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.94f,
        animationSpec = tween(durationMillis = 700, easing = easeCurve),
        label = "iconScale"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 100, easing = easeCurve),
        label = "titleAlpha"
    )
    val titleTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 20f,
        animationSpec = tween(durationMillis = 700, delayMillis = 100, easing = easeCurve),
        label = "titleTranslationY"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 200, easing = easeCurve),
        label = "taglineAlpha"
    )
    val taglineTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 16f,
        animationSpec = tween(durationMillis = 700, delayMillis = 200, easing = easeCurve),
        label = "taglineTranslationY"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8F8))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSplashFinished
            )
            .testTag("raahi_splash_screen")
    ) {
        // 1. Background Map Layer (Karachi street network)
        Image(
            painter = painterResource(id = R.drawable.splash_map_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.40f),
            contentScale = ContentScale.Crop
        )

        // 2. Liquid Glass Translucent Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCCFDF8F8),
                            Color(0x99FDF8F8),
                            Color(0xCCFDF8F8)
                        )
                    )
                )
        )

        // 3. Main Splash Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top flexible spacer
            Spacer(modifier = Modifier.weight(1f))

            // Center Content: Icon + Brand
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag("splash_center_content")
            ) {
                // App Icon with ambient glow & liquid glass container
                Box(
                    modifier = Modifier
                        .size(156.dp)
                        .graphicsLayer {
                            alpha = iconAlpha
                            translationY = iconTranslationY * density
                            scaleX = iconScale
                            scaleY = iconScale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Soft emerald halo glow
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x2E67FB98),
                                        Color(0x18006D36),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Glass card container
                    Box(
                        modifier = Modifier
                            .size(136.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color(0x20000000),
                                spotColor = Color(0x35000000)
                            )
                            .background(
                                color = Color(0xB3FDF8F8),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0x66FFFFFF),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.raahi_app_icon),
                            contentDescription = "RAAHI Application Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                                .testTag("splash_app_icon"),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Name
                Text(
                    text = "RAAHI",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        letterSpacing = 0.2.em,
                        color = Color(0xFF1C1B1B)
                    ),
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = titleAlpha
                            translationY = titleTranslationY * density
                        }
                        .testTag("splash_brand_name")
                )
            }

            // Bottom flexible spacer
            Spacer(modifier = Modifier.weight(1f))

            // Bottom Tagline
            Text(
                text = "DYNAMIC SAFETY INTELLIGENCE",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.15.em,
                    color = Color(0xFF444748).copy(alpha = 0.70f)
                ),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .graphicsLayer {
                        alpha = taglineAlpha
                        translationY = taglineTranslationY * density
                    }
                    .testTag("splash_tagline")
            )
        }
    }
}
