package com.tinkerpop.gremlin.tinkergraph.process.graph.map;

import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.graph.map.FlatMapStep;
import com.tinkerpop.gremlin.structure.AnnotatedList;
import com.tinkerpop.gremlin.structure.AnnotatedValue;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerAnnotatedList;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TinkerAnnotatedListStep<V> extends FlatMapStep<AnnotatedList<V>, AnnotatedValue<V>> {

    public TinkerAnnotatedListStep(final Traversal traversal) {
        super(traversal);
        this.setFunction(holder -> TinkerHelper.getAnnotatedValues((TinkerAnnotatedList) holder.get()));
    }
}
