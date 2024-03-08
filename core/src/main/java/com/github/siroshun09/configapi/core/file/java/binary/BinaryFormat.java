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

package com.github.siroshun09.configapi.core.file.java.binary;

import com.github.siroshun09.configapi.core.file.FileFormat;
import com.github.siroshun09.configapi.core.node.ArrayNode;
import com.github.siroshun09.configapi.core.node.BooleanArray;
import com.github.siroshun09.configapi.core.node.BooleanValue;
import com.github.siroshun09.configapi.core.node.ByteArray;
import com.github.siroshun09.configapi.core.node.ByteValue;
import com.github.siroshun09.configapi.core.node.CharArray;
import com.github.siroshun09.configapi.core.node.CharValue;
import com.github.siroshun09.configapi.core.node.CommentedNode;
import com.github.siroshun09.configapi.core.node.DoubleArray;
import com.github.siroshun09.configapi.core.node.DoubleValue;
import com.github.siroshun09.configapi.core.node.EnumValue;
import com.github.siroshun09.configapi.core.node.FloatArray;
import com.github.siroshun09.configapi.core.node.FloatValue;
import com.github.siroshun09.configapi.core.node.IntArray;
import com.github.siroshun09.configapi.core.node.IntValue;
import com.github.siroshun09.configapi.core.node.ListNode;
import com.github.siroshun09.configapi.core.node.LongArray;
import com.github.siroshun09.configapi.core.node.LongValue;
import com.github.siroshun09.configapi.core.node.MapNode;
import com.github.siroshun09.configapi.core.node.Node;
import com.github.siroshun09.configapi.core.node.NullNode;
import com.github.siroshun09.configapi.core.node.ObjectNode;
import com.github.siroshun09.configapi.core.node.ShortArray;
import com.github.siroshun09.configapi.core.node.ShortValue;
import com.github.siroshun09.configapi.core.node.StringValue;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link FileFormat} implementation that loading/saving {@link MapNode} from/to binary format.
 * <p>
 * Supported {@link Node}s:
 *
 * <ul>
 *     <li>{@link com.github.siroshun09.configapi.core.node.ValueNode}s
 *     <ul>
 *         <li>{@link EnumValue} will be written as {@link String} using {@link Enum#name()}</li>
 *         <li>Loading {@link EnumValue} is not supported</li>
 *     </ul>
 *     </li>
 *     <li>{@link ArrayNode}, {@link ListNode} and {@link MapNode}</li>
 *     <li>{@link NullNode}</li>
 *     <li>{@link CommentedNode} - The comment will be dropped</li>
 * </ul>
 * <p>
 * For specifications of this format, please see the comments in the source code.
 */
public final class BinaryFormat implements FileFormat<Node<?>> {

    /**
     * An instance of {@link BinaryFormat}.
     */
    public static final BinaryFormat DEFAULT = new BinaryFormat();

    /*
      Each node is always given a header.
      The header is encoded in a byte, and the information of the node is represented in the 8 bits.

      The information represented by the byte value can be divided as follows:

        | Length info | Data type |
        |     000     |   00000   |

      The "Data type" field can also be further divided:

        | Value (0) or Structure (1) | Type |
        |             0              | 0000 |

      For value nodes, the most significant bit is 0 and the value type is stored in the "Type" field.
      The current value types are as follows:

        NullNode     - 0x00
        BooleanValue - 0x01
        ByteValue    - 0x02
        DoubleValue  - 0x03
        FloatValue   - 0x04
        IntValue     - 0x05
        LongValue    - 0x06
        ShortValue   - 0x07
        StringValue  - 0x08
        CharValue    - 0x09

      If the most significant bit is 1, it means that the data is a structure.
      The mapping to the node classes that are structures, is as follows:

        ListNode               - 0x10
        BooleanArray           - 0x11
        ByteArray              - 0x12
        DoubleArray            - 0x13
        FloatArray             - 0x14
        IntArray               - 0x15
        LongArray              - 0x16
        ShortArray             - 0x17
        ListNode (Only String) - 0x18
        CharArray              - 0x19
        MapNode                - 0x1f

      In the case of a structure, length information is given in the "Length info" field (high 3 bits) of the header.
      To reduce data size, they are represented differently depending on their length.
      If the length is equal to or less than 4, it is written directly in the header.
      When 5 or more, add the minimum required data using the appropriate numeric type.
      This means that length information is stored as follows:

      |   Length   | "Length type" field | Additional data type |
      |    <= 4    |      000 ~ 100      |  No additional data  |
      |   <= 255   |         101         |         Byte         |
      |  <= 65535  |         110         |         Short        |
      |   65535 <  |         111         |         Int          |

      The elements in ListNode or the entries in MapNode are also stored according to the above.
      This means that ListNode/MapNode will be output as follows:

        ListNode: [header](length data)[header][element data]...
        MapNode:  [header](length data)[header][key data][value data]...

      Additionally, ListNode and MapNode can be nested.
      This is because the length value is used as the number of elements (entries) and writes/reads that number of them.

      Based on these specifications, DataInput/DataOutput methods are called.
      Therefore, the actual data representation depends on their implementation and format.
     */

    /*
      Value Types: 0x00 ~ 0x0f
     */
    private static final byte NULL = 0x00;
    private static final byte BOOLEAN = 0x01;
    private static final byte BYTE = 0x02;
    private static final byte DOUBLE = 0x03;
    private static final byte FLOAT = 0x04;
    private static final byte INT = 0x05;
    private static final byte LONG = 0x06;
    private static final byte SHORT = 0x07;
    private static final byte STRING = 0x08;
    private static final byte CHAR = 0x09;

    // 0x0f cannot be used as a value type because there is no difference between Array + 0x0f and Map
    @Deprecated
    private static final byte PRESERVED_VALUE_TYPE_F = 0x0f;

    /*
      Structure Types: (N is the value type)
        Array: 0x1N
          List: 0x10 (Array + NULL)
          Primitive Array: 0x11 ~ 0x17
          String List: 0x18
        Map: 0x1f
     */
    private static final byte ARRAY = 0x10;
    private static final byte MAP = 0x1f;

    /*
      Length Types:
        0 ~ 4: Represents the length itself, no additional data
        5: The length is represented as byte
        6: The length is represented as short
        7: The length is represented as int
     */
    private static final byte LENGTH_TYPE_BYTE = 5;
    private static final byte LENGTH_TYPE_SHORT = 6;
    private static final byte LENGTH_TYPE_INT = 7;

    /*
      The values to access each field.
     */
    private static final byte VALUE_TYPE_MASK = 0x0f;
    private static final byte DATA_TYPE_MASK = 0x1f;
    private static final byte LENGTH_TYPE_SHIFT = 5;

    private static final int MAX_UNSIGNED_BYTE = 0xff;
    private static final int MAX_UNSIGNED_SHORT = 0xffff;

    private BinaryFormat() {
    }

    @Override
    public @NotNull Node<?> load(@NotNull Path filepath) throws IOException {
        Objects.requireNonNull(filepath);
        if (Files.isRegularFile(filepath)) {
            try (InputStream in = Files.newInputStream(filepath)) {
                return this.load(in);
            }
        } else {
            return NullNode.NULL;
        }
    }

    @Override
    public @NotNull Node<?> load(@NotNull InputStream input) throws IOException {
        return read(new DataInputStream(Objects.requireNonNull(input)));
    }

    @Override
    public void save(@NotNull Node<?> node, @NotNull Path filepath) throws IOException {
        var parent = filepath.getParent();

        if (parent != null && !Files.isDirectory(parent)) {
            Files.createDirectories(parent);
        }

        try (OutputStream out = Files.newOutputStream(filepath)) {
            this.save(node, out);
        }
    }

    @Override
    public void save(@NotNull Node<?> node, @NotNull OutputStream output) throws IOException {
        write(node, new DataOutputStream(Objects.requireNonNull(output)));
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Not supported
     */
    @Override
    @Deprecated
    public @NotNull Node<?> load(@NotNull Reader reader) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Not supported
     */
    @Override
    @Deprecated
    public void save(@NotNull Node<?> node, @NotNull Writer writer) {
        throw new UnsupportedOperationException();
    }

    private static void write(@NotNull Node<?> node, @NotNull DataOutput out) throws IOException {
        var clazz = node.getClass();

        if (clazz == NullNode.class) {
            out.writeByte(NULL);
        } else if (clazz == BooleanValue.class) {
            out.writeByte(BOOLEAN);
            out.writeBoolean(((BooleanValue) node).asBoolean());
        } else if (clazz == ByteValue.class) {
            out.writeByte(BYTE);
            out.writeByte(((ByteValue) node).asByte());
        } else if (clazz == CharValue.class) {
            out.writeByte(CHAR);
            out.writeChar(((CharValue) node).asChar());
        } else if (clazz == DoubleValue.class) {
            out.writeByte(DOUBLE);
            out.writeDouble(((DoubleValue) node).asDouble());
        } else if (clazz == FloatValue.class) {
            out.writeByte(FLOAT);
            out.writeFloat(((FloatValue) node).asFloat());
        } else if (clazz == IntValue.class) {
            out.writeByte(INT);
            out.writeInt(((IntValue) node).asInt());
        } else if (clazz == LongValue.class) {
            out.writeByte(LONG);
            out.writeLong(((LongValue) node).asLong());
        } else if (clazz == ShortValue.class) {
            out.writeByte(SHORT);
            out.writeShort(((ShortValue) node).asShort());
        } else if (clazz == StringValue.class) {
            out.writeByte(STRING);
            out.writeUTF(((StringValue) node).asString());
        } else if (ArrayNode.class.isAssignableFrom(clazz)) {
            if (clazz == BooleanArray.class) {
                boolean[] array = ((BooleanArray) node).value();
                writeArrayHeader(out, BOOLEAN, array.length);
                for (boolean val : array) out.writeBoolean(val);
            } else if (clazz == ByteArray.class) {
                byte[] array = ((ByteArray) node).value();
                writeArrayHeader(out, BYTE, array.length);
                for (byte val : array) out.writeByte(val);
            } else if (clazz == CharArray.class) {
                char[] array = ((CharArray) node).value();
                writeArrayHeader(out, CHAR, array.length);
                for (char val : array) out.writeChar(val);
            } else if (clazz == DoubleArray.class) {
                double[] array = ((DoubleArray) node).value();
                writeArrayHeader(out, DOUBLE, array.length);
                for (double val : array) out.writeDouble(val);
            } else if (clazz == FloatArray.class) {
                float[] array = ((FloatArray) node).value();
                writeArrayHeader(out, FLOAT, array.length);
                for (float val : array) out.writeFloat(val);
            } else if (clazz == IntArray.class) {
                int[] array = ((IntArray) node).value();
                writeArrayHeader(out, INT, array.length);
                for (int val : array) out.writeInt(val);
            } else if (clazz == LongArray.class) {
                long[] array = ((LongArray) node).value();
                writeArrayHeader(out, LONG, array.length);
                for (long val : array) out.writeLong(val);
            } else if (clazz == ShortArray.class) {
                short[] array = ((ShortArray) node).value();
                writeArrayHeader(out, SHORT, array.length);
                for (short val : array) out.writeShort(val);
            } else {
                throw new IOException("Unknown array type: " + clazz);
            }
        } else if (clazz == ListNode.IMPLEMENTATION_CLASS) {
            List<Node<?>> list = ((ListNode) node).value();
            List<StringValue> stringList = ((ListNode) node).asList(StringValue.class);
            int size = list.size();

            if (size == stringList.size()) {
                writeArrayHeader(out, STRING, size);
                for (int i = 0; i < size; i++) out.writeUTF(stringList.get(i).asString());
            } else {
                writeArrayHeader(out, NULL, size);
                for (int i = 0; i < size; i++) write(list.get(i), out);
            }
        } else if (clazz == MapNode.IMPLEMENTATION_CLASS) {
            Map<Object, Node<?>> map = ((MapNode) node).value();
            writeMapHeader(out, map.size());
            for (Map.Entry<Object, Node<?>> entry : map.entrySet()) {
                write(Node.fromObject(entry.getKey()), out);
                write(entry.getValue(), out);
            }
        } else if (clazz == CommentedNode.class) {
            write(((CommentedNode<?>) node).node(), out);
        } else if (clazz == ObjectNode.class) {
            Object obj = node.value();
            throw new IOException("Unsupported object type:" + obj.getClass());
        } else {
            throw new IOException("Unsupported Node type: " + clazz);
        }
    }

    private static void writeArrayHeader(@NotNull DataOutput out, byte valueType, int length) throws IOException {
        writeHeader(out, valueType | ARRAY, length);
    }

    private static void writeMapHeader(@NotNull DataOutput out, int entries) throws IOException {
        writeHeader(out, MAP, entries);
    }

    private static void writeHeader(@NotNull DataOutput out, int dataType, int length) throws IOException {
        if (length < LENGTH_TYPE_BYTE) {
            out.writeByte((length << LENGTH_TYPE_SHIFT) | dataType);
        } else if (length <= MAX_UNSIGNED_BYTE) {
            out.writeByte((LENGTH_TYPE_BYTE << LENGTH_TYPE_SHIFT) | dataType);
            out.writeByte(length);
        } else if (length <= MAX_UNSIGNED_SHORT) {
            out.writeByte((LENGTH_TYPE_SHORT << LENGTH_TYPE_SHIFT) | dataType);
            out.writeShort(length);
        } else {
            out.writeByte((LENGTH_TYPE_INT << LENGTH_TYPE_SHIFT) | dataType);
            out.writeInt(length);
        }
    }

    private static @NotNull Node<?> read(@NotNull DataInput in) throws IOException {
        int header = in.readUnsignedByte();
        int dataType = header & DATA_TYPE_MASK;

        if (dataType == MAP) {
            int entries = readLength(in, header);
            MapNode mapNode = MapNode.create();
            for (int i = 0; i < entries; i++) {
                Object key = read(in).value();
                Node<?> value = read(in);
                mapNode.set(key, value);
            }
            return mapNode;
        }

        if ((header & ARRAY) == ARRAY) {
            int valueType = header & VALUE_TYPE_MASK;
            int length = readLength(in, header);

            return switch (valueType) {
                case NULL -> {
                    ListNode listNode = ListNode.create(length);
                    for (int i = 0; i < length; i++) listNode.add(read(in));
                    yield listNode;
                }
                case BOOLEAN -> {
                    boolean[] array = new boolean[length];
                    for (int i = 0; i < length; i++) array[i] = in.readBoolean();
                    yield new BooleanArray(array);
                }
                case BYTE -> {
                    byte[] array = new byte[length];
                    for (int i = 0; i < length; i++) array[i] = in.readByte();
                    yield new ByteArray(array);
                }
                case CHAR -> {
                    char[] array = new char[length];
                    for (int i = 0; i < length; i++) array[i] = in.readChar();
                    yield new CharArray(array);
                }
                case DOUBLE -> {
                    double[] array = new double[length];
                    for (int i = 0; i < length; i++) array[i] = in.readDouble();
                    yield new DoubleArray(array);
                }
                case FLOAT -> {
                    float[] array = new float[length];
                    for (int i = 0; i < length; i++) array[i] = in.readFloat();
                    yield new FloatArray(array);
                }
                case INT -> {
                    int[] array = new int[length];
                    for (int i = 0; i < length; i++) array[i] = in.readInt();
                    yield new IntArray(array);
                }
                case LONG -> {
                    long[] array = new long[length];
                    for (int i = 0; i < length; i++) array[i] = in.readLong();
                    yield new LongArray(array);
                }
                case SHORT -> {
                    short[] array = new short[length];
                    for (int i = 0; i < length; i++) array[i] = in.readShort();
                    yield new ShortArray(array);
                }
                case STRING -> {
                    ListNode listNode = ListNode.create(length);
                    for (int i = 0; i < length; i++) listNode.add(in.readUTF());
                    yield listNode;
                }
                default -> throw new IOException("Unsupported array type: " + valueType);
            };
        }

        return switch (dataType) {
            case NULL -> NullNode.NULL;
            case BOOLEAN -> BooleanValue.fromBoolean(in.readBoolean());
            case BYTE -> new ByteValue(in.readByte());
            case CHAR -> new CharValue(in.readChar());
            case DOUBLE -> new DoubleValue(in.readDouble());
            case FLOAT -> new FloatValue(in.readFloat());
            case INT -> new IntValue(in.readInt());
            case LONG -> new LongValue(in.readLong());
            case SHORT -> new ShortValue(in.readShort());
            case STRING -> StringValue.fromString(in.readUTF());
            default -> throw new IOException("Unsupported data type: " + dataType);
        };
    }

    private static int readLength(@NotNull DataInput in, int header) throws IOException {
        int lengthType = header >> LENGTH_TYPE_SHIFT;
        return switch (lengthType) {
            case LENGTH_TYPE_BYTE -> in.readUnsignedByte();
            case LENGTH_TYPE_SHORT -> in.readUnsignedShort();
            case LENGTH_TYPE_INT -> {
                int length = in.readInt();
                if (length < 0) {
                    throw new IOException("Length cannot be negative (got " + length + ")");
                }
                yield length;
            }
            default -> {
                if (lengthType < LENGTH_TYPE_BYTE) {
                    yield lengthType;
                } else {
                    throw new IOException("Unsupported length type: " + lengthType);
                }
            }
        };
    }
}
