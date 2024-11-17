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

package zone.cogni.semanticz.shaclviz.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TypeTest {

    @Test
    fun testGetHtmlWithoutField() {
        val c = Type("http://test.org/test", "test")
        Assertions.assertEquals("test", c.name)
        Assertions.assertEquals("http://test.org/test", c.iri)
        Assertions.assertEquals(0, c.fields.size)
    }

    @Test
    fun testGetHtmlWithField() {
        val c = Type("http://test.org/test", "test")
        c.addField(Property("http://test.org/testp", "p"), Type("http://test.org/test", "r"), 1, "")
        Assertions.assertEquals(1, c.fields.size)
        val (property, typeAsRange) = c.fields.iterator().next()
        Assertions.assertEquals("http://test.org/testp", property.iri)
        Assertions.assertEquals("p", property.name)
        Assertions.assertEquals("http://test.org/test", typeAsRange.cls.iri)
        Assertions.assertEquals("r", typeAsRange.cls.name)
        Assertions.assertEquals(1, typeAsRange.min)
        Assertions.assertEquals("", typeAsRange.max)

    }
}