[HOME](../../../README.md)
# web:client

Execute an HTTP client request.


This is a very simple wrapper around Jetty's
<a href="http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/client/HttpClient.html">HTTPClient</a>.
Only PUT and GET requests are supported. Basic Authentication is supported, and so are SSL connections.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [basicAuthentication](#propertybasicauthentication) | Provide Username/Password for Basic Authentication. | 
| [content](#propertycontent) | The request body to send or the response body received. | 
| [contentLength](#propertycontentlength) | Content length of a response. | 
| [contentType](#propertycontenttype) | The content-type of a POST request. | 
| [downloadCount](#propertydownloadcount) | The bytes downloaded so far. | 
| [method](#propertymethod) | The request method. | 
| [name](#propertyname) | The name of the job. | 
| [output](#propertyoutput) | The output (such as a file) to download to. | 
| [parameters](#propertyparameters) | Parameters. | 
| [progress](#propertyprogress) | Progress of a download in a human-readable format. | 
| [requestBody](#propertyrequestbody) | The content to send in a POST Request. | 
| [responseBody](#propertyresponsebody) | The content received if an output is not provided. | 
| [ssl](#propertyssl) | Provide SSL Configuration. | 
| [status](#propertystatus) | The return status. | 
| [timeout](#propertytimeout) | Timeout of requests in seconds. | 
| [url](#propertyurl) | The URL to connect to. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Get the content of a URL using a parameter. |
| [Example 2](#example2) | Basic Authentication. |
| [Example 3](#example3) | Download to a file. |


### Property Detail
#### basicAuthentication <a name="propertybasicauthentication"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Provide Username/Password for Basic Authentication.

#### content <a name="propertycontent"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The request body to send or the response body received. This maps to
<cdoe>requestBody</cdoe> and <code>responseBody</code> as a convenience but is confusing
so should probably be deprecated.

#### contentLength <a name="propertycontentlength"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

Content length of a response.

#### contentType <a name="propertycontenttype"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The content-type of a POST request. Useful for sending forms.

#### downloadCount <a name="propertydownloadcount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

The bytes downloaded so far. Only set for a stream download.

#### method <a name="propertymethod"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No defaults to GET.</td></tr>
</table>

The request method. GET/POST. PUT and DELETE are not supported yet.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the job. Can be any text.

#### output <a name="propertyoutput"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The output (such as a file) to download to.

#### parameters <a name="propertyparameters"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Parameters.

#### progress <a name="propertyprogress"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

Progress of a download in a human-readable format. Only set
for a stream download.

#### requestBody <a name="propertyrequestbody"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>TEXT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The content to send in a POST Request.

#### responseBody <a name="propertyresponsebody"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

The content received if an output is not provided.

#### ssl <a name="propertyssl"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Provide SSL Configuration.

#### status <a name="propertystatus"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The return status.

#### timeout <a name="propertytimeout"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Timeout of requests in seconds.

#### url <a name="propertyurl"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The URL to connect to. Must be a full URL, e.g. http://www.google.com


### Examples
#### Example 1 <a name="example1"></a>

Get the content of a URL using a parameter.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <value key="some.url" value="http://www.google.com/search"/>
                    </values>
                </properties>
                <web:client xmlns:web="oddjob:web" id="request" url="${some.url}">
                    <parameters>
                        <map>
                            <values>
                                <value key="q" value="gold fish"/>
                            </values>
                        </map>
                    </parameters>
                </web:client>
                <echo><![CDATA[${request.content}]]></echo>
            </jobs>
        </sequential>
    </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

Basic Authentication.


```xml
<oddjob id="oddjob">
    <job>
        <web:client xmlns:web="oddjob:web" id="client"
                    url="http://localhost:${server.port}">
            <basicAuthentication>
                <is username="alice" password="secret"/>
            </basicAuthentication>
        </web:client>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

Download to a file.

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <value key="some.url" value="http://ipv4.download.thinkbroadband.com/1GB.zip"/>
                        <value key="some.file" value="download.zip"/>
                    </values>
                </properties>
                <web:client xmlns:web="oddjob:web" id="request" name="Download Example" url="${some.url}">
                    <output>
                        <file file="${some.file}"/>
                    </output>
                </web:client>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
