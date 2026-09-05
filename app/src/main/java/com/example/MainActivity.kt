package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.repository.DestinationRepositoryImpl
import com.example.navigation.RaahiNavHost
import com.example.presentation.splash.RaahiSplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceLight

class MainActivity : ComponentActivity() {

    private val destinationRepository = DestinationRepositoryImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SurfaceLight
                ) {
                    var isSplashFinished by rememberSaveable { mutableStateOf(false) }

                    AnimatedContent(
                        targetState = isSplashFinished,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(350))
                        },
                        label = "RaahiLaunchTransition"
                    ) { finished ->
                        if (!finished) {
                            RaahiSplashScreen(
                                onSplashFinished = { isSplashFinished = true },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            RaahiNavHost(
                                destinationRepository = destinationRepository,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
