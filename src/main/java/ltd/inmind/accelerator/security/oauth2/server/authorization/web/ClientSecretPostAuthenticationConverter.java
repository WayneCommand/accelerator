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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Attempts to extract client credentials from POST parameters of {@link HttpServletRequest}
 * and then converts to an {@link OAuth2ClientAuthenticationToken} used for authenticating the client.
 *
 * @author Anoop Garlapati
 * @author shenlanluck@gmail.com
 * @since 0.1.1
 * @see OAuth2ClientAuthenticationToken
 * @see OAuth2ClientAuthenticationFilter
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-2.3.1">Section 2.3.1 Client Password</a>
 */
public class ClientSecretPostAuthenticationConverter implements AuthenticationConverter {


	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		return exchange.getFormData()
				.flatMap(parameters -> {

					// client_id (REQUIRED)
					String clientId = parameters.getFirst(OAuth2ParameterNames.CLIENT_ID);
					if (!StringUtils.hasText(clientId)) {
						return null;
					}

					if (parameters.get(OAuth2ParameterNames.CLIENT_ID).size() != 1) {
						throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
					}

					// client_secret (REQUIRED)
					String clientSecret = parameters.getFirst(OAuth2ParameterNames.CLIENT_SECRET);
					if (!StringUtils.hasText(clientSecret)) {
						return null;
					}

					if (parameters.get(OAuth2ParameterNames.CLIENT_SECRET).size() != 1) {
						throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
					}

					return Mono.just(new OAuth2ClientAuthenticationToken(clientId, clientSecret, ClientAuthenticationMethod.POST,
							new HashMap<>(parameters.toSingleValueMap())));
				});
	}
}
