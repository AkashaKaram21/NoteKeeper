package com.notekeeper.ViewModel

// Objecte únic per guardar les dades
object UserRepository {

    // Mapa per guardar els usuaris
    private val usuariosRegistrados = mutableMapOf<String, String>()

    // Guarda l'email de l'usuari que ha entrat
    var emailGuardado: String? = null
        private set

    // Funció per registrar un usuari nou
    fun registrarUsuario(email: String, pass: String): Boolean {
        // Si l'email ja existeix, no el registrem
        if (usuariosRegistrados.containsKey(email)) {
            return false
        }
        // Guardem l'usuari i la contrasenya
        usuariosRegistrados[email] = pass
        emailGuardado = email
        return true
    }

    // Comprova si l'email i la contrasenya són correctes
    fun comprobarCredenciales(email: String, pass: String): Boolean {
        return usuariosRegistrados[email] == pass
    }

    // Mira si l'email ja està a la llista
    fun emailExisteix(email: String): Boolean {
        return usuariosRegistrados.containsKey(email)
    }
}