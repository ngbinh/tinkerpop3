package com.tinkerpop.gremlin.pipes;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Property;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.tinkergraph.TinkerFactory;
import com.tinkerpop.blueprints.tinkergraph.TinkerGraph;
import com.tinkerpop.gremlin.pipes.util.Holder;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GremlinTest extends TestCase {

    public void testPipeline() {

        TinkerGraph g = TinkerFactory.createClassic();
        Gremlin.of(g).V()
                .out("knows").out("created")
                .has("name")
                .value("name").path()
                .sideEffect(System.out::println).iterate();

        System.out.println("--------------");

        Gremlin.of(g).V().as("x").out("knows").back("x").path().sideEffect(System.out::println).iterate();

        System.out.println("--------------");

        new Gremlin<>(g.query().ids("1").vertices()).as("x").out()
                .loop("x", o -> ((Holder) o).getLoops() < 2, o -> false)
                .path().sideEffect(System.out::println).iterate();

        System.out.println("--------------");

        System.out.println(Gremlin.of(g).V().both().groupCount());

        System.out.println("--------------");

        Gremlin.of(g).V()
                .both()
                .dedup(e -> ((Element) ((Holder) e).get()).getProperty("name").isPresent())
                .sideEffect(System.out::println)
                .iterate();

        System.out.println("--------------");

    }

    public void testMatch() {
        TinkerGraph g = TinkerFactory.createClassic();
        Gremlin.of(g).V()
                .match("a", "d",
                        Gremlin.of().as("a").out("knows").as("b"),
                        Gremlin.of().as("b").out("created").as("c"),
                        Gremlin.of().as("c").value("name").as("d"))
                .sideEffect(System.out::println).iterate();
    }

    public void testLoop() {

        TinkerGraph g = new TinkerGraph();
        Vertex a = g.addVertex(Property.of(Property.Key.ID, "1"));
        Vertex b = g.addVertex(Property.of(Property.Key.ID, "2"));
        Vertex c = g.addVertex(Property.of(Property.Key.ID, "3"));
        Vertex d = g.addVertex(Property.of(Property.Key.ID, "4"));
        Vertex e = g.addVertex(Property.of(Property.Key.ID, "5"));
        Vertex f = g.addVertex(Property.of(Property.Key.ID, "6"));
        a.addEdge("next", b);
        b.addEdge("next", c);
        c.addEdge("next", d);
        d.addEdge("next", e);
        e.addEdge("next", f);
        f.addEdge("next", a);

        new Gremlin(Arrays.asList(a)).as("x").out()
                .loop("x", o -> ((Holder) o).getLoops() < 7, o -> true)
                .sideEffect(o -> System.out.println(((Holder) o).getLoops()))
                .path().sideEffect(System.out::println).iterate();
    }
}