package com.notekeeper.ViewModel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    // Defineix si l'usuari s'ha registrat correctament o no hi ha cap acció
    enum class SignInAction {
        REGISTRAT, NONE
    }

    // LiveData per avisar a la pantalla que el registre ha anat bé
    private val _signInActionEvent = MutableLiveData<SignInAction>(SignInAction.NONE)
    val signInActionEvent: LiveData<SignInAction> = _signInActionEvent

    // LiveData per enviar missatges d'error (ej: email ja ocupat)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData per activar o desactivar el botó de registre
    private val _isSignInEnabled = MutableLiveData<Boolean>(false)
    val isSignInEnabled: LiveData<Boolean> = _isSignInEnabled

    // Variables per controlar el text i el color de l'etiqueta de l'Email
    private val _emailLabelText = MutableLiveData<String>("Email")
    val emailLabelText: LiveData<String> = _emailLabelText

    private val _emailLabelColor = MutableLiveData<Int>(Color.WHITE)
    val emailLabelColor: LiveData<Int> = _emailLabelColor

    // Variables per controlar el text i el color de l'etiqueta de la Contrasenya
    private val _passLabelText = MutableLiveData<String>("Contrasenya")
    val passLabelText: LiveData<String> = _passLabelText

    private val _passLabelColor = MutableLiveData<Int>(Color.WHITE)
    val passLabelColor: LiveData<Int> = _passLabelColor

    // Funció que es crida cada vegada que l'usuari escriu alguna cosa
    fun onSignInChanged(emailInput: String, passwordInput: String) {
        val emailEsValido = emailInput.contains("@") && emailInput.contains(".")

        // Comprova si l'email sembla real i canvia el color de l'etiqueta
        if (emailInput.isEmpty()) {
            _emailLabelText.value = "Email"
            _emailLabelColor.value = Color.WHITE
        } else {
            if (emailEsValido) {
                _emailLabelText.value = "Email - ¡Vàlid!"
                _emailLabelColor.value = Color.parseColor("#2ecc71")
            } else {
                _emailLabelText.value = "Email - Invàlid"
                _emailLabelColor.value = Color.parseColor("#e74c3c")
            }
        }

        // Comprova si la contrasenya és segura (ej.8 caràcters, números, etc.)
        val passEsFuerte = isStrongPassword(passwordInput)

        if (passwordInput.isEmpty()) {
            _passLabelText.value = "Contrasenya"
            _passLabelColor.value = Color.WHITE
        } else {
            if (passEsFuerte) {
                _passLabelText.value = "Contrasenya - ¡Segura!"
                _passLabelColor.value = Color.parseColor("#2ecc71")
            } else {
                _passLabelText.value = "Contrasenya - Febre"
                _passLabelColor.value = Color.parseColor("#e74c3c")
            }
        }

        // Habilita el botó de registre només si l'email i la pass són correctes
        _isSignInEnabled.value = emailEsValido && passEsFuerte
    }

    // Funció per registrar l'usuari
    fun registrarUsuari(email: String, pass: String) {
        if (UserRepository.emailExisteix(email)) {
            // Si l'email ja està agafat, mostrem error
            _errorMessage.value = "Aquest email ja està registrat"
        } else {
            // Si tot és correcte, el guardem i avisem de l'èxit
            UserRepository.registrarUsuario(email, pass)
            _signInActionEvent.value = SignInAction.REGISTRAT
            _errorMessage.value = null
        }
    }

    // Neteja l'esdeveniment de registre per evitar que es repeteixi per error
    fun resetEvent() {
        _signInActionEvent.value = SignInAction.NONE
    }

    // Neteja el missatge d'error
    fun resetError() {
        _errorMessage.value = null
    }

    // Lògica per saber si la contrasenya té majúscules, minúscules, números i símbols
    private fun isStrongPassword(pass: String): Boolean {
        if (pass.length < 8) return false
        var teMajuscula = false
        var teMinuscula = false
        var teNumero = false
        var teSimbol = false
        for (caracter in pass) {
            if (caracter.isUpperCase()) teMajuscula = true
            if (caracter.isLowerCase()) teMinuscula = true
            if (caracter.isDigit()) teNumero = true
            if (!caracter.isLetterOrDigit()) teSimbol = true
        }
        return teMajuscula && teMinuscula && teNumero && teSimbol
    }
}