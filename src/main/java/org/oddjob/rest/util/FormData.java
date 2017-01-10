package org.oddjob.rest.util;

import java.util.Set;

public interface FormData {

	public Set<String> getParameterNames();
	
	public String getParameter(String name);
	
	
}
