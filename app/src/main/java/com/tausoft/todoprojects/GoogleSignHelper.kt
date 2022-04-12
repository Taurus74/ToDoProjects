package com.tausoft.todoprojects

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task

class GoogleSignHelper(context: Context) {
    var mGoogleSignInClient: GoogleSignInClient? = null

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent() = mGoogleSignInClient!!.signInIntent

    fun signOut(): Task<Void> = mGoogleSignInClient!!.signOut()

    fun revokeAccess(): Task<Void> = mGoogleSignInClient!!.revokeAccess()
}