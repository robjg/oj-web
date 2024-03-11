[HOME](../../../README.md)
# web:remote-handler

Provide a Jetty Server Handler that connects to a local JMX implementation.


This allows greater control over a server. For a quick Oddjob Web UI see
[web:ui-server](../../../org/oddjob/jetty/OddJobWebUiServer.md).



### Property Summary

| Property | Description |
| -------- | ----------- |
| [allowCrossOrigin](#propertyallowCrossOrigin) | Is cross-origin content allowed? | 
| [classLoader](#propertyclassLoader) | The classloader passed to Jetty. | 
| [jmxServer](#propertyjmxServer) | An Oddjob JMX Server. | 
| [multiPartConfig](#propertymultiPartConfig) | Set parameters for MultiPartConfig so that file upload from a form works. | 
| [uploadDirectory](#propertyuploadDirectory) | Upload directory. | 


### Property Detail
#### allowCrossOrigin <a name="propertyallowCrossOrigin"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to false.</td></tr>
</table>

Is cross-origin content allowed?

#### classLoader <a name="propertyclassLoader"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The classloader passed to Jetty. If not set then Jetty and RESTEasy
use the Thread context classloader. This is set by Oddjob's service adapter to be the
classloader that loaded the component that is using this handler which will be the
Oddball classloader. Setting this classloader will be complicated as it may require the
Oddball classloader as a parent.

#### jmxServer <a name="propertyjmxServer"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes, for the time being. Will default soon.</td></tr>
</table>

An Oddjob JMX Server.

#### multiPartConfig <a name="propertymultiPartConfig"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults are used.</td></tr>
</table>

Set parameters for MultiPartConfig so that file upload from a form works.

#### uploadDirectory <a name="propertyuploadDirectory"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults tmp dir.</td></tr>
</table>

Upload directory. Required for an action form that specifies a file.


-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
