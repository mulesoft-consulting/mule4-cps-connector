# Configuration-properties-service Extension

This is a connector to the CPS configuration property service. The CPS is an application that stores configs. The application runs in a Mule Runtime. Each config is stored under its own key, in a persisted Mule Object Store. 

A REST API is provided to allow the configuration property service connector to retrieve (GET) a config and its imports from the service. In addition, several other methods and resources are provided to help manage the configs.

To use the CPS connector in a Mule project, add this dependency to your application pom.xml

```
		<dependency>
			<groupId>org.mule.cps</groupId>
			<artifactId>cps-connector</artifactId>
			<classifier>mule-plugin</classifier>
			<version>1.0.0</version>
		</dependency>
```
Then add the Global Configuration-property-service Config element to your project's config.xml file. The XML looks like this:

```
	<cps:config name="Configuration_properties_service_Config"
		doc:name="Configuration-properties-service Config" doc:id="b994fefb-59ee-48f9-aa88-c9cc40b4c0b8"
		configServerBaseUrl="http://localhost:9184/configuration-property-service/v1/config"
		insecure="true" projectName="test-mule4-cps-connector" branchName="base"
		instanceId="base" envName="DEV" keyId="base" clientId="x"
		clientSecret="x" configId="cps-config" />

```
