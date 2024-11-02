package zone.cogni.semanticz.shaclviz.util

import java.io.File

/**
 * Utility functions.
 */
object Utils {

    /**
     * Loads the content of a path as a String. The path can be either:
     * - a file on the filesystem
     * - a classpath resource (if prefixed with 'classpath:') taken from the classloader of the Utils class.
     *
     * @param path the path of a classpath resource/file
     * @return the path content, or null if the path is invalid
     */
    fun loadResourceContent(path: String): String? =
        if (path.startsWith("classpath:")) {
            val resourcePath = path.removePrefix("classpath:")
            object {}.javaClass.getResourceAsStream(resourcePath)?.bufferedReader()?.use { it.readText() }
        } else {
            val file = File(path)
            if (file.exists()) {
                file.readText()
            } else {
                null
            }
        }
}