package dev.siroshun.configapi.codec;

import dev.siroshun.codec4j.api.codec.Codec;
import dev.siroshun.codec4j.api.codec.Encoder;
import dev.siroshun.codec4j.api.error.DecodeError;
import dev.siroshun.codec4j.api.error.EncodeError;
import dev.siroshun.codec4j.api.io.In;
import dev.siroshun.codec4j.api.io.Out;
import dev.siroshun.codec4j.api.io.Type;
import dev.siroshun.configapi.core.node.ArrayNode;
import dev.siroshun.configapi.core.node.BooleanArray;
import dev.siroshun.configapi.core.node.BooleanValue;
import dev.siroshun.configapi.core.node.ByteArray;
import dev.siroshun.configapi.core.node.ByteValue;
import dev.siroshun.configapi.core.node.CharArray;
import dev.siroshun.configapi.core.node.CharValue;
import dev.siroshun.configapi.core.node.CommentedNode;
import dev.siroshun.configapi.core.node.DoubleArray;
import dev.siroshun.configapi.core.node.DoubleValue;
import dev.siroshun.configapi.core.node.EnumValue;
import dev.siroshun.configapi.core.node.FloatArray;
import dev.siroshun.configapi.core.node.FloatValue;
import dev.siroshun.configapi.core.node.IntArray;
import dev.siroshun.configapi.core.node.IntValue;
import dev.siroshun.configapi.core.node.ListNode;
import dev.siroshun.configapi.core.node.LongArray;
import dev.siroshun.configapi.core.node.LongValue;
import dev.siroshun.configapi.core.node.MapNode;
import dev.siroshun.configapi.core.node.Node;
import dev.siroshun.configapi.core.node.NumberValue;
import dev.siroshun.configapi.core.node.ShortArray;
import dev.siroshun.configapi.core.node.ShortValue;
import dev.siroshun.configapi.core.node.StringValue;
import dev.siroshun.jfun.result.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * Implementations of {@link Codec} for ConfigAPI's {@link Node}.
 */
@ApiStatus.Experimental
public final class NodeCodec {

    /**
     * A {@link Codec} for {@link Node}s.
     */
    public static final Codec<Node<?>> NODE_CODEC = Codec.codec(NodeCodec::encodeNode, NodeCodec::decodeNode, "ConfigAPI-NodeCodec");

    /**
     * A {@link Codec} for {@link ListNode}s.
     */
    public static final Codec<ListNode> LIST_NODE_CODEC = Codec.codec(
            new Encoder<>() {
                @Override
                public @NotNull <O> Result<O, EncodeError> encode(@NotNull Out<O> out, @NotNull ListNode listNode) {
                    return out.createList().flatMap(
                            appender -> {
                                for (Node<?> node : listNode.value()) {
                                    Result<O, EncodeError> result = appender.append(o -> encodeNode(o, node));
                                    if (result.isFailure()) {
                                        return result.asFailure();
                                    }
                                }
                                return appender.finish();
                            }, EncodeError::asFailure);
                }
            },
            in -> in.readList(ListNode.create(), (listNode, elementIn) -> {
                Result<Node<?>, DecodeError> result = decodeNode(elementIn);
                if (result.isSuccess()) {
                    listNode.add(result.unwrap());
                    return Result.success();
                } else {
                    return result.asFailure();
                }
            }),
            "ConfigAPI-ListNodeCodec"
    );

    /**
     * A {@link Codec} for {@link MapNode}.
     */
    public static final Codec<MapNode> MAP_NODE_CODEC = Codec.codec(
            new Encoder<>() {
                @Override
                public @NotNull <O> Result<O, EncodeError> encode(@NotNull Out<O> out, @NotNull MapNode mapNode) {
                    return out.createMap().flatMap(
                            appender -> {
                                for (Map.Entry<Object, Node<?>> entry : mapNode.value().entrySet()) {
                                    Object key = entry.getKey();
                                    Node<?> value = entry.getValue();
                                    Result<Void, EncodeError> result = appender.append(
                                            o -> o.writeString(String.valueOf(key)),
                                            o -> encodeNode(o, value)
                                    );
                                    if (result.isFailure()) {
                                        return result.asFailure();
                                    }
                                }
                                return appender.finish();
                            }, EncodeError::asFailure);
                }
            },
            in -> in.readMap(MapNode.create(), (mapNode, entryIn) -> {
                Result<String, DecodeError> key = entryIn.keyIn().readAsString();
                if (key.isFailure()) {
                    return key.asFailure();
                }
                Result<Node<?>, DecodeError> value = decodeNode(entryIn.valueIn());
                if (value.isFailure()) {
                    return value.asFailure();
                }
                mapNode.set(key.unwrap(), value.unwrap());
                return Result.success();
            }),
            "ConfigAPI-MapNodeCodec"
    );

    private static <O> Result<O, EncodeError> encodeNode(Out<O> out, Node<?> node) {
        if (node instanceof StringValue(String value)) {
            return out.writeString(value);
        } else if (node instanceof EnumValue<?>(Enum<?> value)) {
            return out.writeString(value.name());
        } else if (node instanceof NumberValue numberValue) {
            var clazz = numberValue.getClass();

            if (clazz == ByteValue.class) {
                return out.writeByte(numberValue.asByte());
            } else if (clazz == ShortValue.class) {
                return out.writeShort(numberValue.asShort());
            } else if (clazz == IntValue.class) {
                return out.writeInt(numberValue.asInt());
            } else if (clazz == LongValue.class) {
                return out.writeLong(numberValue.asLong());
            } else if (clazz == FloatValue.class) {
                return out.writeFloat(numberValue.asFloat());
            } else if (clazz == DoubleValue.class) {
                return out.writeDouble(numberValue.asDouble());
            }
        } else if (node instanceof BooleanValue booleanValue) {
            return out.writeBoolean(booleanValue.value());
        } else if (node instanceof CharValue charValue) {
            return out.writeChar(charValue.asChar());
        } else if (node instanceof ListNode listNode) {
            return LIST_NODE_CODEC.encode(out, listNode);
        } else if (node instanceof MapNode mapNode) {
            return MAP_NODE_CODEC.encode(out, mapNode);
        } else if (node instanceof ArrayNode<?>) {
            if (node instanceof IntArray(int[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (int value : values) {
                                var result = appender.append(o -> o.writeInt(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof LongArray(long[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (long value : values) {
                                var result = appender.append(o -> o.writeLong(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof DoubleArray(double[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (double value : values) {
                                var result = appender.append(o -> o.writeDouble(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof FloatArray(float[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (float value : values) {
                                var result = appender.append(o -> o.writeFloat(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof ByteArray(byte[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (byte value : values) {
                                var result = appender.append(o -> o.writeByte(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof ShortArray(short[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (short value : values) {
                                var result = appender.append(o -> o.writeShort(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof BooleanArray(boolean[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (boolean value : values) {
                                var result = appender.append(o -> o.writeBoolean(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            } else if (node instanceof CharArray(char[] values)) {
                return out.createList().flatMap(
                        appender -> {
                            for (char value : values) {
                                var result = appender.append(o -> o.writeChar(value));
                                if (result.isFailure()) {
                                    return result.asFailure();
                                }
                            }
                            return appender.finish();
                        }, EncodeError::asFailure);
            }
        } else if (node instanceof CommentedNode<?> commentedNode) {
            return encodeNode(out, commentedNode.node());
        }

        return new UnsupportedNodeEncodeError(node).asFailure();
    }

    private static Result<Node<?>, DecodeError> decodeNode(In in) {
        var type = in.type();
        if (type.isFailure()) {
            return type.asFailure();
        }

        return switch (type.unwrap()) {
            case Type.Value<?> value -> switch (value) {
                case Type.BooleanValue ignored -> in.readAsBoolean().map(BooleanValue::fromBoolean);
                case Type.ByteValue ignored -> in.readAsByte().map(ByteValue::new);
                case Type.ShortValue ignored -> in.readAsShort().map(ShortValue::new);
                case Type.IntValue ignored -> in.readAsInt().map(IntValue::new);
                case Type.LongValue ignored -> in.readAsLong().map(LongValue::new);
                case Type.FloatValue ignored -> in.readAsFloat().map(FloatValue::new);
                case Type.DoubleValue ignored -> in.readAsDouble().map(val -> {
                    if (val.intValue() == val) {
                        return new IntValue(val.intValue());
                    } else if (val.longValue() == val) {
                        return new LongValue(val.longValue());
                    } else {
                        return new DoubleValue(val);
                    }
                });
                case Type.CharValue ignored -> in.readAsChar().map(CharValue::new);
                case Type.StringValue ignored -> in.readAsString().map(StringValue::fromString);
            };
            case Type.ListType ignored -> LIST_NODE_CODEC.decode(in).map(Function.identity());
            case Type.MapType ignored -> MAP_NODE_CODEC.decode(in).map(Function.identity());
            case Type.Unknown ignored -> new UnknownTypeDecodeError().asFailure();
        };
    }

    /**
     * A {@link EncodeError} when the {@link Node} cannot be encoded.
     *
     * @param node the {@link Node} cannot be encoded
     */
    public record UnsupportedNodeEncodeError(Node<?> node) implements EncodeError.Failure {
    }

    /**
     * A {@link DecodeError} when the {@link Type} is {@link Type.Unknown}.
     */
    public record UnknownTypeDecodeError() implements DecodeError.Failure {
    }

    private NodeCodec() {
        throw new UnsupportedOperationException();
    }
}
