package org.filestore.ejb.file;

public class JBossLoginContextFactory {

	/*static class NamePasswordCallbackHandler implements CallbackHandler {
		private final String username;
		private final String password;

		private NamePasswordCallbackHandler(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback current : callbacks) {
				if (current instanceof NameCallback) {
					((NameCallback) current).setName(username);
				} else if (current instanceof PasswordCallback) {
					((PasswordCallback) current).setPassword(password.toCharArray());
				} else {
					throw new UnsupportedCallbackException(current);
				}
			}
		}
	}

	static class JBossJaasConfiguration extends Configuration {
		private final String configurationName;

		JBossJaasConfiguration(String configurationName) {
			this.configurationName = configurationName;
		}

		@Override
		public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
			if (!configurationName.equals(name)) {
				throw new IllegalArgumentException("Unexpected configuration name '" + name + "'");
			}
			return new AppConfigurationEntry[] { createClientLoginModuleConfigEntry(), };
		}

		private AppConfigurationEntry createClientLoginModuleConfigEntry() {
			Map<String, String> options = new HashMap<String, String>();
			options.put("multi-threaded", "true");
			options.put("restore-login-identity", "true");

			return new AppConfigurationEntry("org.jboss.security.ClientLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
		}
	}

	public static LoginContext createLoginContext(final String username, final String password) throws LoginException {
		final String configurationName = "filestore";

		CallbackHandler cbh = new JBossLoginContextFactory.NamePasswordCallbackHandler(username, password);
		Configuration config = new JBossJaasConfiguration(configurationName);

		return new LoginContext(configurationName, new Subject(), cbh, config);
	}
	*/

}