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

import ltd.inmind.accelerator.security.oauth2.core.endpoint.OAuth2ParameterNames2;
import ltd.inmind.accelerator.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationToken;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * A {@code Filter} for the OAuth 2.0 Token Revocation endpoint.
 *
 * @author Vivek Babu
 * @author Joe Grandja
 * @author shenlanluck@gmail.com
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7009#section-2">Section 2 Token Revocation</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7009#section-2.1">Section 2.1 Revocation Request</a>
 * @since 0.0.3
 */
public class OAuth2TokenRevocationEndpointFilter implements WebFilter {
	/**
	 * The default endpoint {@code URI} for token revocation requests.
	 */
	public static final String DEFAULT_TOKEN_REVOCATION_ENDPOINT_URI = "/oauth2/revoke";

	private final AuthenticationManager authenticationManager;
	private final ServerWebExchangeMatcher tokenRevocationEndpointMatcher;
	private final AuthenticationConverter tokenRevocationAuthenticationConverter =
			new DefaultTokenRevocationAuthenticationConverter();
	private final HttpMessageConverter<OAuth2Error> errorHttpResponseConverter =
			new OAuth2ErrorHttpMessageConverter();

	/**
	 * Constructs an {@code OAuth2TokenRevocationEndpointFilter} using the provided parameters.
	 *
	 * @param authenticationManager the authentication manager
	 */
	public OAuth2TokenRevocationEndpointFilter(AuthenticationManager authenticationManager) {
		this(authenticationManager, DEFAULT_TOKEN_REVOCATION_ENDPOINT_URI);
	}

	/**
	 * Constructs an {@code OAuth2TokenRevocationEndpointFilter} using the provided parameters.
	 *
	 * @param authenticationManager the authentication manager
	 * @param tokenRevocationEndpointUri the endpoint {@code URI} for token revocation requests
	 */
	public OAuth2TokenRevocationEndpointFilter(AuthenticationManager authenticationManager,
			String tokenRevocationEndpointUri) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		Assert.hasText(tokenRevocationEndpointUri, "tokenRevocationEndpointUri cannot be empty");
		this.authenticationManager = authenticationManager;
		this.tokenRevocationEndpointMatcher = ServerWebExchangeMatchers
				.pathMatchers(HttpMethod.POST, tokenRevocationEndpointUri);
	}


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return this.tokenRevocationEndpointMatcher.matches(exchange)
				.filter(ServerWebExchangeMatcher.MatchResult::isMatch)
				.flatMap(matchResult -> this.tokenRevocationAuthenticationConverter.convert(exchange))
				.doOnNext(this.authenticationManager::authenticate)  // TODO 可能不是这么写的
				.doOnSuccess(authentication -> exchange.getResponse().setStatusCode(HttpStatus.OK))
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				.then()
				.onErrorResume(OAuth2AuthenticationException.class, error -> {
					ServerHttpResponse httpResponse = exchange.getResponse();
					httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
					DataBuffer buffer = httpResponse.bufferFactory().wrap(error.getMessage().getBytes(StandardCharsets.UTF_8));

					return httpResponse.writeWith(Mono.just(buffer))
							.doOnNext(next -> {
								// TODO 似乎不是这样写的
								ReactiveSecurityContextHolder.clearContext();
							});
				});

	}

	private static void throwError(String errorCode, String parameterName) {
		OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Token Revocation Parameter: " + parameterName,
				"https://tools.ietf.org/html/rfc7009#section-2.1");
		throw new OAuth2AuthenticationException(error);
	}

	private static class DefaultTokenRevocationAuthenticationConverter
			implements AuthenticationConverter {

		@Override
		public Mono<Authentication> convert(ServerWebExchange exchange) {
			return exchange.getFormData()
					.flatMap(parameters -> {

						// token (REQUIRED)
						String token = parameters.getFirst(OAuth2ParameterNames2.TOKEN);
						if (!StringUtils.hasText(token) ||
								parameters.get(OAuth2ParameterNames2.TOKEN).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames2.TOKEN);
						}

						// token_type_hint (OPTIONAL)
						String tokenTypeHint = parameters.getFirst(OAuth2ParameterNames2.TOKEN_TYPE_HINT);
						if (StringUtils.hasText(tokenTypeHint) &&
								parameters.get(OAuth2ParameterNames2.TOKEN_TYPE_HINT).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames2.TOKEN_TYPE_HINT);
						}

						return ReactiveSecurityContextHolder.getContext()
								.map(SecurityContext::getAuthentication)
								.map(Authentication::getPrincipal)
								.cast(Authentication.class)
								.map(clientPrincipal -> new OAuth2TokenRevocationAuthenticationToken(token, clientPrincipal, tokenTypeHint));
					});

		}
	}
}
