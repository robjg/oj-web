[HOME](../../../README.md)
# web:ui-server

Provide an Oddjob Web UI Server. This provides an incredibly simple Server to get up
and running quickly. For Basic Authentication or SSL then the [web:server](../../../org/oddjob/jetty/JettyHttpServer.md) must be used
with an [web:remote-handler](../../../org/oddjob/web/WebServerHandlerJmx.md).

### Property Summary

| Property | Description |
| -------- | ----------- |
| [allowCrossOrigin](#propertyallowcrossorigin) | Is cross-origin content allowed? | 
| [classLoader](#propertyclassloader) | The classloader passed to Jetty. | 
| [jmxServer](#propertyjmxserver) | An Oddjob JMX Server. | 
| [multiPartConfig](#propertymultipartconfig) | Set parameters for MultiPartConfig so that file upload from a form works. | 
| [name](#propertyname) | The name of service. | 
| [port](#propertyport) | The port number the server listens on. | 
| [root](#propertyroot) | The root component to expose. | 
| [uploadDirectory](#propertyuploaddirectory) | Upload directory. | 
| [webappDir](#propertywebappdir) | The directory for the html files for oddjob web. | 
| [webappResource](#propertywebappresource) | The class path to the html files for oddjob web. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | The simplest web server without any handlers. |


### Property Detail
#### allowCrossOrigin <a name="propertyallowcrossorigin"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Default to false.</td></tr>
</table>

Is cross-origin content allowed?

#### classLoader <a name="propertyclassloader"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The classloader passed to Jetty. If not set then Jetty and RESTEasy
use the Thread context classloader. This is set by Oddjob's service adapter to be the
classloader that loaded this component which will be the
Oddball classloader. Setting this classloader will be complicated as it may require the
Oddball classloader as a parent.

#### jmxServer <a name="propertyjmxserver"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, creates a default internal one.</td></tr>
</table>

An Oddjob JMX Server.

#### multiPartConfig <a name="propertymultipartconfig"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults are used.</td></tr>
</table>

Set parameters for MultiPartConfig so that file upload from a form works.

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

#### root <a name="propertyroot"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, but a Jmx Server must be provided instead.</td></tr>
</table>

The root component to expose.

#### uploadDirectory <a name="propertyuploaddirectory"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults tmp dir.</td></tr>
</table>

Upload directory. Required for an action form that specifies a file.

#### webappDir <a name="propertywebappdir"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The directory for the html files for oddjob web. Mainly used for
development to save stopping and starting Jetty.

#### webappResource <a name="propertywebappresource"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to /dist on the class path.</td></tr>
</table>

The class path to the html files for oddjob web.


### Examples
#### Example 1 <a name="example1"></a>

The simplest web server without any handlers.

```xml
<oddjob id="this">
  <job>
    <web:server xmlns:web="oddjob:web" id="server"/>
  </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
