/*
 *     Copyright 2023 Siroshun09
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

package com.github.siroshun09.configapi.core.serialization;

/**
 * A {@link RuntimeException} that will be thrown when errors occurred while serializing/deserializing objects.
 */
public class SerializationException extends RuntimeException {

    /**
     * Creates {@link Serialization} with no message and no cause.
     */
    public SerializationException() {
    }

    /**
     * Creates {@link Serialization} with the message.
     *
     * @param message the detail message
     * @see RuntimeException#RuntimeException(String)
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * Creates {@link Serialization} with the message and other {@link Throwable}.
     *
     * @param message the detail message
     * @param cause   the caused {@link Throwable}
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates {@link Serialization} with other {@link Throwable}.
     *
     * @param cause the caused {@link Throwable}
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
