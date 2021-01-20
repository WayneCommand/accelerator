/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltd.inmind.accelerator.security.oauth2.server.authorization.web;

import ltd.inmind.accelerator.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Attempts to extract HTTP Basic credentials from {@link ServerWebExchange}
 * and then converts to an {@link OAuth2ClientAuthenticationToken} used for authenticating the client.
 *
 * @author Patryk Kostrzewa
 * @author Joe Grandja
 * @author shenlanluck@gmail.com
 * @since 0.0.2
 * @see OAuth2ClientAuthenticationToken
 * @see OAuth2ClientAuthenticationFilter
 */
public class ClientSecretBasicAuthenticationConverter implements AuthenticationConverter {

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {

		List<String> header = exchange.getRequest()
				.getHeaders()
				.get(HttpHeaders.AUTHORIZATION);
		if (header == null || header.isEmpty()) {
			return null;
		}

		String[] parts = header.get(0).split("\\s");
		if (!parts[0].equalsIgnoreCase("Basic")) {
			return null;
		}

		if (parts.length != 2) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
		}

		byte[] decodedCredentials;
		try {
			decodedCredentials = Base64.getDecoder().decode(
					parts[1].getBytes(StandardCharsets.UTF_8));
		} catch (IllegalArgumentException ex) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST), ex);
		}

		String credentialsString = new String(decodedCredentials, StandardCharsets.UTF_8);
		String[] credentials = credentialsString.split(":", 2);
		if (credentials.length != 2 ||
				!StringUtils.hasText(credentials[0]) ||
				!StringUtils.hasText(credentials[1])) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
		}

		String clientID;
		String clientSecret;
		try {
			clientID = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8.name());
			clientSecret = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8.name());
		} catch (Exception ex) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST), ex);
		}

		return exchange.getFormData()
				.flatMap(form -> {
					return Mono.just(new OAuth2ClientAuthenticationToken(clientID, clientSecret, ClientAuthenticationMethod.BASIC,
							new HashMap<>(form.toSingleValueMap())));
				});

	}
}
