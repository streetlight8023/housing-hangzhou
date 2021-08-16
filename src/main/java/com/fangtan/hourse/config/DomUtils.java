package com.fangtan.hourse.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class DomUtils {


    public static Document getXMLByString(String xmlstr) throws DocumentException {
        if (xmlstr == "" || xmlstr == null) {
            return null;
        }
        Document document = DocumentHelper.parseText(xmlstr);
        return document;
    }

    public static List<Element> getDetailList(String xmlstr) throws DocumentException {
        return getDetailList(getXMLByString(xmlstr));
    }

    public static List<Element> getDetailList(Document document) {
        Element root = document.getRootElement();
        List<Element> listElements = root.elements("listX");
        for (Element element : listElements) {
            Element parent = element.element("i_03").getParent();

        }

        return null;
    }


}
