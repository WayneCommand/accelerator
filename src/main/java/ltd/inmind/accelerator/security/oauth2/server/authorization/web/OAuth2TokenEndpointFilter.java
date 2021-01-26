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

import ltd.inmind.accelerator.security.oauth2.server.authorization.authentication.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@code Filter} for the OAuth 2.0 Token endpoint,
 * which handles the processing of an OAuth 2.0 Authorization Grant.
 *
 * <p>
 * It converts the OAuth 2.0 Authorization Grant request to an {@link Authentication},
 * which is then authenticated by the {@link AuthenticationManager}.
 * If the authentication succeeds, the {@link AuthenticationManager} returns an
 * {@link OAuth2AccessTokenAuthenticationToken}, which is returned in the OAuth 2.0 Access Token response.
 * In case of any error, an {@link OAuth2Error} is returned in the OAuth 2.0 Error response.
 *
 * <p>
 * By default, this {@code Filter} responds to authorization grant requests
 * at the {@code URI} {@code /oauth2/token} and {@code HttpMethod} {@code POST}.
 *
 * <p>
 * The default endpoint {@code URI} {@code /oauth2/token} may be overridden
 * via the constructor {@link #OAuth2TokenEndpointFilter(AuthenticationManager, String)}.
 *
 * @author Joe Grandja
 * @author Madhu Bhat
 * @author Daniel Garnier-Moiroux
 * @author shenlanluck@gmail.com
 * @since 0.0.2
 * @see AuthenticationManager
 * @see OAuth2AuthorizationCodeAuthenticationProvider
 * @see OAuth2RefreshTokenAuthenticationProvider
 * @see OAuth2ClientCredentialsAuthenticationProvider
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-3.2">Section 3.2 Token Endpoint</a>
 */
public class OAuth2TokenEndpointFilter implements WebFilter {
	/**
	 * The default endpoint {@code URI} for access token requests.
	 */
	public static final String DEFAULT_TOKEN_ENDPOINT_URI = "/oauth2/token";

	private final AuthenticationManager authenticationManager;
	private final ServerWebExchangeMatcher tokenEndpointMatcher;
	private final Converter<ServerWebExchange, Mono<Authentication>> authorizationGrantAuthenticationConverter;

	/**
	 * Constructs an {@code OAuth2TokenEndpointFilter} using the provided parameters.
	 *
	 * @param authenticationManager the authentication manager
	 */
	public OAuth2TokenEndpointFilter(AuthenticationManager authenticationManager) {
		this(authenticationManager, DEFAULT_TOKEN_ENDPOINT_URI);
	}

	/**
	 * Constructs an {@code OAuth2TokenEndpointFilter} using the provided parameters.
	 *
	 * @param authenticationManager the authentication manager
	 * @param tokenEndpointUri the endpoint {@code URI} for access token requests
	 */
	public OAuth2TokenEndpointFilter(AuthenticationManager authenticationManager, String tokenEndpointUri) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		Assert.hasText(tokenEndpointUri, "tokenEndpointUri cannot be empty");
		this.authenticationManager = authenticationManager;
		this.tokenEndpointMatcher = ServerWebExchangeMatchers
				.pathMatchers(HttpMethod.POST, tokenEndpointUri);
		Map<AuthorizationGrantType, Converter<ServerWebExchange, Mono<Authentication>>> converters = new HashMap<>();
		converters.put(AuthorizationGrantType.AUTHORIZATION_CODE, new AuthorizationCodeAuthenticationConverter());
		converters.put(AuthorizationGrantType.REFRESH_TOKEN, new RefreshTokenAuthenticationConverter());
		converters.put(AuthorizationGrantType.CLIENT_CREDENTIALS, new ClientCredentialsAuthenticationConverter());
		this.authorizationGrantAuthenticationConverter = new DelegatingAuthorizationGrantAuthenticationConverter(converters);
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		this.tokenEndpointMatcher.matches(exchange)
				.filter(ServerWebExchangeMatcher.MatchResult::isMatch)
				.flatMap(r -> {

					return exchange.getFormData()
							.flatMap(parameters -> {
								String grantType = parameters.getFirst(OAuth2ParameterNames.GRANT_TYPE);
								if (!StringUtils.hasText(grantType))
									throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.GRANT_TYPE);

								return this.authorizationGrantAuthenticationConverter.convert(exchange)
										.or(Mono.empty());
							});
				})
				.flatMap(accessTokenAuthentication -> {
					OAuth2AccessTokenAuthenticationToken authenticationResult = (OAuth2AccessTokenAuthenticationToken) this.authenticationManager.authenticate(accessTokenAuthentication);

					return this.sendAccessTokenResponse(exchange, authenticationResult);
				})
				.onErrorResume(OAuth2AuthenticationException.class, e -> {
					ReactiveSecurityContextHolder.clearContext();
					exchange.getResponse()
							.setStatusCode(HttpStatus.BAD_REQUEST);
					return Mono.empty();
				});






		return null;
	}

	private Mono<Void> sendAccessTokenResponse(ServerWebExchange exchange, OAuth2AccessTokenAuthenticationToken accessTokenAuthentication) {

		OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
		OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
		Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

		OAuth2AccessTokenResponse.Builder builder =
				OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
						.tokenType(accessToken.getTokenType())
						.scopes(accessToken.getScopes());
		if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
			builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
		}
		if (refreshToken != null) {
			builder.refreshToken(refreshToken.getTokenValue());
		}
		if (!CollectionUtils.isEmpty(additionalParameters)) {
			builder.additionalParameters(additionalParameters);
		}
		OAuth2AccessTokenResponse accessTokenResponse = builder.build();

		ServerHttpResponse httpResponse = exchange.getResponse();

		// TODO 想办法把 accessTokenResponse 返回
		DataBuffer buffer = httpResponse.bufferFactory().allocateBuffer();

		return httpResponse.writeWith(Mono.just(buffer));

	}

	private Mono<Void> sendErrorResponse(ServerWebExchange exchange, OAuth2Error error) {
		ServerHttpResponse httpResponse = exchange.getResponse();
		httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);

		// TODO 想办法把 error 返回
		DataBuffer buffer = httpResponse.bufferFactory().allocateBuffer();

		return httpResponse.writeWith(Mono.just(buffer));
	}

	private static void throwError(String errorCode, String parameterName) {
		OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName,
				"https://tools.ietf.org/html/rfc6749#section-5.2");
		throw new OAuth2AuthenticationException(error);
	}



	private static class AuthorizationCodeAuthenticationConverter implements Converter<ServerWebExchange, Mono<Authentication>> {

		@Override
		public Mono<Authentication> convert(ServerWebExchange exchange) {

			return exchange.getFormData()
					.flatMap(parameters -> {
						// grant_type (REQUIRED)
						String grantType = parameters.getFirst(OAuth2ParameterNames.GRANT_TYPE);
						if (!AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(grantType)) {
							return Mono.empty();
						}

						// code (REQUIRED)
						String code = parameters.getFirst(OAuth2ParameterNames.CODE);
						if (!StringUtils.hasText(code) ||
								parameters.get(OAuth2ParameterNames.CODE).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.CODE);
						}

						// redirect_uri (REQUIRED)
						// Required only if the "redirect_uri" parameter was included in the authorization request
						String redirectUri = parameters.getFirst(OAuth2ParameterNames.REDIRECT_URI);
						if (StringUtils.hasText(redirectUri) &&
								parameters.get(OAuth2ParameterNames.REDIRECT_URI).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.REDIRECT_URI);
						}

						Map<String, Object> additionalParameters = parameters
								.entrySet()
								.stream()
								.filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE) &&
										!e.getKey().equals(OAuth2ParameterNames.CLIENT_ID) &&
										!e.getKey().equals(OAuth2ParameterNames.CODE) &&
										!e.getKey().equals(OAuth2ParameterNames.REDIRECT_URI))
								.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

						return ReactiveSecurityContextHolder.getContext()
								.map(SecurityContext::getAuthentication)
								.map(Authentication::getPrincipal)
								.cast(Authentication.class)
								.map(clientPrincipal -> new OAuth2AuthorizationCodeAuthenticationToken(code, clientPrincipal, redirectUri, additionalParameters));
					});
		}
	}

	private static class RefreshTokenAuthenticationConverter implements Converter<ServerWebExchange, Mono<Authentication>> {

		@Override
		public Mono<Authentication> convert(ServerWebExchange exchange) {

			return exchange.getFormData()
					.flatMap(parameters -> {
						// grant_type (REQUIRED)
						String grantType = parameters.getFirst(OAuth2ParameterNames.GRANT_TYPE);
						if (!AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
							return Mono.empty();
						}

						// refresh_token (REQUIRED)
						String refreshToken = parameters.getFirst(OAuth2ParameterNames.REFRESH_TOKEN);
						if (!StringUtils.hasText(refreshToken) ||
								parameters.get(OAuth2ParameterNames.REFRESH_TOKEN).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.REFRESH_TOKEN);
						}

						// scope (OPTIONAL)
						String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
						if (StringUtils.hasText(scope) &&
								parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE);
						}

						if (StringUtils.hasText(scope)) {
							Set<String> requestedScopes = new HashSet<>(
									Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));

							return ReactiveSecurityContextHolder.getContext()
									.map(SecurityContext::getAuthentication)
									.map(Authentication::getPrincipal)
									.cast(Authentication.class)
									.map(clientPrincipal -> new OAuth2RefreshTokenAuthenticationToken(refreshToken, clientPrincipal, requestedScopes));
						}

						return ReactiveSecurityContextHolder.getContext()
								.map(SecurityContext::getAuthentication)
								.map(Authentication::getPrincipal)
								.cast(Authentication.class)
								.map(clientPrincipal -> new OAuth2RefreshTokenAuthenticationToken(refreshToken, clientPrincipal));

					});
		}
	}

	private static class ClientCredentialsAuthenticationConverter implements Converter<ServerWebExchange, Mono<Authentication>> {

		@Override
		public Mono<Authentication> convert(ServerWebExchange exchange) {

			return exchange.getFormData()
					.flatMap(parameters -> {
						String grantType = parameters.getFirst(OAuth2ParameterNames.GRANT_TYPE);

						if (!AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(grantType)) {
							return Mono.empty();
						}

						// scope (OPTIONAL)
						String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
						if (StringUtils.hasText(scope) &&
								parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
							throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE);
						}
						if (StringUtils.hasText(scope)) {
							Set<String> requestedScopes = new HashSet<>(
									Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));

							return ReactiveSecurityContextHolder.getContext()
									.map(SecurityContext::getAuthentication)
									.map(Authentication::getPrincipal)
									.cast(Authentication.class)
									.map(clientPrincipal -> new OAuth2ClientCredentialsAuthenticationToken(clientPrincipal, requestedScopes));
						}

						return ReactiveSecurityContextHolder.getContext()
								.map(SecurityContext::getAuthentication)
								.map(Authentication::getPrincipal)
								.cast(Authentication.class)
								.map(OAuth2ClientCredentialsAuthenticationToken::new);
					});
		}

	}
}
