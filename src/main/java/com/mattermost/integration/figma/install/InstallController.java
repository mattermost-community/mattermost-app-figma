package com.mattermost.integration.figma.install;

import com.mattermost.integration.figma.input.mm.manifest.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class InstallController {

    @Value("${app.root.url}")
    private String appRootUrl;

    @Value("${server.port}")
    private String port;

    @GetMapping("/manifest.json")
    public Manifest getManifest() {
        Expand.ExpandBuilder expandBindingsBuilder = Expand.builder();
        expandBindingsBuilder.actingUser("all");
        expandBindingsBuilder.oauth2App("all");
        expandBindingsBuilder.oauth2User("all");

        Bindings.BindingsBuilder bindingsBuilder = Bindings.builder();
        bindingsBuilder.path("/bindings");
        bindingsBuilder.expand(expandBindingsBuilder.build());

        GetOauth2ConnectUrl.GetOauth2ConnectUrlBuilder oauth2ConnectUrlBuilder = GetOauth2ConnectUrl.builder();
        oauth2ConnectUrlBuilder.path("/oauth2/connect");
        oauth2ConnectUrlBuilder.expand(expandBindingsBuilder.build());

        Expand.ExpandBuilder expandOnOauthCompleteBuilder = Expand.builder();
        expandOnOauthCompleteBuilder.oauth2App("all");
        expandOnOauthCompleteBuilder.actingUserAccessToken("all");

        OnOauth2Complete.OnOauth2CompleteBuilder onOauth2CompleteBuilder = OnOauth2Complete.builder();
        onOauth2CompleteBuilder.path("/oauth2/complete");
        onOauth2CompleteBuilder.expand(expandOnOauthCompleteBuilder.build());

        Function.FunctionBuilder functionBuilder = Function.builder();
        functionBuilder.path("/");
        functionBuilder.name("java-mm-figma-function");
        functionBuilder.handler("com.mattermost.integration.figma.StreamLambdaHandler");
        functionBuilder.runtime("java11");

        Http.HttpBuilder httpBuilder = Http.builder();
        httpBuilder.rootUrl(String.format("%s:%s",appRootUrl,port));

        AwsLambda.AwsLambdaBuilder awsLambdaBuilder = AwsLambda.builder();
        awsLambdaBuilder.functions(Collections.singletonList(functionBuilder.build()));

        Manifest.ManifestBuilder manifestBuilder = Manifest.builder();
        manifestBuilder.appId("figma");
        manifestBuilder.version("0.0.1");
        manifestBuilder.icon("icon.png");
        manifestBuilder.displayName("Figma");
        manifestBuilder.description("Figma integration");
        manifestBuilder.homepageUrl("https://github.com/prokhorind/mattermost-figma");
        manifestBuilder.requestedPermissions(List.of("act_as_bot", "remote_oauth2", "act_as_user", "remote_webhooks"));
        manifestBuilder.requestedLocations(Collections.singletonList("/command"));

        manifestBuilder.bindings(bindingsBuilder.build());
        manifestBuilder.getOauth2ConnectUrl(oauth2ConnectUrlBuilder.build());
        manifestBuilder.onOauth2Complete(onOauth2CompleteBuilder.build());
        manifestBuilder.awsLambda(awsLambdaBuilder.build());
        manifestBuilder.http(httpBuilder.build());


        return manifestBuilder.build();
    }
}
