package com.mattermost.integration.figma.api.mm.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MMServerClientConfig {
    @JsonProperty("AboutLink")
    private String aboutLink;
    @JsonProperty("AndroidAppDownloadLink")
    private String androidAppDownloadLink;
    @JsonProperty("AndroidLatestVersion")
    private String androidLatestVersion;
    @JsonProperty("AndroidMinVersion")
    private String androidMinVersion;
    @JsonProperty("AppDownloadLink")
    private String appDownloadLink;
    @JsonProperty("AsymmetricSigningPublicKey")
    private String asymmetricSigningPublicKey;
    @JsonProperty("BuildDate")
    private String buildDate;
    @JsonProperty("BuildEnterpriseReady")
    private String buildEnterpriseReady;
    @JsonProperty("BuildHash")
    private String buildHash;
    @JsonProperty("BuildHashEnterprise")
    private String buildHashEnterprise;
    @JsonProperty("BuildNumber")
    private String buildNumber;
    @JsonProperty("CWSURL")
    private String cWSURL;
    @JsonProperty("CustomBrandText")
    private String customBrandText;
    @JsonProperty("CustomDescriptionText")
    private String customDescriptionText;
    @JsonProperty("CustomTermsOfServiceReAcceptancePeriod")
    private String customTermsOfServiceReAcceptancePeriod;
    @JsonProperty("DefaultClientLocale")
    private String defaultClientLocale;
    @JsonProperty("DiagnosticId")
    private String diagnosticId;
    @JsonProperty("DiagnosticsEnabled")
    private String diagnosticsEnabled;
    @JsonProperty("EmailLoginButtonBorderColor")
    private String emailLoginButtonBorderColor;
    @JsonProperty("EmailLoginButtonColor")
    private String emailLoginButtonColor;
    @JsonProperty("EmailLoginButtonTextColor")
    private String emailLoginButtonTextColor;
    @JsonProperty("EnableAskCommunityLink")
    private String enableAskCommunityLink;
    @JsonProperty("EnableBotAccountCreation")
    private String enableBotAccountCreation;
    @JsonProperty("EnableComplianceExport")
    private String enableComplianceExport;
    @JsonProperty("EnableCustomBrand")
    private String enableCustomBrand;
    @JsonProperty("EnableCustomEmoji")
    private String enableCustomEmoji;
    @JsonProperty("EnableCustomTermsOfService")
    private String enableCustomTermsOfService;
    @JsonProperty("EnableDiagnostics")
    private String enableDiagnostics;
    @JsonProperty("EnableFile")
    private String enableFile;
    @JsonProperty("EnableGuestAccounts")
    private String enableGuestAccounts;
    @JsonProperty("EnableLdap")
    private String enableLdap;
    @JsonProperty("EnableMultifactorAuthentication")
    private String enableMultifactorAuthentication;
    @JsonProperty("EnableOpenServer")
    private String enableOpenServer;
    @JsonProperty("EnableSaml")
    private String enableSaml;
    @JsonProperty("EnableSignInWithEmail")
    private String enableSignInWithEmail;
    @JsonProperty("EnableSignInWithUsername")
    private String enableSignInWithUsername;
    @JsonProperty("EnableSignUpWithEmail")
    private String enableSignUpWithEmail;
    @JsonProperty("EnableSignUpWithGitLab")
    private String enableSignUpWithGitLab;
    @JsonProperty("EnableSignUpWithGoogle")
    private String enableSignUpWithGoogle;
    @JsonProperty("EnableSignUpWithOffice365")
    private String enableSignUpWithOffice365;
    @JsonProperty("EnableSignUpWithOpenId")
    private String enableSignUpWithOpenId;
    @JsonProperty("EnableUserCreation")
    private String enableUserCreation;
    @JsonProperty("EnforceMultifactorAuthentication")
    private String enforceMultifactorAuthentication;
    @JsonProperty("FeatureFlagAnnualSubscription")
    private String featureFlagAnnualSubscription;
    @JsonProperty("FeatureFlagAppsEnabled")
    private String featureFlagAppsEnabled;
    @JsonProperty("FeatureFlagBoardsDataRetention")
    private String featureFlagBoardsDataRetention;
    @JsonProperty("FeatureFlagBoardsFeatureFlags")
    private String featureFlagBoardsFeatureFlags;
    @JsonProperty("FeatureFlagBoardsProduct")
    private String featureFlagBoardsProduct;
    @JsonProperty("FeatureFlagCallsEnabled")
    private String featureFlagCallsEnabled;
    @JsonProperty("FeatureFlagCallsMobile")
    private String featureFlagCallsMobile;
    @JsonProperty("FeatureFlagCollapsedThreads")
    private String featureFlagCollapsedThreads;
    @JsonProperty("FeatureFlagCommandPalette")
    private String featureFlagCommandPalette;
    @JsonProperty("FeatureFlagCustomGroups")
    private String featureFlagCustomGroups;
    @JsonProperty("FeatureFlagEnableInactivityCheckJob")
    private String featureFlagEnableInactivityCheckJob;
    @JsonProperty("FeatureFlagEnableRemoteClusterService")
    private String featureFlagEnableRemoteClusterService;
    @JsonProperty("FeatureFlagGraphQL")
    private String featureFlagGraphQL;
    @JsonProperty("FeatureFlagGuidedChannelCreation")
    private String featureFlagGuidedChannelCreation;
    @JsonProperty("FeatureFlagInsightsEnabled")
    private String featureFlagInsightsEnabled;
    @JsonProperty("FeatureFlagInviteToTeam")
    private String featureFlagInviteToTeam;
    @JsonProperty("FeatureFlagNormalizeLdapDNs")
    private String featureFlagNormalizeLdapDNs;
    @JsonProperty("FeatureFlagPeopleProduct")
    private String featureFlagPeopleProduct;
    @JsonProperty("FeatureFlagPermalinkPreviews")
    private String featureFlagPermalinkPreviews;
    @JsonProperty("FeatureFlagPluginApps")
    private String featureFlagPluginApps;
    @JsonProperty("FeatureFlagPluginCalls")
    private String featureFlagPluginCalls;
    @JsonProperty("FeatureFlagPluginFocalboard")
    private String featureFlagPluginFocalboard;
    @JsonProperty("FeatureFlagPluginPlaybooks")
    private String featureFlagPluginPlaybooks;
    @JsonProperty("FeatureFlagPostPriority")
    private String featureFlagPostPriority;
    @JsonProperty("FeatureFlagReduceOnBoardingTaskList")
    private String featureFlagReduceOnBoardingTaskList;
    @JsonProperty("FeatureFlagSendWelcomePost")
    private String featureFlagSendWelcomePost;
    @JsonProperty("FeatureFlagTestBoolFeature")
    private String featureFlagTestBoolFeature;
    @JsonProperty("FeatureFlagTestFeature")
    private String featureFlagTestFeature;
    @JsonProperty("FeatureFlagThreadsEverywhere")
    private String featureFlagThreadsEverywhere;
    @JsonProperty("FeatureFlagUseCaseOnboarding")
    private String featureFlagUseCaseOnboarding;
    @JsonProperty("FeatureFlagWorkTemplate")
    private String featureFlagWorkTemplate;
    @JsonProperty("FileLevel")
    private String fileLevel;
    @JsonProperty("GitLabButtonColor")
    private String gitLabButtonColor;
    @JsonProperty("GitLabButtonText")
    private String gitLabButtonText;
    @JsonProperty("GuestAccountsEnforceMultifactorAuthentication")
    private String guestAccountsEnforceMultifactorAuthentication;
    @JsonProperty("HasImageProxy")
    private String hasImageProxy;
    @JsonProperty("HelpLink")
    private String helpLink;
    @JsonProperty("IosAppDownloadLink")
    private String iosAppDownloadLink;
    @JsonProperty("IosLatestVersion")
    private String iosLatestVersion;
    @JsonProperty("IosMinVersion")
    private String iosMinVersion;
    @JsonProperty("LdapLoginButtonBorderColor")
    private String ldapLoginButtonBorderColor;
    @JsonProperty("LdapLoginButtonColor")
    private String ldapLoginButtonColor;
    @JsonProperty("LdapLoginButtonTextColor")
    private String ldapLoginButtonTextColor;
    @JsonProperty("LdapLoginFieldName")
    private String ldapLoginFieldName;
    @JsonProperty("NoAccounts")
    private String noAccounts;
    @JsonProperty("OpenIdButtonColor")
    private String openIdButtonColor;
    @JsonProperty("OpenIdButtonText")
    private String openIdButtonText;
    @JsonProperty("PasswordMinimumLength")
    private String passwordMinimumLength;
    @JsonProperty("PasswordRequireLowercase")
    private String passwordRequireLowercase;
    @JsonProperty("PasswordRequireNumber")
    private String passwordRequireNumber;
    @JsonProperty("PasswordRequireSymbol")
    private String passwordRequireSymbol;
    @JsonProperty("PasswordRequireUppercase")
    private String passwordRequireUppercase;
    @JsonProperty("PluginsEnabled")
    private String pluginsEnabled;
    @JsonProperty("PrivacyPolicyLink")
    private String privacyPolicyLink;
    @JsonProperty("ReportAProblemLink")
    private String reportAProblemLink;
    @JsonProperty("SamlLoginButtonBorderColor")
    private String samlLoginButtonBorderColor;
    @JsonProperty("SamlLoginButtonColor")
    private String samlLoginButtonColor;
    @JsonProperty("SamlLoginButtonText")
    private String samlLoginButtonText;
    @JsonProperty("SamlLoginButtonTextColor")
    private String samlLoginButtonTextColor;
    @JsonProperty("SiteName")
    private String siteName;
    @JsonProperty("SiteURL")
    private String siteURL;
    @JsonProperty("SupportEmail")
    private String supportEmail;
    @JsonProperty("TelemetryId")
    private String telemetryId;
    @JsonProperty("TermsOfServiceLink")
    private String termsOfServiceLink;
    @JsonProperty("Version")
    private String version;
    @JsonProperty("WebsocketPort")
    private String websocketPort;
    @JsonProperty("WebsocketSecurePort")
    private String websocketSecurePort;
    @JsonProperty("WebsocketURL")
    private String websocketURL;
}
