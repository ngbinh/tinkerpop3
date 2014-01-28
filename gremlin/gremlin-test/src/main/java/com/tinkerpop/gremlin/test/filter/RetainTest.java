package com.tinkerpop.gremlin.test.filter;

import com.tinkerpop.blueprints.Vertex;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RetainTest {

    public void testCompliance() {
        assertTrue(true);
    }

    public void g_v1_out_retainXg_v2X(Iterator<Vertex> pipe) {
        assertEquals("vadas", pipe.next().<String>getValue("name"));
        assertFalse(pipe.hasNext());
    }

    public void g_v1_out_aggregateXxX_out_retainXxX(Iterator<Vertex> pipe) {
        assertEquals("lop", pipe.next().<String>getValue("name"));
        assertFalse(pipe.hasNext());
    }
}