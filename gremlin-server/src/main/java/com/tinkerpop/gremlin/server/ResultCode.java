package com.tinkerpop.gremlin.server;

/**
 * Result codes for Gremlin Server responses. Result codes tend to map to
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">HTTP status codes</a>.  It is not a one-to-one
 * mapping and there are custom status codes to be considered.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public enum ResultCode {
    /**
     * The server successfully processed a request.
     */
    SUCCESS(200),

    /**
     * The server is terminating a result set successfully.
     */
    SUCCESS_TERMINATOR(299),

    /**
     * The request message was not properly formatted which means it could not be parsed at all or the "op" code was
     * not recognized such that Gremlin Server could properly route it for processing.  Check the message format and
     * retry the request.
     */
    REQUEST_ERROR_MALFORMED_REQUEST(498),

    /**
     * The request message was parseable, but the arguments supplied in the message were in conflict or incomplete.
     * Check the message format and retry the request.
     */
    REQUEST_ERROR_INVALID_REQUEST_ARGUMENTS(499),

    /**
     * A general server error occurred that prevented the request from being processed.
     */
    SERVER_ERROR(500),

    /**
     * The script submitted for processing evaluated in the {@code ScriptEngine} with errors and could not be
     * processed.  Check the script submitted for syntax errors or other problems and then resubmit.
     */
    SERVER_ERROR_SCRIPT_EVALUATION(597),

    /**
     * The server exceeded one of the timeout settings for the request and could therefore only partially respond
     * or not respond at all.
     */
    SERVER_ERROR_TIMEOUT(598),

    /**
     * The server was not capable of serializing an object that was returned from the script supplied on the request.
     * Either transform the object into something Gremlin Server can process within the script or install custom
     * serialization classes to Gremlin Server.
     *
     * @see MessageSerializer
     * @see com.tinkerpop.gremlin.server.util.ser.JsonMessageSerializerV1d0
     */
    SERVER_ERROR_SERIALIZATION(599);


    private final int value;

    private ResultCode(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isSuccess() {
        return String.valueOf(this.value).startsWith("2");
    }
}
