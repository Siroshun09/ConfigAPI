package dev.siroshun.configapi.codec;

import dev.siroshun.codec4j.api.codec.Codec;
import dev.siroshun.codec4j.api.encoder.Encoder;
import dev.siroshun.codec4j.api.error.DecodeError;
import dev.siroshun.codec4j.api.error.EncodeError;
import dev.siroshun.codec4j.api.io.ElementReader;
import dev.siroshun.codec4j.api.io.EntryIn;
import dev.siroshun.codec4j.api.io.EntryReader;
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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

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
            in -> {
                Result<ElementReader<? extends In>, DecodeError> readerResult = in.readList();
                if (readerResult.isFailure()) {
                    return readerResult.asFailure();
                }
                ListNode listNode = ListNode.create();

                ElementReader<?> reader = readerResult.unwrap();
                while (reader.hasNext()) {
                    Result<? extends In, DecodeError> elementInResult = reader.next();
                    if (elementInResult.isFailure()) {
                        return elementInResult.asFailure();
                    }

                    Result<Node<?>, DecodeError> result = decodeNode(elementInResult.unwrap());
                    if (result.isFailure()) {
                        return result.asFailure();
                    }

                    listNode.add(result.unwrap());
                }

                Result<Void, DecodeError> finishResult = reader.finish();
                if (finishResult.isFailure()) {
                    return finishResult.asFailure();
                }

                return Result.success(listNode);
            },
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
            in -> {
                Result<EntryReader, DecodeError> readerResult = in.readMap();
                if (readerResult.isFailure()) {
                    return readerResult.asFailure();
                }
                MapNode mapNode = MapNode.create();

                EntryReader reader = readerResult.unwrap();
                while (reader.hasNext()) {
                    Result<EntryIn, DecodeError> entryInResult = reader.next();
                    if (entryInResult.isFailure()) {
                        return entryInResult.asFailure();
                    }

                    EntryIn entryIn = entryInResult.unwrap();
                    Result<String, DecodeError> key = entryIn.keyIn().readAsString();
                    Result<Node<?>, DecodeError> value = decodeNode(entryIn.valueIn());

                    if (key.isFailure()) {
                        return key.asFailure();
                    } else if (value.isFailure()) {
                        return value.asFailure();
                    }

                    mapNode.set(key.unwrap(), value.unwrap());
                }

                Result<Void, DecodeError> finishResult = reader.finish();
                if (finishResult.isFailure()) {
                    return finishResult.asFailure();
                }

                return Result.success(mapNode);
            },
            "ConfigAPI-MapNodeCodec"
    );

    private static <O> Result<O, EncodeError> encodeNode(@NotNull Out<O> out, @UnknownNullability Node<?> node) {
        return switch (node) {
            case StringValue(String value) -> out.writeString(value);
            case EnumValue<?>(Enum<?> value) -> out.writeString(value.name());
            case NumberValue numberValue -> switch (numberValue) {
                case IntValue intValue -> out.writeInt(intValue.value());
                case LongValue longValue -> out.writeLong(longValue.value());
                case DoubleValue doubleValue -> out.writeDouble(doubleValue.value());
                case FloatValue floatValue -> out.writeFloat(floatValue.value());
                case ByteValue byteValue -> out.writeByte(byteValue.value());
                case ShortValue shortValue -> out.writeShort(shortValue.value());
            };
            case BooleanValue booleanValue -> out.writeBoolean(booleanValue.value());
            case CharValue charValue -> out.writeChar(charValue.asChar());
            case ListNode listNode -> LIST_NODE_CODEC.encode(out, listNode);
            case MapNode mapNode -> MAP_NODE_CODEC.encode(out, mapNode);
            case ArrayNode<?> arrayNode -> switch (arrayNode) {
                case IntArray(int[] values) -> out.createList().flatMap(appender -> {
                    for (int value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeInt(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case LongArray(long[] values) -> out.createList().flatMap(appender -> {
                    for (long value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeLong(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case DoubleArray(double[] values) -> out.createList().flatMap(appender -> {
                    for (double value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeDouble(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case FloatArray(float[] values) -> out.createList().flatMap(appender -> {
                    for (float value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeFloat(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case ByteArray(byte[] values) -> out.createList().flatMap(appender -> {
                    for (byte value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeByte(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case ShortArray(short[] values) -> out.createList().flatMap(appender -> {
                    for (short value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeShort(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case BooleanArray(boolean[] values) -> out.createList().flatMap(appender -> {
                    for (boolean value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeBoolean(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
                case CharArray(char[] values) -> out.createList().flatMap(appender -> {
                    for (char value : values) {
                        Result<O, EncodeError> result = appender.append(o -> o.writeChar(value));
                        if (result.isFailure()) {
                            return result.asFailure();
                        }
                    }
                    return appender.finish();
                }, EncodeError::asFailure);
            };
            case CommentedNode<?> commentedNode -> encodeNode(out, commentedNode.node());
            case null, default -> new UnsupportedNodeEncodeError(node).asFailure();
        };
    }

    private static Result<Node<?>, DecodeError> decodeNode(@NotNull In in) {
        Result<Type, DecodeError> type = in.type();
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
    public record UnsupportedNodeEncodeError(@Nullable Node<?> node) implements EncodeError.Failure {
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
