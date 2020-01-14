package dev.morphia.aggregation.experimental.expressions;

import dev.morphia.aggregation.experimental.expressions.internal.DocumentExpression;
import dev.morphia.aggregation.experimental.expressions.internal.Literal;
import dev.morphia.aggregation.experimental.expressions.internal.Push;
import dev.morphia.mapping.Mapper;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;

import java.util.StringJoiner;

/**
 * Base class for all the expression types.
 *
 * @mongodb.driver.manual reference/operator/aggregation/ Expressions
 */
public abstract class Expression {
    private final String operation;
    private final Object value;

    protected Expression(final String operation) {
        this.operation = operation;
        this.value = null;
    }

    protected Expression(final String operation, final Object value) {
        this.operation = operation;
        this.value = value;
    }

    /**
     * Creates a field expression for the given value.  If the value does not already start with '$', it will be prepended automatically.
     *
     * @param name the field name
     *
     * @return the new expression
     */
    public static Expression field(final String name) {
        return new Literal(name.startsWith("$") ? name : "$" + name);
    }

    /**
     * Returns a value without parsing. Use for values that the aggregation pipeline may interpret as an expression.
     *
     * @param value the value
     * @return the new expression
     * @mongodb.driver.manual reference/operator/aggregation/literal $literal
     */
    public static Expression literal(final Object value) {
        return new Literal(value);
    }

    /**
     * Returns an array of all values that result from applying an expression to each document in a group of documents that share the
     * same group by key.
     * <p>
     * $push is only available in the $group stage.
     *
     * @return the new expression
     * @mongodb.driver.manual reference/operator/aggregation/push $push
     */
    public static Push push() {
        return new Push();
    }

    /**
     * Creates a new DocumentExpression.
     *
     * @return the new expression
     */
    public static DocumentExpression of() {
        return new DocumentExpression();
    }

    /**
     * @param mapper         the mapper
     * @param writer         the writer
     * @param encoderContext the context
     * @morphia.internal
     */
    @SuppressWarnings("rawtypes")
    public void encode(final Mapper mapper, final BsonWriter writer, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeName(operation);
        Codec codec = mapper.getCodecRegistry().get(value.getClass());
        encoderContext.encodeWithChildContext(codec, writer, value);
        writer.writeEndDocument();
    }

    /**
     * @return the value
     * @morphia.internal
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @return the value
     * @morphia.internal
     */
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Expression.class.getSimpleName() + "[", "]")
                   .add("operation='" + operation + "'")
                   .toString();
    }

}
