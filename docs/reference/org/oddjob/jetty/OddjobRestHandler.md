[HOME](../../../README.md)
# web:oddjob-rest

Provide the Oddjob REST Service.


The actual service API is defined in [org.oddjob.rest.OddjobApi](http://rgordon.co.uk/oddjob/1.6.0/api/org/oddjob/rest/OddjobApi.html).

### Property Summary

| Property | Description |
| -------- | ----------- |
| [allowCrossOrigin](#propertyallowcrossorigin) | Is cross origin content allowed? | 
| [contextPath](#propertycontextpath) | The context path. | 
| [multiPartConfig](#propertymultipartconfig) | Set parameters for MultiPartConfig so that file upload from a form works. | 
| [root](#propertyroot) | The root Oddjob component to expose via the web service. | 
| [servicePath](#propertyservicepath) | The context path for the Oddjob web service. | 
| [uploadDirectory](#propertyuploaddirectory) | Upload directory. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Provide an Oddjob web service only without the client. |


### Property Detail
#### allowCrossOrigin <a name="propertyallowcrossorigin"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Default to false.</td></tr>
</table>

Is cross origin content allowed?

#### contextPath <a name="propertycontextpath"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The context path.

#### multiPartConfig <a name="propertymultipartconfig"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults are used.</td></tr>
</table>

Set parameters for MultiPartConfig so that file upload from a form works.

#### root <a name="propertyroot"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The root Oddjob component to expose via the web service.

#### servicePath <a name="propertyservicepath"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to '/'</td></tr>
</table>

The context path for the Oddjob web service.

#### uploadDirectory <a name="propertyuploaddirectory"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults tmp dir.</td></tr>
</table>

Upload directory. Required for an action form that specifies a file.


### Examples
#### Example 1 <a name="example1"></a>

Provide an Oddjob web service only without the client.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
  <job>
    <sequential>
      <jobs>
        <web:server xmlns:web="oddjob:web" id="server">
          <handler>
            <web:oddjob-rest root="${jobs}" />
          </handler>
        </web:server>
        <sequential id="jobs">
          <jobs>
            <echo name="Echo 1"><![CDATA[Hello]]></echo>
            <echo name="Echo 2"><![CDATA[Hello]]></echo>
          </jobs>
        </sequential>
      </jobs>
    </sequential>
  </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
