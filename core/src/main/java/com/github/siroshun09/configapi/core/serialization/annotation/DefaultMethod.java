/*
 *     Copyright 2024 Siroshun09
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.github.siroshun09.configapi.core.serialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to get a default value from the method.
 * <p>
 * Note that the method must be static.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.RECORD_COMPONENT
})
public @interface DefaultMethod {

    /**
     * Returns the class where the method is located.
     *
     * @return the class where the method is located
     */
    Class<?> clazz();

    /**
     * Returns the method name.
     *
     * @return the method name
     */
    String name();

}
