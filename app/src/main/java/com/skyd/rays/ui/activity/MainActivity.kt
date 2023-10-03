package com.skyd.rays.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skyd.rays.model.bean.UriWithStickerUuidBean
import com.skyd.rays.model.preference.SettingsProvider
import com.skyd.rays.ui.local.LocalDarkMode
import com.skyd.rays.ui.local.LocalNavController
import com.skyd.rays.ui.local.LocalWindowSizeClass
import com.skyd.rays.ui.screen.MAIN_SCREEN_ROUTE
import com.skyd.rays.ui.screen.MainScreen
import com.skyd.rays.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.skyd.rays.ui.screen.about.AboutScreen
import com.skyd.rays.ui.screen.about.license.LICENSE_SCREEN_ROUTE
import com.skyd.rays.ui.screen.about.license.LicenseScreen
import com.skyd.rays.ui.screen.about.update.UpdateDialog
import com.skyd.rays.ui.screen.add.ADD_SCREEN_ROUTE
import com.skyd.rays.ui.screen.add.AddScreen
import com.skyd.rays.ui.screen.add.openAddScreen
import com.skyd.rays.ui.screen.minitool.MINI_TOOL_SCREEN_ROUTE
import com.skyd.rays.ui.screen.minitool.MiniToolScreen
import com.skyd.rays.ui.screen.minitool.styletransfer.STYLE_TRANSFER_SCREEN_ROUTE
import com.skyd.rays.ui.screen.minitool.styletransfer.StyleTransferScreen
import com.skyd.rays.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.SettingsScreen
import com.skyd.rays.ui.screen.settings.api.API_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.api.ApiScreen
import com.skyd.rays.ui.screen.settings.api.apigrant.API_GRANT_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.api.apigrant.ApiGrantScreen
import com.skyd.rays.ui.screen.settings.appearance.APPEARANCE_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.appearance.AppearanceScreen
import com.skyd.rays.ui.screen.settings.appearance.style.HOME_STYLE_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.appearance.style.HomeStyleScreen
import com.skyd.rays.ui.screen.settings.data.DATA_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.data.DataScreen
import com.skyd.rays.ui.screen.settings.data.importexport.IMPORT_EXPORT_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.data.importexport.ImportExportScreen
import com.skyd.rays.ui.screen.settings.data.importexport.cloud.webdav.WEBDAV_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.data.importexport.cloud.webdav.WebDavScreen
import com.skyd.rays.ui.screen.settings.ml.ML_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.ml.MlScreen
import com.skyd.rays.ui.screen.settings.ml.classification.CLASSIFICATION_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.ml.classification.ClassificationScreen
import com.skyd.rays.ui.screen.settings.ml.classification.model.CLASSIFICATION_MODEL_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.ml.classification.model.ClassificationModelScreen
import com.skyd.rays.ui.screen.settings.ml.textrecognize.TEXT_RECOGNIZE_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.ml.textrecognize.TextRecognizeScreen
import com.skyd.rays.ui.screen.settings.searchconfig.SEARCH_CONFIG_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.searchconfig.SearchConfigScreen
import com.skyd.rays.ui.screen.settings.shareconfig.SHARE_CONFIG_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.shareconfig.ShareConfigScreen
import com.skyd.rays.ui.screen.settings.shareconfig.autoshare.AUTO_SHARE_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.shareconfig.autoshare.AutoShareScreen
import com.skyd.rays.ui.screen.settings.shareconfig.uristringshare.URI_STRING_SHARE_SCREEN_ROUTE
import com.skyd.rays.ui.screen.settings.shareconfig.uristringshare.UriStringShareScreen
import com.skyd.rays.ui.theme.RaysTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberNavController()
            SettingsProvider {
                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalWindowSizeClass provides calculateWindowSizeClass(this)
                ) {
                    AppContent()
                    LaunchedEffect(Unit) {
                        initIntent()
                    }
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
        var openUpdateDialog by rememberSaveable { mutableStateOf(true) }

        RaysTheme(darkTheme = LocalDarkMode.current) {
            NavHost(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                navController = navController,
                startDestination = MAIN_SCREEN_ROUTE,
                enterTransition = {
                    fadeIn(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + scaleIn(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        initialScale = 0.96f
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + scaleOut(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        targetScale = 0.96f
                    )
                },
                popEnterTransition = {
                    fadeIn(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + scaleIn(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        initialScale = 0.96f
                    )
                },
                popExitTransition = {
                    fadeOut(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + scaleOut(
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        targetScale = 0.96f
                    )
                },
            ) {
                composable(route = MAIN_SCREEN_ROUTE) {
                    MainScreen()
                }
                composable(
                    route = "$ADD_SCREEN_ROUTE?isEdit={isEdit}",
                    arguments = listOf(navArgument("isEdit") { defaultValue = false })
                ) {
                    AddScreen(
                        initStickers = it.arguments?.getParcelableArrayList("stickers")
                            ?: mutableListOf(),
                        isEdit = it.arguments?.getBoolean("isEdit") ?: false,
                    )
                }
                composable(route = SETTINGS_SCREEN_ROUTE) {
                    SettingsScreen()
                }
                composable(route = ML_SCREEN_ROUTE) {
                    MlScreen()
                }
                composable(route = CLASSIFICATION_SCREEN_ROUTE) {
                    ClassificationScreen()
                }
                composable(route = CLASSIFICATION_MODEL_SCREEN_ROUTE) {
                    ClassificationModelScreen()
                }
                composable(route = TEXT_RECOGNIZE_SCREEN_ROUTE) {
                    TextRecognizeScreen()
                }
                composable(route = SEARCH_CONFIG_SCREEN_ROUTE) {
                    SearchConfigScreen()
                }
                composable(route = APPEARANCE_SCREEN_ROUTE) {
                    AppearanceScreen()
                }
                composable(route = HOME_STYLE_SCREEN_ROUTE) {
                    HomeStyleScreen()
                }
                composable(route = ABOUT_SCREEN_ROUTE) {
                    AboutScreen()
                }
                composable(route = LICENSE_SCREEN_ROUTE) {
                    LicenseScreen()
                }
                composable(route = IMPORT_EXPORT_SCREEN_ROUTE) {
                    ImportExportScreen()
                }
                composable(route = WEBDAV_SCREEN_ROUTE) {
                    WebDavScreen()
                }
                composable(route = DATA_SCREEN_ROUTE) {
                    DataScreen()
                }
                composable(route = SHARE_CONFIG_SCREEN_ROUTE) {
                    ShareConfigScreen()
                }
                composable(route = URI_STRING_SHARE_SCREEN_ROUTE) {
                    UriStringShareScreen()
                }
                composable(route = MINI_TOOL_SCREEN_ROUTE) {
                    MiniToolScreen()
                }
                composable(route = STYLE_TRANSFER_SCREEN_ROUTE) {
                    StyleTransferScreen()
                }
                composable(route = API_SCREEN_ROUTE) {
                    ApiScreen()
                }
                composable(route = API_GRANT_SCREEN_ROUTE) {
                    ApiGrantScreen()
                }
                composable(route = AUTO_SHARE_SCREEN_ROUTE) {
                    AutoShareScreen()
                }
            }

            if (openUpdateDialog) {
                UpdateDialog(silence = true, onClosed = { openUpdateDialog = false })
            }
        }
    }

    private fun initIntent() {
        val stickers: MutableList<UriWithStickerUuidBean> = when (intent?.action) {
            Intent.ACTION_SEND -> {
                val data = mutableListOf<UriWithStickerUuidBean>()
                if (intent.type?.startsWith("image/") == true) {
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM)
                    }
                    if (uri != null) {
                        data.add(UriWithStickerUuidBean(uri = uri))
                    }
                    data
                } else data
            }

            Intent.ACTION_SEND_MULTIPLE -> {
                val data = mutableListOf<UriWithStickerUuidBean>()
                if (intent.type?.startsWith("image/") == true) {
                    val uris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
                    }?.map { UriWithStickerUuidBean(uri = it) }
                    if (uris != null) {
                        data.addAll(uris)
                    }
                    data
                } else data
            }

            else -> mutableListOf()
        }

        if (stickers.isEmpty()) return

        openAddScreen(
            navController = navController,
            stickers = stickers,
            isEdit = false,
        )
    }
}
