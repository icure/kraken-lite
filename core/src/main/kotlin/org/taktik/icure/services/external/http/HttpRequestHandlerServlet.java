/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.http;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 * Based on org.springframework.web.context.support.HttpRequestHandlerServlet, without WebApplicationContext dependency
 */
public class HttpRequestHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private HttpRequestHandler httpRequestHandler;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LocaleContextHolder.setLocale(request.getLocale());
		try {
			if (httpRequestHandler != null) {
				this.httpRequestHandler.handleRequest(request, response);
			}
		} catch (HttpRequestMethodNotSupportedException ex) {
			String[] supportedMethods = ex.getSupportedMethods();
			if (supportedMethods != null) {
				response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
			}
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getMessage());
		} finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	public void setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
}
