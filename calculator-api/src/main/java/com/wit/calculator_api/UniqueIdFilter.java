package com.wit.calculator_api;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@WebFilter("/*")
public class UniqueIdFilter implements Filter {
    @Value("${uniqueIdHeaderKey}")
    private String UNIQUE_ID_HEADER;
    @Value("${mdcUniqueIdKey}")
    private String MDC_UNIQUE_ID;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uniqueId = httpRequest.getHeader(UNIQUE_ID_HEADER);
        if (uniqueId == null) {
            uniqueId = UUID.randomUUID().toString();
        }

        System.out.println("VOU METER " + MDC_UNIQUE_ID + ": "+uniqueId );

        MDC.put(MDC_UNIQUE_ID, uniqueId);

        httpResponse.setHeader(UNIQUE_ID_HEADER, uniqueId);

        filterChain.doFilter(request, response);

        MDC.remove(MDC_UNIQUE_ID);
    }
}
