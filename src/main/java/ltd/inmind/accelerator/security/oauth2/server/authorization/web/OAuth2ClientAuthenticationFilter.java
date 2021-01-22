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

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A {@code Filter} that processes an authentication request for an OAuth 2.0 Client.
 *
 * @author Joe Grandja
 * @author Patryk Kostrzewa
 * @author shenlanluck@gmail.com
 * @since 0.0.2
 * @see AuthenticationManager
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-2.3">Section 2.3 Client Authentication</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-3.2.1">Section 3.2.1 Token Endpoint Client Authentication</a>
 */
public class OAuth2ClientAuthenticationFilter implements WebFilter {
	private final AuthenticationManager authenticationManager;
	private final ServerWebExchangeMatcher exchangeMatcher;
	private AuthenticationConverter authenticationConverter;
	private ServerAuthenticationSuccessHandler authenticationSuccessHandler;
	private ServerAuthenticationFailureHandler authenticationFailureHandler;

	/**
	 * Constructs an {@code OAuth2ClientAuthenticationFilter} using the provided parameters.
	 *
	 * @param authenticationManager the {@link AuthenticationManager} used for authenticating the client
	 * @param exchangeMatcher the {@link ServerWebExchangeMatcher} used for matching against the {@code ServerWebExchange}
	 */
	public OAuth2ClientAuthenticationFilter(AuthenticationManager authenticationManager,
											ServerWebExchangeMatcher exchangeMatcher) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		Assert.notNull(exchangeMatcher, "exchangeMatcher cannot be null");
		this.authenticationManager = authenticationManager;
		this.exchangeMatcher = exchangeMatcher;
		this.authenticationConverter = new DelegatingAuthenticationConverter(
				Arrays.asList(
						new ClientSecretBasicAuthenticationConverter(),
						new ClientSecretPostAuthenticationConverter(),
						new PublicClientAuthenticationConverter()));
		this.authenticationSuccessHandler = this::onAuthenticationSuccess;
		this.authenticationFailureHandler = this::onAuthenticationFailure;
	}
	/**
	 * Sets the {@link AuthenticationConverter} used for converting a {@link ServerWebExchange} to an {@link Authentication}.
	 *
	 * @param authenticationConverter used for converting a {@link ServerWebExchange} to an {@link Authentication}
	 */
	public final void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
		Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
		this.authenticationConverter = authenticationConverter;
	}

	/**
	 * Sets the {@link ServerAuthenticationSuccessHandler} used for handling successful authentications.
	 *
	 * @param authenticationSuccessHandler the {@link ServerAuthenticationSuccessHandler} used for handling successful authentications
	 */
	public final void setAuthenticationSuccessHandler(ServerAuthenticationSuccessHandler authenticationSuccessHandler) {
		Assert.notNull(authenticationSuccessHandler, "authenticationSuccessHandler cannot be null");
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}

	/**
	 * Sets the {@link ServerAuthenticationFailureHandler} used for handling failed authentications.
	 *
	 * @param authenticationFailureHandler the {@link ServerAuthenticationFailureHandler} used for handling failed authentications
	 */
	public final void setAuthenticationFailureHandler(ServerAuthenticationFailureHandler authenticationFailureHandler) {
		Assert.notNull(authenticationFailureHandler, "authenticationFailureHandler cannot be null");
		this.authenticationFailureHandler = authenticationFailureHandler;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		WebFilterExchange webFilterExchange = new WebFilterExchange(exchange, chain);

		return this.exchangeMatcher.matches(exchange)
				.filter(ServerWebExchangeMatcher.MatchResult::isMatch)
				.flatMap(matchResult -> this.authenticationConverter.convert(exchange))
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				.flatMap(token -> {
					Authentication authenticationResult = this.authenticationManager.authenticate(token);
					return this.authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authenticationResult);
				})
				.onErrorResume(OAuth2AuthenticationException.class, e -> this.authenticationFailureHandler
						.onAuthenticationFailure(webFilterExchange, e));
	}

	private Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {

		return Mono.empty()
				.then()
				.contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
	}

	private Mono<Void> onAuthenticationFailure(WebFilterExchange exchange, AuthenticationException failed) {

		// TODO
		// The authorization server MAY return an HTTP 401 (Unauthorized) status code
		// to indicate which HTTP authentication schemes are supported.
		// If the client attempted to authenticate via the "Authorization" request header field,
		// the authorization server MUST respond with an HTTP 401 (Unauthorized) status code and
		// include the "WWW-Authenticate" response header field
		// matching the authentication scheme used by the client.

		OAuth2Error error = ((OAuth2AuthenticationException) failed).getError();

		ServerHttpResponse httpResponse = exchange.getExchange().getResponse();


		if (OAuth2ErrorCodes.INVALID_CLIENT.equals(error.getErrorCode())) {
			httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
		} else {
			httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
		}

		DataBuffer buffer = httpResponse.bufferFactory().wrap(error.getDescription().getBytes(StandardCharsets.UTF_8));

		return httpResponse.writeWith(Mono.just(buffer));
	}
}
