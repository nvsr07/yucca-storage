package org.csi.yucca.storage.datamanagementapi.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
	private Set<Object> singletons = new HashSet<Object>();

	public ApplicationConfig() {
		singletons.add(new MetadataService());
	}

	public Set<Object> getSingletons() {
		return singletons;
	}
}
