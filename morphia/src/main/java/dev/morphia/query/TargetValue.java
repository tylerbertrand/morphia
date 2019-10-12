package dev.morphia.query;

import dev.morphia.internal.PathTarget;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.codec.DocumentWriter;
import dev.morphia.mapping.codec.pojo.PropertyHandler;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.pojo.PropertyModel;

import java.util.StringJoiner;

class TargetValue {
    private PathTarget target;
    private Object value;

    public TargetValue(final PathTarget target, final Object value) {
        this.target = target;
        this.value = value;
    }

    public PathTarget getTarget() {
        return target;
    }

    public Object getValue() {
        return value;
    }

    public Object encode(final Mapper mapper) {
        if(target == null) {
            return value;
        }
        MappedField mappedField = this.target.getTarget();
        Object mappedValue = value;

        PropertyModel<?> propertyModel = mappedField != null
                                         ? mappedField.getDeclaringClass()
                                                      .getMorphiaModel()
                                                      .getPropertyModel(mappedField.getJavaFieldName())
                                         : null;

        Codec cachedCodec = propertyModel.getCachedCodec();
        if (cachedCodec instanceof PropertyHandler) {
            mappedValue = ((PropertyHandler) cachedCodec).prepare(mappedValue);
        }

        if (mappedValue != null) {
            Codec codec = mapper.getCodecRegistry().get(mappedValue.getClass());
            DocumentWriter writer = new DocumentWriter();
            try {
                codec.encode(writer, mappedValue, EncoderContext.builder().build());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw e;
            }
            mappedValue = writer.getRoot();
        }
        return new Document(target.translatedPath(), mappedValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TargetValue.class.getSimpleName() + "[", "]")
                   .add("target=" + target)
                   .add("value=" + value)
                   .toString();
    }
}
