package ab.cgi;

/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import ab.compiler.ABAbstractCompilerEngine;
import ab.compiler.statement.Statement;
import ab.exception.InvalidFileException;
import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.activation.MimetypesFileTypeMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

/**
 * A simple handler that serves incoming HTTP requests to send their respective
 * HTTP responses.  It also implements {@code 'If-Modified-Since'} header to
 * take advantage of browser cache, as described in
 * <a href="http://tools.ietf.org/html/rfc2616#section-14.25">RFC 2616</a>.
 *
 * <h3>How Browser Caching Works</h3>
 *
 * Web browser caching works with HTTP headers as illustrated by the following
 * sample:
 * <ol>
 * <li>Request #1 returns the content of <code>/file1.txt</code>.</li>
 * <li>Contents of <code>/file1.txt</code> is cached by the browser.</li>
 * <li>Request #2 for <code>/file1.txt</code> does return the contents of the
 *     file again. Rather, a 304 Not Modified is returned. This tells the
 *     browser to use the contents stored in its cache.</li>
 * <li>The server knows the file has not been modified because the
 *     <code>If-Modified-Since</code> date is the same as the file's last
 *     modified date.</li>
 * </ol>
 *
 * <pre>
 * Request #1 Headers
 * ===================
 * GET /file1.txt HTTP/1.1
 *
 * Response #1 Headers
 * ===================
 * HTTP/1.1 200 OK
 * Date:               Tue, 01 Mar 2011 22:44:26 GMT
 * Last-Modified:      Wed, 30 Jun 2010 21:36:48 GMT
 * Expires:            Tue, 01 Mar 2012 22:44:26 GMT
 * Cache-Control:      private, max-age=31536000
 *
 * Request #2 Headers
 * ===================
 * GET /file1.txt HTTP/1.1
 * If-Modified-Since:  Wed, 30 Jun 2010 21:36:48 GMT
 *
 * Response #2 Headers
 * ===================
 * HTTP/1.1 304 Not Modified
 * Date:               Tue, 01 Mar 2011 22:44:28 GMT
 *
 * </pre>
 */
public class ABCgiServerHandler extends SimpleChannelUpstreamHandler
{

    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    private static final int HTTP_CACHE_SECONDS = 60;
    final String documentRoot = ab.cgi.Constant.DOCUMENT_ROOT;

    /**
     * untuk mengambil string dari URI request browser
     *
     * @param req HttpRequest
     * @return String
     */
    private String getURIRequest(HttpRequest req)
    {
        String uri = req.getUri();

        try
        {
            uri = URLDecoder.decode(uri, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            try
            {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            }
            catch(UnsupportedEncodingException e2)
            {
                e2.printStackTrace();
            }
        }
        return uri;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
    {
        HttpRequest request = (HttpRequest) e.getMessage();

        ab.compiler.ABAbstractCompilerEngine doABCompiler = new ABAbstractCompilerEngine()
        {

            public void beforeCompile(Statement statement, String urlHttpReq)
            {
                System.out.println("Before compile");
            }

            public void afterCompile(Statement statement, String urlHttpReq)
            {
                System.out.println("After compile");
            }
        };

        String str = null;
        try
        {
            str = doABCompiler.interpreteSource(new Statement(), documentRoot + getURIRequest(request));
        }
        catch(InvalidFileException ex)
        {
            str = ex.getMessage();
            ex.printStackTrace();
        }

        str = str == null ? "NULL" : str;

        HttpResponseStatus status = OK;
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(str, CharsetUtil.UTF_8));

        Channel ch = e.getChannel();
        ChannelFuture writeFuture;
        writeFuture = ch.write(response);
        writeFuture.addListener(new ChannelFutureProgressListener()
        {

            public void operationComplete(ChannelFuture future)
            {
                future.getChannel().close();
                System.out.println("Operation completed...");
            }

            public void operationProgressed(ChannelFuture future, long amount, long current, long total)
            {
                System.out.printf(" %d / %d (+%d)%n", current, total, amount);
            }
        });

        // Decide whether to close the connection or not.
        if(!isKeepAlive(request))
        {
            // Close the connection when the whole content is written out.
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception
    {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        if(cause instanceof TooLongFrameException)
        {
            sendErrorResponse(ctx, BAD_REQUEST);
            return;
        }

        cause.printStackTrace();
        if(ch.isConnected())
        {
            sendErrorResponse(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status)
    {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Error " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
    254      * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
    255      *
    256      * @param ctx
    257      *            Context
    258      */
    private static void sendNotModified(ChannelHandlerContext ctx)
    {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
    268      * Sets the Date header for the HTTP response
    269      *
    270      * @param response
    271      *            HTTP response
    272      */
    private static void setDateHeader(HttpResponse response)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.setHeader(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));
    }

    /**
    282      * Sets the Date and Cache headers for the HTTP Response
    283      *
    284      * @param response
    285      *            HTTP response
    286      * @param fileToCache
    287      *            file to extract content type
    288      */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.setHeader(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.setHeader(HttpHeaders.Names.EXPIRES, dateFormatter.format(time.getTime()));
        response.setHeader(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.setHeader(
                HttpHeaders.Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
    306      * Sets the content type header for the HTTP Response
    307      *
    308      * @param response
    309      *            HTTP response
    310      * @param file
    311      *            file to extract content type
    312      */
    private static void setContentTypeHeader(HttpResponse response, File file)
    {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}