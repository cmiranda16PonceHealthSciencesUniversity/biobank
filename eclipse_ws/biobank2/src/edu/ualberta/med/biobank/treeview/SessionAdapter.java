package edu.ualberta.med.biobank.treeview;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionAdapter extends Node {
	
	private WritableApplicationService appService;
	
	public SessionAdapter(Node parent, WritableApplicationService appService, 
			int sessionId, String name) {
		super(parent);
		this.appService = appService;
		setId(sessionId);
		setName(name);
	}

	public WritableApplicationService getAppService() {
		return appService;
	}

	@Override
	public int getId() {
		return 0;
	}
}
