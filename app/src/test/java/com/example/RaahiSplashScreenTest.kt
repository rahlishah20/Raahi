package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.example.presentation.splash.RaahiSplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class RaahiSplashScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun splash_screen_renders_components_and_matches_visuals() {
        var finished = false
        composeTestRule.setContent {
            MyApplicationTheme {
                RaahiSplashScreen(
                    onSplashFinished = { finished = true },
                    displayDurationMillis = 2000L
                )
            }
        }

        // Advance clock so fade-up animations complete
        composeTestRule.mainClock.advanceTimeBy(1000L)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("raahi_splash_screen").assertExists()
        composeTestRule.onNodeWithTag("splash_app_icon", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("splash_brand_name", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("splash_tagline", useUnmergedTree = true).assertExists()

        // Capture Roborazzi screenshot for visual verification
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/raahi_splash.png")

        // Clicking anywhere on splash completes launch early
        composeTestRule.onNodeWithTag("raahi_splash_screen").performClick()
        composeTestRule.waitForIdle()
        assertTrue(finished)
    }
}
