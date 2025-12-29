package com.example.storeapp.models

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class DatabaseException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class ValidationException(message: String) : AppException(message)
    class AuthenticationException(message: String) : AppException(message)
    class NotFoundException(message: String) : AppException(message)
}
