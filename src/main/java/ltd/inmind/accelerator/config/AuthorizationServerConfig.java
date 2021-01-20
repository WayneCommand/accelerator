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
package ltd.inmind.accelerator.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import ltd.inmind.accelerator.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import ltd.inmind.accelerator.security.jose.Jwks;
import ltd.inmind.accelerator.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import ltd.inmind.accelerator.security.oauth2.server.authorization.client.RegisteredClient;
import ltd.inmind.accelerator.security.oauth2.server.authorization.client.RegisteredClientRepository;
import ltd.inmind.accelerator.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.UUID;

/**
 * @author Joe Grandja
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

	// @formatter:off
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("messaging-client")
				.clientSecret("secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://localhost:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("http://localhost:8080/authorized")
				.scope(OidcScopes.OPENID)
				.scope("message.read")
				.scope("message.write")
				.clientSettings(clientSettings -> clientSettings.requireUserConsent(true))
				.build();

		return new InMemoryRegisteredClientRepository(registeredClient);
	}
	// @formatter:on

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = Jwks.generateRsa();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	@Bean
	public ProviderSettings providerSettings() {
		return new ProviderSettings().issuer("http://auth-service:9000");
	}
}
