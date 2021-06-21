package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils;

import org.json.JSONObject;
import org.json.XML;

public class XMLToJSONConverter {

    public XMLToJSONConverter() {
    }

    public JSONObject parse(String XMLString) {

        return XML.toJSONObject(XMLString);
    }
}
