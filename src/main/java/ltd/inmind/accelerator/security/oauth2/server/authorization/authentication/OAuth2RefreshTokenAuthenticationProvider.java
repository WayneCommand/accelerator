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
package ltd.inmind.accelerator.security.oauth2.server.authorization.authentication;

import ltd.inmind.accelerator.security.oauth2.server.authorization.OAuth2Authorization;
import ltd.inmind.accelerator.security.oauth2.server.authorization.OAuth2AuthorizationAttributeNames;
import ltd.inmind.accelerator.security.oauth2.server.authorization.OAuth2AuthorizationService;
import ltd.inmind.accelerator.security.oauth2.server.authorization.TokenType;
import ltd.inmind.accelerator.security.oauth2.server.authorization.client.RegisteredClient;
import ltd.inmind.accelerator.security.oauth2.server.authorization.token.OAuth2TokenMetadata;
import ltd.inmind.accelerator.security.oauth2.server.authorization.token.OAuth2Tokens;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Set;

import static ltd.inmind.accelerator.security.oauth2.server.authorization.authentication.OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient;


/**
 * An {@link AuthenticationProvider} implementation for the OAuth 2.0 Refresh Token Grant.
 *
 * @author Alexey Nesterov
 * @since 0.0.3
 * @see OAuth2RefreshTokenAuthenticationToken
 * @see OAuth2AccessTokenAuthenticationToken
 * @see OAuth2AuthorizationService
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-1.5">Section 1.5 Refresh Token Grant</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-6">Section 6 Refreshing an Access Token</a>
 */
public class OAuth2RefreshTokenAuthenticationProvider implements AuthenticationProvider {
	private final OAuth2AuthorizationService authorizationService;

	/**
	 * Constructs an {@code OAuth2RefreshTokenAuthenticationProvider} using the provided parameters.
	 *
	 * @param authorizationService the authorization service
	 */
	public OAuth2RefreshTokenAuthenticationProvider(OAuth2AuthorizationService authorizationService) {
		Assert.notNull(authorizationService, "authorizationService cannot be null");
		this.authorizationService = authorizationService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		OAuth2RefreshTokenAuthenticationToken refreshTokenAuthentication =
				(OAuth2RefreshTokenAuthenticationToken) authentication;

		OAuth2ClientAuthenticationToken clientPrincipal =
				getAuthenticatedClientElseThrowInvalidClient(refreshTokenAuthentication);
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

		OAuth2Authorization authorization = this.authorizationService.findByToken(
				refreshTokenAuthentication.getRefreshToken(), TokenType.REFRESH_TOKEN);
		if (authorization == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));
		}

		if (!registeredClient.getId().equals(authorization.getRegisteredClientId())) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT));
		}

		if (!registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT));
		}

		Instant refreshTokenExpiresAt = authorization.getTokens().getRefreshToken().getExpiresAt();
		if (refreshTokenExpiresAt.isBefore(Instant.now())) {
			// As per https://tools.ietf.org/html/rfc6749#section-5.2
			// invalid_grant: The provided authorization grant (e.g., authorization code,
			// resource owner credentials) or refresh token is invalid, expired, revoked [...].
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));
		}

		// As per https://tools.ietf.org/html/rfc6749#section-6
		// The requested scope MUST NOT include any scope not originally granted by the resource owner,
		// and if omitted is treated as equal to the scope originally granted by the resource owner.
		Set<String> scopes = refreshTokenAuthentication.getScopes();
		Set<String> authorizedScopes = authorization.getAttribute(OAuth2AuthorizationAttributeNames.AUTHORIZED_SCOPES);
		if (!authorizedScopes.containsAll(scopes)) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE));
		}
		if (scopes.isEmpty()) {
			scopes = authorizedScopes;
		}

		OAuth2RefreshToken refreshToken = authorization.getTokens().getRefreshToken();
		OAuth2TokenMetadata refreshTokenMetadata = authorization.getTokens().getTokenMetadata(refreshToken);

		if (refreshTokenMetadata.isInvalidated()) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));
		}

		/*Jwt jwt = OAuth2TokenIssuerUtil
			.issueJwtAccessToken(this.jwtEncoder, authorization.getPrincipalName(), registeredClient.getClientId(), scopes, registeredClient.getTokenSettings().accessTokenTimeToLive());
		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
			jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), scopes);*/

		Jwt jwt = null;

		OAuth2AccessToken accessToken = null;

		authorization = OAuth2Authorization.from(authorization)
				.tokens(OAuth2Tokens.from(authorization.getTokens()).accessToken(accessToken).refreshToken(refreshToken).build())
				.attribute(OAuth2AuthorizationAttributeNames.ACCESS_TOKEN_ATTRIBUTES, jwt)
				.build();
		this.authorizationService.save(authorization);

		return new OAuth2AccessTokenAuthenticationToken(
				registeredClient, clientPrincipal, accessToken, refreshToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
