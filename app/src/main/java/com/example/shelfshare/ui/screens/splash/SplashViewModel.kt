package com.example.shelfshare.ui.screens.splash

import androidx.compose.runtime.mutableStateOf
import com.example.shelfshare.LOGIN_SCREEN
import com.example.shelfshare.SPLASH_SCREEN
import com.example.shelfshare.model.service.AccountService
import com.example.shelfshare.model.service.LogService
import com.example.shelfshare.HOME_SCREEN
import com.example.shelfshare.ui.screens.ShelfShareViewModel
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ShelfShareViewModel(logService) {
    val showError = mutableStateOf(false)


    fun onAppStart(openAndPopUp: (String, String) -> Unit) {

        showError.value = false
        if (accountService.hasUser) openAndPopUp(HOME_SCREEN, SPLASH_SCREEN)
        else redirectToAuthStack(openAndPopUp)
    }

    private fun redirectToAuthStack(openAndPopUp: (String, String) -> Unit){
        openAndPopUp(LOGIN_SCREEN, SPLASH_SCREEN)
    }
}