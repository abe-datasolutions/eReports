package com.abedatasolutions.ereports.core.errors.network

sealed class AuthException(message: String): NetworkException(message) {
    data object InvalidCredentialsException: AuthException("Invalid Username/Password") {
        private fun readResolve(): Any = InvalidCredentialsException
    }
    data object AccountExpiredException: AuthException("Account has expired") {
        private fun readResolve(): Any = AccountExpiredException
    }
    data object SessionExpiredException: AuthException("Session expired") {
        private fun readResolve(): Any = SessionExpiredException
    }
    data object UnauthorizedException: AuthException("User not logged in") {
        private fun readResolve(): Any = UnauthorizedException
    }
}