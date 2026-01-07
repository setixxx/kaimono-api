package setixx.software.utils

class ValidationException(message: String) : Exception(message)
class UserAlreadyExistsException(message: String) : Exception(message)
class AuthenticationException(message: String) : Exception(message)