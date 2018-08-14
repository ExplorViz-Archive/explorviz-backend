package net.explorviz.shared.security;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import net.explorviz.shared.security.TokenDetails;

public class TokenBasedSecurityContext implements SecurityContext {

	private final AuthenticatedUserDetails authenticatedUserDetails;
	private final TokenDetails authenticationTokenDetails;
	private final boolean secure;

	public TokenBasedSecurityContext(final AuthenticatedUserDetails authenticatedUserDetails,
			final TokenDetails authenticationTokenDetails, final boolean secure) {
		this.authenticatedUserDetails = authenticatedUserDetails;
		this.authenticationTokenDetails = authenticationTokenDetails;
		this.secure = secure;
	}

	@Override
	public Principal getUserPrincipal() {
		return authenticatedUserDetails;
	}

	@Override
	public boolean isUserInRole(final String s) {
		return authenticatedUserDetails.getRoles().contains(s);
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public String getAuthenticationScheme() {
		return "Bearer";
	}

	public TokenDetails getTokenDetails() {
		return authenticationTokenDetails;
	}
}