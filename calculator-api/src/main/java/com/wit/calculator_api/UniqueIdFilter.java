package com.wit.calculator_api;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@WebFilter("/*")
public class UniqueIdFilter implements Filter {
    private static final String UNIQUE_ID_HEADER = "X-Unique-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Retrieve Correlation ID from the request header (if provided)
        String uniqueId = httpRequest.getHeader(UNIQUE_ID_HEADER);

        // If no correlation ID is provided, generate a new one
        if (uniqueId == null) {
            uniqueId = UUID.randomUUID().toString();
        }

        // Set the correlation ID in the response header
        httpResponse.setHeader(UNIQUE_ID_HEADER, uniqueId);

        // Log the correlation ID for debugging or tracing purposes (optional)
        System.out.println("Unique ID: " + uniqueId);

        filterChain.doFilter(request, response);
    }
}
