package com.coindcx.springclient.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

/**
 * Filter to log all HTTP requests and responses
 */
@Component
public class HttpLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Wrap request and response to cache content
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);
            
            long startTime = System.currentTimeMillis();
            
            try {
                // Log incoming request
                logRequest(requestWrapper);
                
                // Continue the filter chain
                chain.doFilter(requestWrapper, responseWrapper);
                
                // Log outgoing response
                long duration = System.currentTimeMillis() - startTime;
                logResponse(responseWrapper, duration);
                
            } finally {
                // Copy cached content to actual response
                responseWrapper.copyBodyToResponse();
            }
        } else {
            chain.doFilter(request, response);
        }
    }
    
    private void logRequest(ContentCachingRequestWrapper request) {
        logger.info("▶▶▶ INCOMING HTTP REQUEST ▶▶▶");
        logger.info("Method: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Remote Address: {}", request.getRemoteAddr());
        
        // Log headers
        logger.info("Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Don't log sensitive headers
            if (!headerName.toLowerCase().contains("secret") && 
                !headerName.toLowerCase().contains("password") &&
                !headerName.toLowerCase().contains("authorization")) {
                logger.info("  {}: {}", headerName, request.getHeader(headerName));
            } else {
                logger.info("  {}: ********", headerName);
            }
        }
        
        // Log query parameters
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            logger.info("Query String: {}", queryString);
        }
        
        // Log request body
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = getContentAsString(content, request.getCharacterEncoding());
            if (body.length() > 1000) {
                body = body.substring(0, 1000) + "... (truncated)";
            }
            logger.info("Request Body: {}", body);
        }
    }
    
    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        logger.info("◀◀◀ OUTGOING HTTP RESPONSE ◀◀◀");
        logger.info("Status: {}", response.getStatus());
        logger.info("Duration: {}ms", duration);
        
        // Log response headers
        logger.info("Headers:");
        response.getHeaderNames().forEach(headerName -> {
            logger.info("  {}: {}", headerName, response.getHeader(headerName));
        });
        
        // Log response body
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = getContentAsString(content, response.getCharacterEncoding());
            if (body.length() > 1000) {
                body = body.substring(0, 1000) + "... (truncated)";
            }
            logger.info("Response Body: {}", body);
        }
        
        logger.info("════════════════════════════════════════════════════════════");
    }
    
    private String getContentAsString(byte[] content, String encoding) {
        try {
            return new String(content, encoding == null ? "UTF-8" : encoding);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Unsupported encoding: {}", encoding, e);
            return "[Unable to decode content]";
        }
    }
}
