package com.surf_sharing.surfsharingmobileapp.utils;

/**
 * Utility class containing the error codes returned by a FirebaseAuth operation.
 *
 * Errors:
 * CUSTOM_TOKEN_MISMATCH - The custom token corresponds to a different audience.
 * INVALID_CREDENTIAL - The supplied auth credential is malformed or has expired.
 * INVALID_EMAIL - The email address is badly formatted.
 * WRONG_PASSWORD - The password is invalid or the user does not have a password.
 * USER_MISMATCH - The supplied credentials do not correspond to the previously signed in user.
 * REQUIRES_RECENT_LOGIN - This operation is sensitive and requires recent authentication. Log in again before retrying this request.
 * ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL - An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
 * EMAIL_ALREADY_IN_USE - The email address is already in use by another account.
 * CREDENTIAL_ALREADY_IN_USE - This credential is already associated with a different user account.
 * USER_DISABLED - The user account has been disabled by an administrator.
 * USER_TOKEN_EXPIRED - The user\'s credential is no longer valid. The user must sign in again.
 * USER_NOT_FOUND - There is no user record corresponding to this identifier. The user may have been deleted.
 * INVALID_USER_TOKEN - The user\'s credential is no longer valid. The user must sign in again.
 * OPERATION_NOT_ALLOWED - This operation is not allowed. You must enable this service in the console.
 * WEAK_PASSWORD - The given password is invalid.
 *
 * Created by aran on 12/03/17.
 */
public class FirebaseError {

    /**
     * FirebaseAuth error codes.
     */
    public static final String
            INVALID_CUSTOM_TOKEN = "ERROR_INVALID_CUSTOM_TOKEN",
            CUSTOM_TOKEN_MISMATCH = "ERROR_CUSTOM_TOKEN_MISMATCH",
            INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL",
            INVALID_EMAIL = "ERROR_INVALID_EMAIL",
            WRONG_PASSWORD = "ERROR_WRONG_PASSWORD",
            USER_MISMATCH = "ERROR_USER_MISMATCH",
            REQUIRES_RECENT_LOGIN = "ERROR_REQUIRES_RECENT_LOGIN",
            ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL = "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL",
            EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE",
            CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE",
            USER_DISABLED = "ERROR_USER_DISABLED",
            USER_TOKEN_EXPIRED = "ERROR_USER_TOKEN_EXPIRED",
            USER_NOT_FOUND = "ERROR_USER_NOT_FOUND",
            INVALID_USER_TOKEN = "ERROR_INVALID_USER_TOKEN",
            OPERATION_NOT_ALLOWED = "ERROR_OPERATION_NOT_ALLOWED",
            WEAK_PASSWORD = "ERROR_WEAK_PASSWORD";

}
