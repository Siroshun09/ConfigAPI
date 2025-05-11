/*
 *     Copyright 2025 Siroshun09
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

package dev.siroshun.configapi.core.node;

import dev.siroshun.configapi.core.node.visitor.NodeVisitor;
import dev.siroshun.configapi.core.node.visitor.VisitResult;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A {@link Node} implementation that represents {@code null}.
 */
@NotNullByDefault
public final class NullNode implements Node<Object> {

    /**
     * An instance of {@link NullNode}.
     */
    public static final NullNode NULL = new NullNode();

    private NullNode() {
    }

    @Override
    public @Nullable Object value() {
        return null;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public Optional<Object> asOptional() {
        return Optional.empty();
    }

    @Override
    public VisitResult accept(NodeVisitor visitor) {
        return visitor.visit(this);
    }
}
