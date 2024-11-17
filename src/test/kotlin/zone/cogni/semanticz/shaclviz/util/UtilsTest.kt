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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.*
import kotlin.io.path.deleteExisting
import kotlin.io.path.writeText

class UtilsTest {

    @Test
    fun loadResourceContentLoadsCorrectlyExistingResource() {
        val content = Utils.loadResourceContent("classpath:/example.ttl")
        Assertions.assertNotNull(content)
        Assertions.assertTrue(content?.length!! > 0)
    }

    @Test
    fun loadResourceContentFailsForNonExistingResource() {
        val content = Utils.loadResourceContent("classpath:/" + UUID.randomUUID())
        Assertions.assertNull(content)
    }

    @Test
    fun loadResourceContentLoadsCorrectlyExistingFile() {
        val f = Files.createTempFile("test", ".txt")
        f.writeText("hello")
        val content = Utils.loadResourceContent(f.toFile().absolutePath)
        Assertions.assertEquals("hello",content)
        f.deleteExisting()
    }

    @Test
    fun loadResourceContentFailsForNonExistingFile() {
        val content = Utils.loadResourceContent("" + UUID.randomUUID())
        Assertions.assertNull(content)
    }
}