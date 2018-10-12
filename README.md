# Configuration-properties-service Extension

This is a connector to the CPS configuration property service. The CPS is an application that stores configs. The application runs in a Mule Runtime. Each config is stored under its own key, in a persisted Mule Object Store. 

A REST API is provided to allow the configuration property service connector to retrieve (GET) a config and its imports from the service. In addition, several other methods and resources are provided to help manage the configs.

To use the CPS connector in a Mule project, add this dependency to your application pom.xml

```
		<dependency>
			<groupId>org.mule.cps</groupId>
			<artifactId>cps-connector</artifactId>
			<classifier>mule-plugin</classifier>
			<version>1.0.1</version>
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
All properties defined in the configuration server's repository for the application, profiles and labels will be available in the application in the form of placeholders. 

This connector loads the properties as part of its initialization
state functions.

Once loaded, the properties are available to use in the connector's configuration as well as in the Mule application using the standard
property-placeholder notation.  The search order for properties is:

1. {env}-config.properties properties,
2. Initial Configuration Property Service properties,
3. Import Configuration Property Service properties as defined in the initial configuration (in order of definition in the intial configuration)

As of version 1.0.1, an environment specific property file can also be defined in the classpath directory (for instance, use the src/main/resources directory). The property file values will take precedence over the same property names in the configuration property service.

The file is named {env}-config.properties where {env} is the envName parameter specified in the cps:config (shifted to all uppercase). As an example, the file DEV-config.properties is used for the configuration example above. If the file is optional.

##Encrypted Properties
## How To Build

This is a standard 'Mule 4 Extension' project. Therefore, it is mavenized and it can be built using the standard toolset.

In short, this connector can be built using:

    $ mvn clean install 

## Configuring the Keystore for Secured Properties

The option to encrypt selected properties in a configuration requires an RSA key stored in a keystore file. Many different configurations can use encryption to secure properties and each of the configurations can use a different key. All keys that are
used to secure configuration properties must be in a single keystore file that is accessible by the configuration-property-service-connector. Also note that all the keys must use the same key password in the keystore.

To configure the keystore, add these properties to either your server environment variables or your Mule server's wrapper.conf file:

```
	mule_cps_keystore_filename=<path to the keystore.jks file, required>
	mule_cps_keystore_password=<password for the keystore file, the default is "">
	mule_cps_key_password=<password for all the keys to be used, the default is "">
```
Note for CloudHub, place the keystore in the src/main/resources directory and specify only the filename in the mule_cps_keystore_filename.

##Associated REST Service Project
The CPS REST service project can be found [here](https://github.com/mulesoft-consulting/mule4-cps-rest-service):

## Invalid Key Size Error During Encryption or Decryption

This error will occur if the Java Cryptography Extension (JCE) Unlimited Strength is not installed in your Java JRE (lib/security) directory. Do an Internet search to find where to download this set of libraries.