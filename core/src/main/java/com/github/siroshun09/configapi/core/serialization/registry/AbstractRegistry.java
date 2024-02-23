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

package com.github.siroshun09.configapi.core.serialization.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

abstract class AbstractRegistry<T> {

    private final StampedLock lock = new StampedLock();
    private final Map<Class<?>, T> map = new LinkedHashMap<>();
    private Map<Class<?>, T> cachedUnmodifiableMap = Map.of();
    private volatile boolean frozen;

    protected final @Nullable T getValue(@NotNull Class<?> clazz) {
        var map = this.getMap();
        return map.get(clazz);
    }

    protected final <V> @Nullable T findValue(@NotNull V instance) {
        var map = this.getMap();
        var value = map.get(instance.getClass());

        if (value != null) {
            return value;
        }

        for (var entry : map.entrySet()) {
            if (entry.getKey().isInstance(instance)) {
                return entry.getValue();
            }
        }

        return null;
    }

    protected final void registerValue(@NotNull Class<?> clazz, @NotNull T value) {
        long stamp = this.lock.writeLock();
        boolean frozen;

        try {
            frozen = this.frozen;

            if (!frozen) {
                this.map.put(clazz, value);
                this.cachedUnmodifiableMap = Map.copyOf(this.map);
            }
        } finally {
            this.lock.unlockWrite(stamp);
        }

        if (frozen) {
            this.throwISE();
        }
    }

    protected final void registerAll(@NotNull Map<Class<?>, T> map) {
        long stamp = this.lock.writeLock();
        boolean frozen;

        try {
            frozen = this.frozen;

            if (!frozen) {
                this.map.putAll(map);
                this.cachedUnmodifiableMap = Map.copyOf(this.map);
            }
        } finally {
            this.lock.unlockWrite(stamp);
        }

        if (frozen) {
            this.throwISE();
        }
    }

    protected final @NotNull Map<Class<?>, T> getMap() {
        {
            long stamp = this.lock.tryOptimisticRead();
            var map = this.cachedUnmodifiableMap;

            if (this.lock.validate(stamp)) {
                return map;
            }
        }

        long stamp = this.lock.readLock();
        Map<Class<?>, T> ret;

        try {
            ret = this.cachedUnmodifiableMap;
        } finally {
            this.lock.unlockRead(stamp);
        }

        return ret;
    }

    protected void freezeRegistry() {
        long stamp = this.lock.writeLock();

        try {
            this.frozen = true;
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public boolean isFrozen() {
        {
            long stamp = this.lock.tryOptimisticRead();
            boolean frozen = this.frozen;

            if (this.lock.validate(stamp)) {
                return frozen;
            }
        }

        long stamp = this.lock.readLock();
        boolean frozen;

        try {
            frozen = this.frozen;
        } finally {
            this.lock.unlockRead(stamp);
        }

        return frozen;
    }

    protected void throwISE() {
        throw new IllegalStateException("This registry is frozen.");
    }
}
