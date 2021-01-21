/*
 * Copyright 2020-2021 the original author or authors.
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

import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * A {@code Filter} that processes JWK Set requests.
 *
 * @author Joe Grandja
 * @author shenlanluck@gmail.com
 * @since 0.0.2
 * @see JWKSource
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7517">JSON Web Key (JWK)</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7517#section-5">Section 5 JWK Set Format</a>
 */
public class NimbusJwkSetEndpointFilter implements WebFilter {
	/**
	 * The default endpoint {@code URI} for JWK Set requests.
	 */
	public static final String DEFAULT_JWK_SET_ENDPOINT_URI = "/oauth2/jwks";

	private final JWKSource<SecurityContext> jwkSource;
	private final JWKSelector jwkSelector;
	private final ServerWebExchangeMatcher exchangeMatcher;

	/**
	 * Constructs a {@code NimbusJwkSetEndpointFilter} using the provided parameters.
	 * @param jwkSource the {@code com.nimbusds.jose.jwk.source.JWKSource}
	 */
	public NimbusJwkSetEndpointFilter(JWKSource<SecurityContext> jwkSource) {
		this(jwkSource, DEFAULT_JWK_SET_ENDPOINT_URI);
	}

	/**
	 * Constructs a {@code NimbusJwkSetEndpointFilter} using the provided parameters.
	 *
	 * @param jwkSource the {@code com.nimbusds.jose.jwk.source.JWKSource}
	 * @param jwkSetEndpointUri the endpoint {@code URI} for JWK Set requests
	 */
	public NimbusJwkSetEndpointFilter(JWKSource<SecurityContext> jwkSource, String jwkSetEndpointUri) {
		Assert.notNull(jwkSource, "jwkSource cannot be null");
		Assert.hasText(jwkSetEndpointUri, "jwkSetEndpointUri cannot be empty");
		this.jwkSource = jwkSource;
		this.jwkSelector = new JWKSelector(new JWKMatcher.Builder().publicOnly(true).build());
		this.exchangeMatcher = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, jwkSetEndpointUri);
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		return this.exchangeMatcher.matches(exchange)
				.filter(ServerWebExchangeMatcher.MatchResult::isMatch)
				.flatMap(r -> {
					try {
						return Mono.just(new JWKSet(this.jwkSource.get(this.jwkSelector, null)));
					} catch (Exception ex) {
						return Mono.error(new IllegalStateException("Failed to select the JWK public key(s) -> " + ex.getMessage(), ex));
					}
				})
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				.flatMap(jwkSet -> {
					ServerHttpResponse response = exchange.getResponse();
					response.setStatusCode(HttpStatus.OK);
					response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

					return response.writeWith(Mono.just(response.bufferFactory()
							.wrap(jwkSet.toString().getBytes(StandardCharsets.UTF_8))));
				});

	}
}
