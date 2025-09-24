package at.mcbabo.calsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import at.mcbabo.calsync.data.repository.SettingsRepository
import at.mcbabo.calsync.data.store.AppSettings
import at.mcbabo.calsync.data.store.ThemeMode
import at.mcbabo.calsync.navigation.SimpleNavApp
import at.mcbabo.calsync.ui.theme.CalSyncTheme
import at.mcbabo.calsync.worker.SyncScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val settings by settingsRepository.settingsFlow.collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )

            val isDarkTheme =
                when (settings.selectedTheme) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }

            val navController = rememberNavController()

            CalSyncTheme(isDarkTheme) {
                SystemBarsTheme()
                DismissKeyboard {
                    SimpleNavApp(navController)
                }
            }
        }
    }
}

@Composable
fun SystemBarsTheme(backgroundColor: Color = MaterialTheme.colorScheme.background) {
    val activity = LocalActivity.current
    val insetsController = WindowCompat.getInsetsController(activity?.window!!, activity.window.decorView)
    val isLightBackground = backgroundColor.luminance() > 0.5f

    LaunchedEffect(backgroundColor) {
        activity.window?.setBackgroundDrawable(
            backgroundColor.toArgb().toDrawable()
        )

        activity.window.setNavigationBarContrastEnforced(false)

        insetsController.isAppearanceLightStatusBars = isLightBackground
        insetsController.isAppearanceLightNavigationBars = isLightBackground
    }
}

@Composable
fun DismissKeyboard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier =
            modifier.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.first()

                        if (change.pressed) {
                            focusManager.clearFocus()
                        }
                    }
                }
            }
    ) {
        content()
    }
}

