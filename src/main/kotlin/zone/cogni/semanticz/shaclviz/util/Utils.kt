/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
            File(path).takeIf { it.exists() }?.readText()
        }
}