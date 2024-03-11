[HOME](../../../README.md)
# web:ssl

Provides TLS Configuration for Client and Servers. Some properties apply only to servers,
some only to clients. All passwords be obfuscated, see the Jetty documentation on how to do this.

<h3>Debugging</h3>
<pre>-Djavax.net.debug=ssl</pre>
<pre>-Djavax.net.debug=all</pre>
<a href="https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-31B7E142-B874-46E9-8DD0-4E18EC0EB2CF">This Link</a> will
explain what's going on.

<h3>Common Errors</h3>
<dl>
<dt>PKIX path building failed: unable to find valid certification path to requested target</dt>
<dd>There no certificate in the Trust Store matching the server.</dd>

<dt>org.eclipse.jetty.http.HttpParser$IllegalCharacterException: 400: Illegal character CNTL=0x15</dt>
<dd>One side is using TLS, the other isn't.</dd>

<dt>javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No name matching <i>some-domain</i> found</dt>
<dd>The certificate CN does not match the host name.</dd>

<dt>java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty</dt>
<dd></dd>

<dt>java.security.UnrecoverableKeyException: Get Key failed: null</dt>
<dd>I got this when the Key Store password was missing.</dd>

<dt>java.io.IOException: keystore password was incorrect</dt>
<dd>This one is obvious.</dd>
</dl>

### Property Summary

| Property | Description |
| -------- | ----------- |
| [clientAuth](#propertyclientAuth) | Should the server perform client authentication. | 
| [hostnameVerifier](#propertyhostnameVerifier) | Something that can verify if a hostname is acceptable when the host doesn't match the certificate CN. | 
| [ignoreSni](#propertyignoreSni) | Ignore SNI checks on the server. | 
| [keyManagerPassword](#propertykeyManagerPassword) | The key password. | 
| [keyStorePassword](#propertykeyStorePassword) | The key store password. | 
| [keyStorePath](#propertykeyStorePath) | The path of the store that contains the private key and signed cert. | 
| [keyStoreType](#propertykeyStoreType) | The key store type. | 
| [port](#propertyport) | The port of the server ssl connector. | 
| [trustAll](#propertytrustAll) | Should the client trust all certificates. | 
| [trustStorePassword](#propertytrustStorePassword) | The trust store password. | 
| [trustStorePath](#propertytrustStorePath) | The path of the store that contains trusted public certs. | 
| [trustStoreType](#propertytrustStoreType) | The trust store type. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Client Accepts any certificate. |
| [Example 2](#example2) | Host Name Verification. |
| [Example 3](#example3) | One Way Trust. |
| [Example 4](#example4) | Two Way Trust. |


### Property Detail
#### clientAuth <a name="propertyclientAuth"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to NONE, and only applicable to a server.</td></tr>
</table>

Should the server perform client authentication. NONE/WANT/NEED.

#### hostnameVerifier <a name="propertyhostnameVerifier"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, and only applicable to a client.</td></tr>
</table>

Something that can verify if a hostname is acceptable when the host doesn't match
the certificate CN. In Jetty, to get this work, Client Endpoint Identification Algorithm is set to null. This
generates this warning: <em>No Client EndPointIdentificationAlgorithm configured for Client</em>

#### ignoreSni <a name="propertyignoreSni"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false, and only applicable to the server.</td></tr>
</table>

Ignore SNI checks on the server. This allows us to use localhost without getting
an `org.eclipse.jetty.http.BadMessageException: 400: Invalid SNI` which is happening since upgrading
Jetty version.

#### keyManagerPassword <a name="propertykeyManagerPassword"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The key password. Only applicable to JKS stores.

#### keyStorePassword <a name="propertykeyStorePassword"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The key store password.

#### keyStorePath <a name="propertykeyStorePath"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes for Server, No for client unless doing Client Auth.</td></tr>
</table>

The path of the store that contains the private key and signed cert.

#### keyStoreType <a name="propertykeyStoreType"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults depending on JDK version.</td></tr>
</table>

The key store type. Either JKS or PKCS12.

#### port <a name="propertyport"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, can be set from the Server configuration.</td></tr>
</table>

The port of the server ssl connector.

#### trustAll <a name="propertytrustAll"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false, and only applicable to a client.</td></tr>
</table>

Should the client trust all certificates.

#### trustStorePassword <a name="propertytrustStorePassword"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes, if you have a trust store.</td></tr>
</table>

The trust store password.

#### trustStorePath <a name="propertytrustStorePath"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, unless you wish to verify your peer.</td></tr>
</table>

The path of the store that contains trusted public certs.

#### trustStoreType <a name="propertytrustStoreType"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The trust store type. Either JKS or PKCS12.


### Examples
#### Example 1 <a name="example1"></a>

Client Accepts any certificate.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oddjob id="oddjob">
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <value key="work.dir" value="${oddjob.dir}"/>
                        <file file="${work.dir}/stores" key="ssltest.stores.dir"/>
                    </values>
                </properties>
                <sequential name="Setup Keys and Certs">
                    <jobs>
                        <delete force="true" name="Delete Any Previous Store Directory">
                            <files>
                                <file file="${ssltest.stores.dir}"/>
                            </files>
                        </delete>
                        <mkdir dir="${ssltest.stores.dir}" name="Create Store Directory"/>
                        <exec dir="${ssltest.stores.dir}" name="Create Server Keystore">
                            <![CDATA[keytool -v -genkey -keyalg RSA -keysize 2048 -validity 360 -alias serverkey -keystore server_keystore.p12 -storetype pkcs12 -storepass storepwd -dname "CN=anything"]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="List Server Keystore">
                            <![CDATA[keytool -list -v -keystore server_keystore.p12 -storetype pkcs12 -storepass storepwd]]>
                            <stdout>
                                <stdout/>
                            </stdout>
                        </exec>
                    </jobs>
                </sequential>
                <web:server xmlns:web="oddjob:web" id="server">
                    <handler>
                        <web:resource base="${oddjob.dir}">
                            <welcomeFiles>
                                <list>
                                    <values>
                                        <value value="index.html"/>
                                    </values>
                                </list>
                            </welcomeFiles>
                        </web:resource>
                    </handler>
                    <modifiers>
                        <web:ssl keyStorePassword="OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4"
                                 keyStorePath="${ssltest.stores.dir}/server_keystore.p12"
                                 ignoreSni="true"/>
                    </modifiers>
                </web:server>
                <web:client xmlns:web="oddjob:web" id="client" url="https://localhost:${server.port}">
                    <ssl>
                        <web:ssl trustAll="true"/>
                    </ssl>
                </web:client>
                <echo id="echo"><![CDATA[${client.content}>]]></echo>
                <check eq="&lt;h1&gt;Hello World&lt;/h1&gt;" value="#{client.get('content').trim()}"/>
                <stop job="${server}"/>
            </jobs>
        </sequential>
    </job>
</oddjob>

```


#### Example 2 <a name="example2"></a>

Host Name Verification. The client accepts the host even if it doesn't match the certificate.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oddjob id="oddjob">
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <value key="work.dir" value="${oddjob.dir}"/>
                        <file file="${work.dir}/stores" key="ssltest.stores.dir"/>
                    </values>
                </properties>
                <sequential name="Setup Keys and Certs">
                    <jobs>
                        <delete force="true" name="Delete Any Previous Store Directory">
                            <files>
                                <file file="${ssltest.stores.dir}"/>
                            </files>
                        </delete>
                        <mkdir dir="${ssltest.stores.dir}" name="Create Store Directory"/>
                        <exec dir="${ssltest.stores.dir}" name="Create Server Keystore">
                            <![CDATA[keytool -v -genkey -keyalg RSA -keysize 2048 -validity 360 -alias serverkey -keystore server_keystore.p12 -storetype pkcs12 -storepass srvstorepwd -dname "CN=anything"]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Export Server Certificate">
                            <stdout>
                                <file file="${ssltest.stores.dir}/server_cert.pem"/>
                            </stdout>
                            <![CDATA[keytool -export -rfc -alias serverkey -keystore server_keystore.p12 -storepass srvstorepwd]]>
                        </exec>
                        <exec dir="${ssltest.stores.dir}" name="Import Server Certificate into Client Trustore">
                            <stdin>
                                <file file="${ssltest.stores.dir}/server_cert.pem"/>
                            </stdin>
                            <![CDATA[keytool -v -import -keystore client_trustore.p12 -storepass clitrustpwd -alias serverkey -noprompt]]>
                        </exec>
                        <exec dir="${ssltest.stores.dir}" name="List Server Keystore">
                            <![CDATA[keytool -list -v -keystore server_keystore.p12 -storetype pkcs12 -storepass srvstorepwd]]></exec>
                    </jobs>
                </sequential>
                <web:server xmlns:web="oddjob:web" id="server">
                    <handler>
                        <web:resource base="${oddjob.dir}">
                            <welcomeFiles>
                                <list>
                                    <values>
                                        <value value="index.html"/>
                                    </values>
                                </list>
                            </welcomeFiles>
                        </web:resource>
                    </handler>
                    <modifiers>
                        <web:ssl keyStorePassword="srvstorepwd" keyStorePath="${ssltest.stores.dir}/server_keystore.p12"
                                 ignoreSni="true"/>
                    </modifiers>
                </web:server>
                <web:client xmlns:web="oddjob:web" id="client" url="https://localhost:${server.port}">
                    <ssl>
                        <web:ssl trustStorePassword="clitrustpwd"
                                 trustStorePath="${ssltest.stores.dir}/client_trustore.p12">
                            <hostnameVerifier>
                                <web:hostname-verifier hostname=".*" regex="true"/>
                            </hostnameVerifier>
                        </web:ssl>
                    </ssl>
                </web:client>
                <echo id="echo"><![CDATA[${client.content}>]]></echo>
                <check eq="&lt;h1&gt;Hello World&lt;/h1&gt;" value="#{client.get('content').trim()}"/>
                <stop job="${server}"/>
            </jobs>
        </sequential>
    </job>
</oddjob>

```


#### Example 3 <a name="example3"></a>

One Way Trust. The client verifies who the server is but the server doesn't care who the client is.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oddjob id="oddjob">
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <value key="work.dir" value="${oddjob.dir}"/>
                        <value key="ssltest.hostname" value="#{java.net.InetAddress.getLocalHost().getHostName()}"/>
                        <file file="${work.dir}/stores" key="ssltest.stores.dir"/>
                    </values>
                </properties>
                <sequential name="Setup Keys and Certs">
                    <jobs>
                        <delete force="true" name="Delete Any Previous Store Directory">
                            <files>
                                <file file="${ssltest.stores.dir}"/>
                            </files>
                        </delete>
                        <mkdir dir="${ssltest.stores.dir}" name="Create Store Directory"/>
                        <exec dir="${ssltest.stores.dir}" name="Create Server Keystore"><![CDATA[keytool -v -genkey -keyalg RSA -keysize 2048 -validity 360 -alias serverkey -keystore server_keystore.p12 -storetype pkcs12 -storepass srvstorepwd -dname "CN=${ssltest.hostname}"]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Export Server Certificate">
                            <stdout>
                                <file file="${ssltest.stores.dir}/server_cert.pem"/>
                            </stdout><![CDATA[keytool -export -rfc -alias serverkey -keystore server_keystore.p12 -storepass srvstorepwd]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Import Server Certificate into Client Trustore">
                            <stdin>
                                <file file="${ssltest.stores.dir}/server_cert.pem"/>
                            </stdin><![CDATA[keytool -v -import -storetype pkcs12 -keystore client_trustore.p12 -storepass clitrustpwd -alias serverkey -noprompt]]></exec>
                    </jobs>
                </sequential>
                <web:server xmlns:web="oddjob:web" id="server">
                    <handler>
                        <web:resource base="${oddjob.dir}">
                            <welcomeFiles>
                                <list>
                                    <values>
                                        <value value="index.html"/>
                                    </values>
                                </list>
                            </welcomeFiles>
                        </web:resource>
                    </handler>
                    <modifiers>
                        <web:ssl keyStorePassword="srvstorepwd" keyStorePath="${ssltest.stores.dir}/server_keystore.p12" keyStoreType="PKCS12"/>
                    </modifiers>
                </web:server>
                <web:client xmlns:web="oddjob:web" id="client" url="https://${ssltest.hostname}:${server.port}">
                    <ssl>
                        <web:ssl trustStorePassword="clitrustpwd" trustStorePath="${ssltest.stores.dir}/client_trustore.p12" trustStoreType="PKCS12"/>
                    </ssl>
                </web:client>
                <echo id="echo"><![CDATA[${client.content}>]]></echo>
                <check eq="&lt;h1&gt;Hello World&lt;/h1&gt;" value="#{client.get('content').trim()}"/>
                <stop job="${server}"/>
            </jobs>
        </sequential>
    </job>
</oddjob>

```


#### Example 4 <a name="example4"></a>

Two Way Trust. The client verifies who the server is and the server verifies who the client is.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oddjob id="oddjob">
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <value key="work.dir" value="${oddjob.dir}"/>
                        <value key="ssltest.hostname" value="#{java.net.InetAddress.getLocalHost().getHostName()}"/>
                        <file file="${work.dir}/stores" key="ssltest.stores.dir"/>
                    </values>
                </properties>
                <sequential name="Setup Keys and Certs">
                    <jobs>
                        <delete force="true" name="Delete Any Previous Store Directory">
                            <files>
                                <file file="${ssltest.stores.dir}"/>
                            </files>
                        </delete>
                        <mkdir dir="${ssltest.stores.dir}" name="Create Store Directory"/>
                        <exec dir="${ssltest.stores.dir}" name="Create Server Keystore"><![CDATA[keytool -v -genkey -keyalg RSA -keysize 2048 -validity 360 -alias serverkey -keystore server_keystore.p12 -storetype pkcs12 -storepass srvstorepwd -dname "CN=${ssltest.hostname}"]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Export Server Certificate">
                            <stdout>
                                <file file="${ssltest.stores.dir}/server_cert.pem"/>
                            </stdout><![CDATA[keytool -export -rfc -alias serverkey -keystore server_keystore.p12 -storepass srvstorepwd]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Import Server Certificate into Client Trustore">
                            <stdin>
                                <file file="${ssltest.stores.dir}/server_cert.pem"/>
                            </stdin><![CDATA[keytool -v -import -storetype pkcs12 -keystore client_trustore.p12 -storepass clitrustpwd -alias serverkey -noprompt]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Create Client Keystore"><![CDATA[keytool -v -genkey -keyalg RSA -keysize 2048 -validity 360 -alias clientkey -keystore client_keystore.p12 -storetype pkcs12 -storepass clistorepwd -dname "CN=anything"]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Export Client Certificate">
                            <stdout>
                                <file file="${ssltest.stores.dir}/client_cert.pem"/>
                            </stdout><![CDATA[keytool -export -rfc -alias clientkey -keystore client_keystore.p12 -storepass clistorepwd]]></exec>
                        <exec dir="${ssltest.stores.dir}" name="Import Server Certificate into Client Trustore">
                            <stdin>
                                <file file="${ssltest.stores.dir}/client_cert.pem"/>
                            </stdin><![CDATA[keytool -v -import -storetype pkcs12 -keystore server_trustore.p12 -storepass srvtrustpwd -alias clientkey -noprompt]]></exec>
                    </jobs>
                </sequential>
                <web:server xmlns:web="oddjob:web" id="server">
                    <handler>
                        <web:resource base="${oddjob.dir}">
                            <welcomeFiles>
                                <list>
                                    <values>
                                        <value value="index.html"/>
                                    </values>
                                </list>
                            </welcomeFiles>
                        </web:resource>
                    </handler>
                    <modifiers>
                        <web:ssl clientAuth="NEED" keyStorePassword="srvstorepwd" keyStorePath="${ssltest.stores.dir}/server_keystore.p12" keyStoreType="PKCS12" trustStorePassword="srvtrustpwd" trustStorePath="${ssltest.stores.dir}/server_trustore.p12" trustStoreType="PKCS12"/>
                    </modifiers>
                </web:server>
                <web:client xmlns:web="oddjob:web" id="client" url="https://${ssltest.hostname}:${server.port}">
                    <ssl>
                        <web:ssl keyStorePassword="clistorepwd" keyStorePath="${ssltest.stores.dir}/client_keystore.p12" keyStoreType="PKCS12" trustStorePassword="clitrustpwd" trustStorePath="${ssltest.stores.dir}/client_trustore.p12" trustStoreType="PKCS12"/>
                    </ssl>
                </web:client>
                <echo id="echo"><![CDATA[${client.content}>]]></echo>
                <check eq="&lt;h1&gt;Hello World&lt;/h1&gt;" value="#{client.get('content').trim()}"/>
                <stop job="${server}"/>
            </jobs>
        </sequential>
    </job>
</oddjob>

```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
