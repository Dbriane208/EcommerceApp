package daniel.brian.ecommerceapp.util

sealed class ResourceWrapper<out T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T?) : ResourceWrapper<T>(data)
    class Error(message: String?) : ResourceWrapper<Nothing>(message = message)
    class Loading<T> : ResourceWrapper<T>()
    class Unspecified<T> : ResourceWrapper<T>()
}
