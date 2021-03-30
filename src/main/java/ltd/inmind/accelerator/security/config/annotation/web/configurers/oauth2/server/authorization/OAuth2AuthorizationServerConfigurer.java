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
package ltd.inmind.accelerator.security.config.annotation.web.configurers.oauth2.server.authorization;

import ltd.inmind.accelerator.security.oauth2.server.authorization.OAuth2AuthorizationService;
import ltd.inmind.accelerator.security.oauth2.server.authorization.authentication.*;
import ltd.inmind.accelerator.security.oauth2.server.authorization.client.RegisteredClientRepository;
import ltd.inmind.accelerator.security.oauth2.server.authorization.oidc.web.OidcProviderConfigurationEndpointFilter;
import ltd.inmind.accelerator.security.oauth2.server.authorization.web.*;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An {@link AbstractHttpConfigurer} for OAuth 2.0 Authorization Server support.
 * @since 0.0.1
 */
public final class OAuth2AuthorizationServerConfigurer {

	private final RequestMatcher authorizationEndpointMatcher = new OrRequestMatcher(
			new AntPathRequestMatcher(
					OAuth2AuthorizationEndpointFilter.DEFAULT_AUTHORIZATION_ENDPOINT_URI,
					HttpMethod.GET.name()),
			new AntPathRequestMatcher(
					OAuth2AuthorizationEndpointFilter.DEFAULT_AUTHORIZATION_ENDPOINT_URI,
					HttpMethod.POST.name()));
	private final RequestMatcher tokenEndpointMatcher = new AntPathRequestMatcher(
			OAuth2TokenEndpointFilter.DEFAULT_TOKEN_ENDPOINT_URI, HttpMethod.POST.name());
	private final RequestMatcher tokenRevocationEndpointMatcher = new AntPathRequestMatcher(
			OAuth2TokenRevocationEndpointFilter.DEFAULT_TOKEN_REVOCATION_ENDPOINT_URI, HttpMethod.POST.name());
	private final RequestMatcher jwkSetEndpointMatcher = new AntPathRequestMatcher(
			NimbusJwkSetEndpointFilter.DEFAULT_JWK_SET_ENDPOINT_URI, HttpMethod.GET.name());
	private final RequestMatcher oidcProviderConfigurationEndpointMatcher = new AntPathRequestMatcher(
			OidcProviderConfigurationEndpointFilter.DEFAULT_OIDC_PROVIDER_CONFIGURATION_ENDPOINT_URI, HttpMethod.GET.name());

	/**
	 * Sets the repository of registered clients.
	 *
	 * @param registeredClientRepository the repository of registered clients
	 * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
	 */
	public OAuth2AuthorizationServerConfigurer registeredClientRepository(RegisteredClientRepository registeredClientRepository) {
		Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
		return this;
	}

	/**
	 * Sets the authorization service.
	 *
	 * @param authorizationService the authorization service
	 * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
	 */
	public OAuth2AuthorizationServerConfigurer authorizationService(OAuth2AuthorizationService authorizationService) {
		Assert.notNull(authorizationService, "authorizationService cannot be null");
		return this;
	}

	/**
	 * Sets the provider settings.
	 *
	 * @return the {@link OAuth2AuthorizationServerConfigurer} for further configuration
	 */
	public OAuth2AuthorizationServerConfigurer providerSettings() {
		return this;
	}

	/**
	 * Returns a {@code List} of {@link RequestMatcher}'s for the authorization server endpoints.
	 *
	 * @return a {@code List} of {@link RequestMatcher}'s for the authorization server endpoints
	 */
	public List<RequestMatcher> getEndpointMatchers() {
		// TODO Initialize matchers using URI's from ProviderSettings
		return Arrays.asList(this.authorizationEndpointMatcher, this.tokenEndpointMatcher,
				this.tokenRevocationEndpointMatcher, this.jwkSetEndpointMatcher,
				this.oidcProviderConfigurationEndpointMatcher);
	}

	public void init() {

		OAuth2ClientAuthenticationProvider clientAuthenticationProvider =
				new OAuth2ClientAuthenticationProvider(null, null);


		OAuth2AuthorizationCodeAuthenticationProvider authorizationCodeAuthenticationProvider =
				new OAuth2AuthorizationCodeAuthenticationProvider(null,null);

		OAuth2RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider =
				new OAuth2RefreshTokenAuthenticationProvider(null, null);

		OAuth2ClientCredentialsAuthenticationProvider clientCredentialsAuthenticationProvider =
				new OAuth2ClientCredentialsAuthenticationProvider(null, null);

		OAuth2TokenRevocationAuthenticationProvider tokenRevocationAuthenticationProvider =
				new OAuth2TokenRevocationAuthenticationProvider(null);
	}

	public void configure() {
		AuthenticationManager authenticationManager = null;

		OAuth2ClientAuthenticationFilter clientAuthenticationFilter =
				new OAuth2ClientAuthenticationFilter(
						authenticationManager, null);

		OAuth2AuthorizationEndpointFilter authorizationEndpointFilter =
				new OAuth2AuthorizationEndpointFilter(
						null, null);

		OAuth2TokenEndpointFilter tokenEndpointFilter =
				new OAuth2TokenEndpointFilter(
						authenticationManager, null);

		OAuth2TokenRevocationEndpointFilter tokenRevocationEndpointFilter =
				new OAuth2TokenRevocationEndpointFilter(
						authenticationManager);
	}


	private static <B extends HttpSecurityBuilder<B>> RegisteredClientRepository getRegisteredClientRepository(B builder) {
		RegisteredClientRepository registeredClientRepository = builder.getSharedObject(RegisteredClientRepository.class);
		if (registeredClientRepository == null) {
			registeredClientRepository = getBean(builder, RegisteredClientRepository.class);
			builder.setSharedObject(RegisteredClientRepository.class, registeredClientRepository);
		}
		return registeredClientRepository;
	}


	private static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, Class<T> type) {
		return builder.getSharedObject(ApplicationContext.class).getBean(type);
	}

	@SuppressWarnings("unchecked")
	private static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, ResolvableType type) {
		ApplicationContext context = builder.getSharedObject(ApplicationContext.class);
		String[] names = context.getBeanNamesForType(type);
		if (names.length == 1) {
			return (T) context.getBean(names[0]);
		}
		if (names.length > 1) {
			throw new NoUniqueBeanDefinitionException(type, names);
		}
		throw new NoSuchBeanDefinitionException(type);
	}

	private static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, Class<T> type) {
		Map<String, T> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				builder.getSharedObject(ApplicationContext.class), type);
		if (beansMap.size() > 1) {
			throw new NoUniqueBeanDefinitionException(type, beansMap.size(),
					"Expected single matching bean of type '" + type.getName() + "' but found " +
							beansMap.size() + ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
		}
		return (!beansMap.isEmpty() ? beansMap.values().iterator().next() : null);
	}
}
