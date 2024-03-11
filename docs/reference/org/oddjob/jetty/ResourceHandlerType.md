[HOME](../../../README.md)
# web:resource

Provides a Handler to Serve file content from the file system or
class path.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [base](#propertybase) | The base directory or resource from where to serve content. | 
| [directoriesListed](#propertydirectoriesListed) | List directories or not. | 
| [minMemoryMappedContentLength](#propertyminMemoryMappedContentLength) | Control memory mapped size. | 
| [resourceType](#propertyresourceType) | The type of resource, FILE or CLASSPATH. | 
| [welcomeFiles](#propertywelcomeFiles) | List of welcome files to serve. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Serve files from the same directory the configuration file is in. |
| [Example 2](#example2) | Serves files but also provides a list of welcome files. |


### Property Detail
#### base <a name="propertybase"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, but pointless without one.</td></tr>
</table>

The base directory or resource from where to serve content.

#### directoriesListed <a name="propertydirectoriesListed"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

List directories or not.

#### minMemoryMappedContentLength <a name="propertyminMemoryMappedContentLength"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Control memory mapped size. Set to -1 on windows because of
<a href="https://wiki.eclipse.org/Jetty/Howto/Deal_with_Locked_Windows_Files">This issue</a>


This property was deprecated, and then removed in version 10. The link above no
longer exists so we assume this has been fixed. This property now does nothing and
will be removed in Oddjob 1.8.



#### resourceType <a name="propertyresourceType"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to FILE.</td></tr>
</table>

The type of resource, FILE or CLASSPATH.

#### welcomeFiles <a name="propertywelcomeFiles"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

List of welcome files to serve.


### Examples
#### Example 1 <a name="example1"></a>

Serve files from the same directory the configuration file is in.

```xml
<oddjob id="this">
  <job>
    <web:server xmlns:web="oddjob:web" id="server">
      <handler>
        <web:resource directoriesListed="true" base="${this.dir}"/>
      </handler>
    </web:server>
  </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

Serves files but also provides a list of welcome files.

```xml
<oddjob id="this">
  <job>
    <web:server xmlns:web="oddjob:web" id="server">
      <handler>
        <web:resource base="${this.dir}">
          <welcomeFiles>
            <value value="TestFile.txt"/>
          </welcomeFiles>
        </web:resource>
      </handler>
    </web:server>
  </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
