package com.qeevee.gq.base;

import org.dom4j.Element;

import com.qeevee.gq.ui.abstrakt.MissionOrToolUI;


public interface MissionOrToolActivity extends BlockableAndReleasable {

	MissionOrToolUI getUI();

	Element getXML();

	void setKeepActivity(boolean keep);

	boolean keepsActivity();

	void setBackAllowed(boolean backAllowed);

	boolean isBackAllowed();

	String getMissionID();

	void finish(Double status);

}
