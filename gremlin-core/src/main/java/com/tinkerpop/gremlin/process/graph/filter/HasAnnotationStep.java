package com.tinkerpop.gremlin.process.graph.filter;

import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.util.TraversalHelper;
import com.tinkerpop.gremlin.structure.AnnotatedList;
import com.tinkerpop.gremlin.structure.AnnotatedValue;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;
import com.tinkerpop.gremlin.structure.util.HasContainer;
import com.tinkerpop.gremlin.util.StreamFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class HasAnnotationStep extends FilterStep<Element> {

    public HasContainer hasContainer;
    public String propertyKey;

    public HasAnnotationStep(final Traversal traversal, final String propertyKey, final HasContainer hasContainer) {
        super(traversal);
        this.propertyKey = propertyKey;
        this.hasContainer = hasContainer;
        this.setPredicate(holder -> {
            final Property<AnnotatedList> property = holder.get().getProperty(this.propertyKey);
            return property.isPresent() &&
                    StreamFactory.stream(property.get().values()).filter(av -> this.hasContainer.test((AnnotatedValue) av)).iterator().hasNext();
        });
    }

    public String toString() {
        return TraversalHelper.makeStepString(this, this.propertyKey, this.hasContainer);
    }
}
