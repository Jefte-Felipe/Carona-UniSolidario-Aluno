package com.tcc.unisolidaria.providers

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthProvider {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun register(email: String, password: String): Task<AuthResult> {
        if (isValidEmailDomain(email)) {
            return auth.createUserWithEmailAndPassword(email, password)
        } else {
            val failedTask = TaskCompletionSource<AuthResult>()
            failedTask.setException(Exception("E-mail inválido"))
            return failedTask.task
        }
    }

    private fun isValidEmailDomain(email: String): Boolean {
        return email.endsWith("@unifatecpr.com.br")
    }

    fun getId(): String {
        // NULL POINTER EXCEPTION
        return auth.currentUser?.uid ?: ""
    }

    fun existSession(): Boolean {
        var exist = false
        if (auth.currentUser != null) {
            exist = true
        }
        return exist
    }

    fun logout() {
        auth.signOut()
    }

}