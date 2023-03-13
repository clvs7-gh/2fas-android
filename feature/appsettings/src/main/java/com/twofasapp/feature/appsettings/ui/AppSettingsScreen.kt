package com.twofasapp.feature.appsettings.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.ConfirmDialog
import com.twofasapp.designsystem.dialog.ListRadioDialog
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.designsystem.settings.SettingsSwitch
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppSettingsRoute(
    viewModel: AppSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppSettingsScreen(
        uiState = uiState,
        onSelectedThemeChange = { viewModel.setSelectedTheme(it) },
        onServicesStyleChange = { viewModel.setServiceStyle(it) },
        onShowNextTokenToggle = { viewModel.toggleShowNextToken() },
        onShowBackupNoticeToggle = { viewModel.toggleShowBackupNotice() },
        onAutoFocusSearchToggle = { viewModel.toggleAutoFocusSearch() }
    )
}

@Composable
private fun AppSettingsScreen(
    uiState: AppSettingsUiState,
    onSelectedThemeChange: (SelectedTheme) -> Unit,
    onServicesStyleChange: (ServicesStyle) -> Unit,
    onShowNextTokenToggle: () -> Unit,
    onShowBackupNoticeToggle: () -> Unit,
    onAutoFocusSearchToggle: () -> Unit,
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showServicesStyleDialog by remember { mutableStateOf(false) }
    var showConfirmDisableBackupNotice by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.settingsAppearance) }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            item {
                SettingsLink(
                    title = TwLocale.strings.settingsTheme,
                    subtitle = uiState.appSettings.selectedTheme.name,
                    icon = TwIcons.Theme,
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.settingsServicesStyle,
                    subtitle = uiState.appSettings.servicesStyle.name,
                    icon = TwIcons.ListStyle,
                    onClick = { showServicesStyleDialog = true }
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsShowNextCode,
                    checked = uiState.appSettings.showNextCode,
                    onCheckedChange = { onShowNextTokenToggle() },
                    subtitle = TwLocale.strings.settingsShowNextCodeBody,
                    icon = TwIcons.NextToken,
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsAutoFocusSearch,
                    checked = uiState.appSettings.autoFocusSearch,
                    onCheckedChange = { onAutoFocusSearchToggle() },
                    subtitle = TwLocale.strings.settingsAutoFocusSearchBody,
                    icon = TwIcons.Search,
                )
            }

            item {
                SettingsSwitch(
                    title = TwLocale.strings.settingsShowBackupNotice,
                    checked = uiState.appSettings.showBackupNotice,
                    onCheckedChange = { checked ->
                        if (checked.not()) {
                            showConfirmDisableBackupNotice = true
                        } else {
                            onShowBackupNoticeToggle()
                        }
                    },
                    icon = TwIcons.CloudOff,
                )
            }
        }

        if (showThemeDialog) {
            ListRadioDialog(
                onDismissRequest = { showThemeDialog = false },
                title = TwLocale.strings.settingsTheme,
                options = SelectedTheme.values().map { it.name },
                selectedOption = uiState.appSettings.selectedTheme.name,
                onOptionSelected = { index, _ -> onSelectedThemeChange(SelectedTheme.values()[index]) },
            )
        }

        if (showServicesStyleDialog) {
            ListRadioDialog(
                onDismissRequest = { showServicesStyleDialog = false },
                title = TwLocale.strings.settingsServicesStyle,
                options = ServicesStyle.values().map { it.name },
                selectedOption = uiState.appSettings.servicesStyle.name,
                onOptionSelected = { index, _ -> onServicesStyleChange(ServicesStyle.values()[index]) },
            )
        }

        if (showConfirmDisableBackupNotice) {
            ConfirmDialog(
                onDismissRequest = { showConfirmDisableBackupNotice = false },
                title = TwLocale.strings.settingsShowBackupNotice,
                body = TwLocale.strings.settingsShowBackupNoticeConfirmBody,
                onConfirm = { onShowBackupNoticeToggle() }
            )
        }
    }
}
