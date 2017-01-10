package org.oddjob.rest.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/**
 * @author http://blogs.steeplesoft.com/posts/2014/file-uploads-with-jax-rs-2.html
 *
 */
public class MultipartRequestMap {

	private static final String DEFAULT_ENCODING = "UTF-8";
    
    private File uploadDir;

    public MultipartRequestMap() {
        this(new File(System.getProperty("java.io.tmpdir")));
    }

    public MultipartRequestMap(File tempLocation) {
        this.uploadDir = tempLocation;
    }
    
    public FormData parse(HttpServletRequest request) throws IOException, ServletException {

    	String encoding = request.getCharacterEncoding();
        if (encoding == null) {
                request.setCharacterEncoding(encoding = DEFAULT_ENCODING);
        }
        
        Map<String, String> map = new HashMap<>();
        
        for (Part part : request.getParts()) {
            String fileName = part.getSubmittedFileName();
            if (fileName == null) {
                map.put(part.getName(), getValue(part, encoding));
            } 
            else {
                map.put(part.getName(), processFilePart(part, fileName));
            }
        }
        
        return new FormDataImpl(map);
    }

    private String processFilePart(Part part, String fileName) throws IOException {
        File uploadFile = new File(uploadDir, fileName);
        uploadFile.createNewFile();

        try (BufferedInputStream input = new BufferedInputStream(part.getInputStream(), 8192);
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(uploadFile), 8192);) {

            byte[] buffer = new byte[8192];
            for (int length = 0; ((length = input.read(buffer)) > 0);) {
                output.write(buffer, 0, length);
            }
        } 
        
        part.delete();
        
        return uploadFile.getAbsolutePath();
    }

    private String getValue(Part part, String encoding) throws IOException {

    	BufferedReader reader
                = new BufferedReader(new InputStreamReader(part.getInputStream(), encoding));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[8192];
        for (int length; (length = reader.read(buffer)) > 0;) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }

    static class FormDataImpl implements FormData {
    	
    	private final Map<String, String> data;
    	
    	FormDataImpl(Map<String, String> data) {
    		this.data = data;
    	}

		@Override
		public Set<String> getParameterNames() {
			return data.keySet();
		}

		@Override
		public String getParameter(String name) {
			return data.get(name);
		}

    	
    }
}