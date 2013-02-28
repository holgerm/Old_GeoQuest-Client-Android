package com.qeevee.gq.tests.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.qeevee.gq.tests.robolectric.GQTestRunner;

@RunWith(GQTestRunner.class)
public class ReadGameXML {

    Element root;

    private void loadGameXML(String gameName) {
	SAXReader reader = new SAXReader();
	URL xmlFileURL = this.getClass().getResource("/testgames/" + gameName
		+ "/game.xml");
	Document document;
	try {
	    document = reader.read(new File(xmlFileURL.getFile()));
	    root = document.getRootElement();
	} catch (DocumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testQRTag_Treasure_Demo_NoSchema() {
	loadGameXML("QRTag_Treasure_Demo_NoSchema");
	assertEquals("QRTag Treasure Demo",
		     root.attributeValue("name"));
	assertEquals(2,
		     root.selectNodes("//mission").size());
    }

    @Test
    public void testQRTag_Treasure_Demo() {
	loadGameXML("QRTag_Treasure_Demo");
	assertEquals("QRTag Treasure Demo",
		     root.attributeValue("name"));
	assertEquals(2,
		     root.selectNodes("//mission").size());
    }

}
