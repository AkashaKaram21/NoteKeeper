package com.example.notekeeper.ViewModel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogInViewModel : ViewModel() {

    // Estats de l'acció de login
    enum class LoginAction { LOGIN_OK, NONE }

    // Variables internes per controlar els canvis
    private val _loginActionEvent = MutableLiveData<LoginAction>(LoginAction.NONE)
    val loginActionEvent: LiveData<LoginAction> = _loginActionEvent

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoginEnabled = MutableLiveData<Boolean>(false)
    val isLoginEnabled: LiveData<Boolean> = _isLoginEnabled

    private val _emailLabelText = MutableLiveData<String>("Email")
    val emailLabelText: LiveData<String> = _emailLabelText

    private val _emailLabelColor = MutableLiveData<Int>(Color.WHITE)
    val emailLabelColor: LiveData<Int> = _emailLabelColor

    private val _passLabelText = MutableLiveData<String>("Contrasenya")
    val passLabelText: LiveData<String> = _passLabelText

    private val _passLabelColor = MutableLiveData<Int>(Color.WHITE)
    val passLabelColor: LiveData<Int> = _passLabelColor

    // Es crida cada cop que l'usuari escriu algo
    fun onLoginChanged(emailInput: String, passwordInput: String) {
        val emailEsValido = emailInput.contains("@") && emailInput.contains(".")

        // Valida l'email i canvia el text/color
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

        // Valida la contrasenya
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

        // Activa el botó només si tot està bé
        _isLoginEnabled.value = emailEsValido && passEsFuerte
    }

    // Comprova les credencials amb el Repository
    fun ferLogin(email: String, password: String) {
        if (UserRepository.comprobarCredenciales(email, password)) {
            _loginActionEvent.value = LoginAction.LOGIN_OK
            _errorMessage.value = null
        } else {
            _errorMessage.value = "Email o contrasenya incorrectes"
        }
    }

    fun resetEvent() { _loginActionEvent.value = LoginAction.NONE }
    fun resetError() { _errorMessage.value = null }

    // Funció per comprovar si la contrasenya és segura
    private fun isStrongPassword(pass: String): Boolean {
        if (pass.length < 8) return false
        var teMajuscula = false; var teMinuscula = false; var teNumero = false; var teSimbol = false
        for (caracter in pass) {
            if (caracter.isUpperCase()) teMajuscula = true
            if (caracter.isLowerCase()) teMinuscula = true
            if (caracter.isDigit()) teNumero = true
            if (!caracter.isLetterOrDigit()) teSimbol = true
        }
        return teMajuscula && teMinuscula && teNumero && teSimbol
    }
}