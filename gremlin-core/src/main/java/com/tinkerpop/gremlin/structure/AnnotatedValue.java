package com.tinkerpop.gremlin.structure;

import com.tinkerpop.gremlin.structure.util.AnnotatedValueHelper;

import java.util.Optional;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface AnnotatedValue<V> {

    public static final String VALUE = "value";

    public class Key {

        private Key() {
        }

        public static String hidden(final String key) {
            return Graph.HIDDEN_PREFIX.concat(key);
        }
    }

    public void remove();

    public V getValue();

    public void setAnnotation(final String key, final Object value);

    public <T> Optional<T> getAnnotation(final String key);

    public void removeAnnotation(final String key);

    public Set<String> getAnnotationKeys();

    public default void setAnnotations(final Object... keyValues) {
        AnnotatedValueHelper.legalAnnotationKeyValueArray(keyValues);
        AnnotatedValueHelper.attachAnnotations(this, keyValues);
    }

    public static class Exceptions {

        public static IllegalArgumentException providedKeyValuesMustBeAMultipleOfTwo() {
            return new IllegalArgumentException("The provided annotation key/value array must be a multiple of two");
        }

        public static IllegalArgumentException providedKeyValuesMustHaveAStringOnEvenIndices() {
            return new IllegalArgumentException("The provided annotation key/value array must have a String key on even array indices");
        }

        public static IllegalArgumentException annotatedValueCanNotBeNull() {
            return new IllegalArgumentException("Annotated value can not be null");
        }

        public static IllegalArgumentException annotationKeyIsReserved(final String key) {
            return new IllegalArgumentException("Annotation key is reserved: " + key);
        }

        public static IllegalArgumentException annotationKeyValueIsReserved() {
            return annotationKeyIsReserved(VALUE);
        }

        public static IllegalArgumentException annotationKeyCanNotBeEmpty() {
            return new IllegalArgumentException("Annotation key can not be the empty string");
        }

        public static IllegalArgumentException annotationKeyCanNotBeNull() {
            return new IllegalArgumentException("Annotation key can not be null");
        }

        public static IllegalArgumentException annotationValueCanNotBeNull() {
            return new IllegalArgumentException("Annotation value can not be null");
        }

        public static UnsupportedOperationException dataTypeOfAnnotatedValueNotSupported(final Object val) {
            return new UnsupportedOperationException(String.format("Annotated value [%s] is of type %s is not supported", val, val.getClass()));
        }
    }
}
