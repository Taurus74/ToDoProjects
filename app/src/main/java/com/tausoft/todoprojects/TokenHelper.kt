package com.tausoft.todoprojects

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import java.io.IOException

internal class TokenHelper(val context: Context) {
    companion object {
        private const val TAG = "TokenHelper"
        private const val AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/userinfo.email"
    }

    fun getToken(account: Account?): String {
        Log.d(TAG, "Start getToken")
        val accountManager = AccountManager.get(context)
        val options = Bundle()
        var token = ""
        if (account != null)
            accountManager.getAuthToken(
                account,
                AUTH_TOKEN_TYPE,
                options,
                context as Activity,
                { result ->
                    Log.d(TAG, "run for token...")
                    try {
                        token = result.result.getString(AccountManager.KEY_AUTHTOKEN).toString()
                        Log.d(TAG, "Token is <$token>")
                    } catch (e: AuthenticatorException) {
                        Log.e(TAG, e.toString())
                    } catch (e: IOException) {
                        Log.e(TAG, e.toString())
                    } catch (e: OperationCanceledException) {
                        Log.e(TAG, e.toString())
                    }
                },
                null
            )
        return token
    }
}