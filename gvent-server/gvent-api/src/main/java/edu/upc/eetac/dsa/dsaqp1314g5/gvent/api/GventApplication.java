package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;


public class GventApplication extends ResourceConfig {
	public GventApplication(){
		super();
		register(DeclarativeLinkingFeature.class);
	}

}
