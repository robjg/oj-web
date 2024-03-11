[HOME](../../../README.md)
# web:server

An HTTP server.


This is a wrapper around the Jetty <a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/server/Server.html">Server</a>.
The `handlers` property must be used to configure the server to do anything useful. By default
a simple welcome message is returned for all requests.



Common handlers to use are:

- [web:resource](../../../org/oddjob/jetty/ResourceHandlerType.md)
- [web:oddjob-rest](../../../org/oddjob/jetty/OddjobRestHandler.md)


### Property Summary

| Property | Description |
| -------- | ----------- |
| [beans](#propertybeans) | Provide Beans directly to the Jetty Server for management by Jetty. | 
| [handler](#propertyhandler) | The Jetty Handler. | 
| [modifiers](#propertymodifiers) | Things that modify the server. | 
| [name](#propertyname) | The name of service. | 
| [port](#propertyport) | The port number the server listens on. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Using Basic Authentication. |


### Property Detail
#### beans <a name="propertybeans"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Provide Beans directly to the Jetty Server for management by Jetty.
Currently untested.

#### handler <a name="propertyhandler"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, but pointless if missing.</td></tr>
</table>

The Jetty Handler. To provide a list of handlers that will be tried in order
use [web:handlers](../../../org/oddjob/jetty/HandlerListType.md).

#### modifiers <a name="propertymodifiers"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Things that modify the server.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of service. Can be any text.

#### port <a name="propertyport"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, uses a random available port.</td></tr>
</table>

The port number the server listens on.


### Examples
#### Example 1 <a name="example1"></a>

Using Basic Authentication.

```xml
<oddjob id="oddjob">
    <job>
        <web:server xmlns:web="oddjob:web" id="server"
                    port="${server.port}">
            <handler>
                <bean class="org.eclipse.jetty.security.ConstraintSecurityHandler" >
                    <loginService>
                        <bean class="org.eclipse.jetty.security.HashLoginService"
                              name="FooRealm"
                              config="${oddjob.dir}/realm.txt"
                              hotReload="false"/>
                    </loginService>
                    <constraintMappings>
                        <list>
                            <values>
                                <bean class="org.eclipse.jetty.security.ConstraintMapping"
                                      pathSpec="/*">
                                    <constraint>
                                        <bean class="org.eclipse.jetty.util.security.Constraint"
                                              name="auth" authenticate="true">
                                            <roles>
                                                <value value="**"/>
                                            </roles>
                                        </bean>
                                    </constraint>
                                </bean>
                            </values>
                        </list>
                    </constraintMappings>
                    <authenticator>
                        <bean class="org.eclipse.jetty.security.authentication.BasicAuthenticator"/>
                    </authenticator>
                    <handler>
                        <bean class="org.oddjob.jetty.EchoRequestHandler" />
                    </handler>
                </bean>
            </handler>
        </web:server>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
