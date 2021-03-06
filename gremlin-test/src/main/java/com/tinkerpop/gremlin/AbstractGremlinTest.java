package com.tinkerpop.gremlin;

import com.tinkerpop.gremlin.structure.FeatureRequirement;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.structure.io.GraphReader;
import com.tinkerpop.gremlin.structure.io.kryo.KryoReader;
import com.tinkerpop.gremlin.structure.strategy.GraphStrategy;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

/**
 * Sets up g based on the current graph configuration and checks required features for the test.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public abstract class AbstractGremlinTest {
    protected Graph g;
    protected Configuration config;
    protected Optional<? extends GraphStrategy> strategyToTest;
    protected GraphProvider graphProvider;

    @Rule
    public TestName name = new TestName();

    public AbstractGremlinTest() {
        this(Optional.empty());
    }

    public AbstractGremlinTest(final Optional<? extends GraphStrategy> strategyToTest) {
        this.strategyToTest = strategyToTest;
    }

    @Before
    public void setup() throws Exception {
        graphProvider = GraphManager.get();
        config = graphProvider.standardGraphConfiguration();

        // this should clear state from a previously unfinished test. since the graph does not yet exist,
        // persisted graphs will likely just have their directories removed
        graphProvider.clear(config);

        // not sure how the strategy can ever be null, but it seems to happen in the performance tests
        g = graphProvider.openTestGraph(config, strategyToTest == null ? Optional.empty() : strategyToTest);

        final Method testMethod = this.getClass().getMethod(cleanMethodName(name.getMethodName()));
        final FeatureRequirement[] featureRequirement = testMethod.getAnnotationsByType(FeatureRequirement.class);
        final List<FeatureRequirement> frs = Arrays.asList(featureRequirement);
        for (FeatureRequirement fr : frs) {
            try {
                //System.out.println(String.format("Assume that %s meets Feature Requirement - %s - with %s", fr.featureClass().getSimpleName(), fr.feature(), fr.supported()));
                assumeThat(g.getFeatures().supports(fr.featureClass(), fr.feature()), is(fr.supported()));
            } catch (NoSuchMethodException nsme) {
                throw new NoSuchMethodException(String.format("[supports%s] is not a valid feature on %s", fr.feature(), fr.featureClass()));
            } catch (Exception ex) {
                throw ex;
            }
        }

        final LoadGraphWith[] loadGraphWiths = testMethod.getAnnotationsByType(LoadGraphWith.class);
        final Optional<LoadGraphWith> loadGraphWith = loadGraphWiths.length == 0 ? Optional.empty() : Optional.of(loadGraphWiths[0]);
        loadGraphWith.ifPresent(lgw -> {
            try {
                readIntoGraph(g, lgw.value().location());
            } catch (IOException ioe) {
                throw new RuntimeException("Graph could not be loaded with data for test.");
            }
        });

        prepareGraph(g);
    }

    protected void prepareGraph(final Graph g) throws Exception {
        // do nothing
    }

    @After
    public void tearDown() throws Exception {
        graphProvider.clear(g, config);
        g = null;
        config = null;
        strategyToTest = null;
        graphProvider = null;
    }

    /**
     * Looks up the identifier as generated by the current source graph being tested.
     *
     * @param identifier a unique string that will identify a graph element within a graph
     * @return the id as generated by the graph
     */
    protected Object convertToId(final String identifier) {
        return convertToId(g, identifier);
    }

    /**
     * Looks up the identifier as generated by the current source graph being tested.
     *
     * @param g the graph to get the element id from
     * @param identifier a unique string that will identify a graph element within a graph
     * @return the id as generated by the graph
     */
    protected Object convertToId(final Graph g, final String identifier) {
        // all test graphs have "name" as a unique id which makes it easy to hardcode this...works for now
        return ((Vertex) g.V().has("name", identifier).next()).getId();
    }

    /**
     * Utility method that commits if the graph supports transactions.
     */
    protected void tryCommit(final Graph g) {
        if (g.getFeatures().graph().supportsTransactions())
            g.tx().commit();
    }

    /**
     * Utility method that commits if the graph supports transactions and executes an assertion function before and
     * after the commit.  It assumes that the assertion should be true before and after the commit.
     */
    protected void tryCommit(final Graph g, final Consumer<Graph> assertFunction) {
        assertFunction.accept(g);
        if (g.getFeatures().graph().supportsTransactions()) {
            g.tx().commit();
            assertFunction.accept(g);
        }
    }

    /**
     * Utility method that rollsback if the graph supports transactions.
     */
    protected void tryRollback(final Graph g) {
        if (g.getFeatures().graph().supportsTransactions())
            g.tx().rollback();
    }

    /**
     * If using "parameterized test" junit will append an identifier to the end of the method name which prevents it
     * from being found via reflection.  This method removes that suffix.
     */
    private static String cleanMethodName(final String methodName) {
        if (methodName.endsWith("]")) {
            return methodName.substring(0, methodName.indexOf("["));
        }

        return methodName;
    }

    private static void readIntoGraph(final Graph g, final String path) throws IOException {
        final GraphReader reader = KryoReader.create().setWorkingDirectory(File.separator + "tmp").build();
        try (final InputStream stream = AbstractGremlinTest.class.getResourceAsStream(path)) {
            reader.readGraph(stream, g);
        }
    }
}
