package com.game.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.game.web.WebConfig;

public class TokenFilter implements Filter {

	private static final Logger	LOG					= LoggerFactory.getLogger(TokenFilter.class);

	public static final String	CHARSET				= "UTF-8";

	WebConfig					WebConfig;

	// URL 过期时间
	public static final long	REQUEST_URL_EXPIRED	= 1000 * 1800;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(filterConfig
				.getServletContext());
		WebConfig = (WebConfig) ctx.getBean("WebConfig");
	}

	@Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException,
                                                                                             ServletException {
    	HttpServletRequest request = (HttpServletRequest)arg0;
    	HttpServletResponse response = (HttpServletResponse)arg1;
    	
    	LOG.info("request url:" + request.getRequestURL().toString());
    	
    }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}