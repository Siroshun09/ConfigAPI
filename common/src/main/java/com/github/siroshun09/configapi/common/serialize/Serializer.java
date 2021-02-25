/*
 *     Copyright 2021 Siroshun09
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

package com.github.siroshun09.configapi.common.serialize;

import com.github.siroshun09.configapi.common.serialize.exception.DeserializationException;
import com.github.siroshun09.configapi.common.serialize.exception.SerializationException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Serializer<T> {

    @NotNull Map<String, Object> serialize(@NotNull T object) throws SerializationException;

    @NotNull T deserialize(@NotNull Map<String, Object> source) throws DeserializationException;
}
