package com.tinkerpop.gremlin.util.function;

import java.util.Objects;

/**
 * Represents an operation that accepts two input arguments and returns no result. This is the quad-arity
 * specialization of {@link java.util.function.Consumer}. Unlike most other functional interfaces,
 * {@link HexConsumer} is expected to operate via side-effects.
 * <p/>
 * This is a functional interface whose functional method is
 * {@link #accept(Object, Object, Object, Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the operation
 * @param <B> the type of the second argument to the operation
 * @param <C> the type of the third argument to the operation
 * @param <D> the type of the fourth argument to the operation
 * @param <E> the type of the fifth argument to the operation
 * @param <F> the type of the sixth argument to the operation
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public interface HexConsumer<A,B,C,D,E,F> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first argument to the operation
     * @param b the second argument to the operation
     * @param c the third argument to the operation
     * @param d the fourth argument to the operation
     * @param e the fifth argument to the operation
     * @param f the sixth argument to the operation
     */
    public void accept(final A a, final B b, final C c, final D d, final E e, final F f);

    /**
     * Returns a composed @{link HexConsumer} that performs, in sequence, this operation followed by the {@code after}
     * operation. If performing either operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the after operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@link HexConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    public default HexConsumer<A, B, C, D, E, F> andThen(final HexConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d, E e, F f) -> {
            accept(a, b, c, d, e, f);
            after.accept(a, b, c, d, e, f);
        };
    }
}