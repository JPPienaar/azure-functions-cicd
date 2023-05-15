package com.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureCliCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.VisualStudioCodeCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.azure.core.management.AzureEnvironment;

/**
 * Azure Functions with HTTP Trigger.
 */
public class TestCICD {
    /**
     * This function listens at endpoint "/api/TestCICD". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/TestCICD
     * 2. curl {your host}/api/TestCICD?name=HTTP%20Query
     */
    @FunctionName("TestCICD")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        
        try {
            AzureProfile profile = new AzureProfile("804521c5-2292-4e60-ba96-282c5f6d724d","91f277f3-0f24-4e0f-a841-15d287766a59",AzureEnvironment.AZURE);
            TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
            AzureResourceManager azure = AzureResourceManager
                .authenticate(credential, profile)
                .withDefaultSubscription();
            context.getLogger().info("Testing AZURE SDK.");
            PagedIterable<ResourceGroup> resources = azure.resourceGroups().list();
            int count = 0;
            for (ResourceGroup resourceGroup : resources) {
                count++;
                context.getLogger().info(resourceGroup.name());
            }
            context.getLogger().info("Count:"+ String.valueOf(count));
        } catch (Exception e) {
            context.getLogger().warning(e.toString());
        }
        
        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}
