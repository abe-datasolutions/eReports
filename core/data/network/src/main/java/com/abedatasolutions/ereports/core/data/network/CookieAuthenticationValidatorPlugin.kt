package com.abedatasolutions.ereports.core.data.network

import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.errors.network.AuthException
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.encodedPath
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * `CookieAuthenticationValidatorPlugin` is a Ktor client plugin responsible for validating authentication cookies
 * present in outgoing HTTP requests. It checks if a specific authentication cookie (`.ASPXAUTH`) is present,
 * and if it's still valid based on its expiration date.
 *
 * **Functionality:**
 * 1. **Whitelist Check:** It first checks if the requested path is included in a predefined whitelist
 *    ([Endpoints.authWhiteList]). If the path is whitelisted, it bypasses the authentication validation.
 *    This is useful for public endpoints that don't require authentication.
 * 2. **Cookie Retrieval:** If the path is not whitelisted, it retrieves cookies associated with the requested URL
 *    from the provided [CookiesStorage].
 * 3. **Authentication Cookie Search:** It searches for the `.ASPXAUTH` cookie within the retrieved cookies.
 *    If it's not found, it throws an [AuthException.UnauthorizedException], indicating that the user is not authenticated.
 * 4. **Expiration Check:** If the `.ASPXAUTH` cookie is found, it checks if the cookie has expired.
 *    - It parses the expiration timestamp from the cookie.
 *    - It compares the expiration time with the current time from [Clock.System].
 *    - If the cookie has expired, it throws an [AuthException.SessionExpiredException].
 * 5. **Exception Handling:** If an exception occurs during expiration timestamp parsing, it logs the exception using
 *    [Logger.recordException] and treats the cookie as valid. It doesn't throw exception in this case.
 * 6. **Success:** If the cookie is present and hasn't expired, the request proceeds normally.
 *
 * **Dependencies:**
 * - [CookiesStorage]: A storage mechanism for retrieving cookies based on URLs.
 * - [Endpoints.authWhiteList]: A list of URL path segments that should bypass authentication validation.
 * - [Logger]: A logging utility for recording debug messages and exceptions.
 * - [AuthException]: Custom exceptions for authentication failures.
 **/
internal class CookieAuthenticationValidatorPlugin(
    storage: CookiesStorage
): ClientPlugin<Unit> by createClientPlugin(
    name = "CookieAuthenticationValidator",
    body = {
        onRequest { request, _ ->
            val path = request.url.encodedPath.also {
                Logger.Debug.log("Request Path: $it")
            }
            val whiteListed = Endpoints.authWhiteList.any {
                path.contains(it, true)
            }.also {
                Logger.Debug.log("WhiteListed: $it")
            }
            if (whiteListed) return@onRequest
            val cookies = storage.get(request.url.build()).also {
                Logger.Debug.log("Cookies: $it")
            }

            val authCookie = cookies.find {
                it.name == AUTH_COOKIE_NAME
            }?: throw AuthException.UnauthorizedException

            val isAuthenticated = authCookie.expires?.runCatching {
                Instant.fromEpochMilliseconds(timestamp) > Clock.System.now()
            }?.getOrElse {
                Logger.recordException(it)
                null
            }?: return@onRequest

            Logger.Debug.log("Authenticated: $isAuthenticated")
            if (isAuthenticated) return@onRequest
            throw AuthException.SessionExpiredException
        }
    }
){
    companion object{
        private const val AUTH_COOKIE_NAME = ".ASPXAUTH"
    }
}