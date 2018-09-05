package com.wordcount.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.wordcount.api.utilities.CSVWriterUtils;

@Component
@PropertySource("classpath:/config/global.properties")
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${security.realm}")
	private String realmName;

	/**
	 * Defines the response headers, status and error message in case of Authentication failure.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
			throws IOException {
		logger.error("User unauthorized to access API: {}", authEx.getMessage());

		// Set Response Headers/Status
		response.addHeader("WWW-Authenticate", "Basic realm - " + getRealmName());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		// If CSV response required then write error message in CSV
		if ("text/csv".equals(request.getHeader("Accept"))) {
			response.setContentType("text/csv");
			CSVWriterUtils.writeOject(authEx, new String[] { "message" }, response.getWriter());
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authEx.getMessage());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setRealmName(this.realmName);
 		super.afterPropertiesSet();
	}
}
