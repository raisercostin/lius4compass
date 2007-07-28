/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lius.index.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lius.config.LiusConfig;
import lius.config.LiusField;
import lius.config.LiusProxyField;
import lius.config.LiusValueProxyField;
import lius.index.BaseIndexer;
import lius.index.ParsingResult;
import lius.index.parser.VCardParser;
import lius.index.util.LiusUtils;
import lius.index.util.VCard;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

/**
 * Class: VCardIndexer <br>
 * This Indexer reads .vcf and .vcard files as exported by various email and PIM
 * applications (e.g. Mozilla Mail, Evolution, KMail). Fields which can be
 * indexed are:
 * <ul>
 * <li><code>name</code>: The contact's full name</li>
 * <li><code>title</code>: Title (e.g. 'Dr.')</li>
 * <li><code>nickname</code>: nickname</li>
 * <li><code>birthday</code>: user notes</li>
 * <li><code>email</code>: email addresses</li>
 * <li><code>phone</code>: all available phone numbers</li>
 * <li><code>homephone</code>: home phone number</li>
 * <li><code>workphone</code>: work phone number</li>
 * <li><code>cellphone</code>: cell phone number</li>
 * <li><code>categories</code>: categories as used by the
 * creator-application </li>
 * <li><code>address</code>: all available addresses</li>
 * <li><code>homeaddress</code>: the home address</li>
 * <li><code>workaddress</code>: the work address</li>
 * <li><code>url</code>: the URL (usually an http address)</li>
 * <li><code>organization</code>: the organization</li>
 * </ul>
 * <br/> Changelog:
 * <ul>
 * <li>02.06.2005: Initial implementation (jf)</li>
 * </ul>
 * 
 * @author <a href="mailto:jf@teamskill.de">Jens Fendler </a>
 */
/**
 * Adapted by Rida Benjelloun
 */
public class VCardIndexer extends BaseIndexer {
    static Logger logger = Logger.getLogger(VCardIndexer.class);

    public int getType() {
        return 1;
    }

    public boolean isConfigured(LiusConfig liusConfig) {
        return liusConfig.getVCardFields() != null;
    }

    public Collection getConfigurationFields(LiusConfig liusConfig) {
        return liusConfig.getVCardFields();
    }

    public ParsingResult parseResource(LiusConfig liusConfig, Resource resource) {
        Collection c = new ArrayList();
        try {
            VCardParser vcp = new VCardParser(resource);
            for (Iterator i = liusConfig.getVCardFields().iterator(); i
                    .hasNext();) {
                Object next = i.next();
                if (next instanceof LiusField) {
                    LiusField lf = (LiusField) next;
                    addVCardProxyFields(vcp, lf, c);
                } else
                    c.add(next);
            }
        } catch (IOException e) {
            LiusUtils.doOnException( e);
        }
        return new ParsingResult(c,"");
    }

    private void addVCardProxyFields(VCardParser vcp, LiusField lf, Collection c) {
        if ("name".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "FN", null, lf, c);
        } else if ("title".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "TITLE", null, lf, c);
        } else if ("nickname".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "NICKNAME", null, lf, c);
        } else if ("birthday".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "BDAY", null, lf, c);
        } else if ("notes".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "NOTE", null, lf, c);
        } else if ("email".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "EMAIL", null, lf, c);
        } else if ("phone".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "TEL", null, lf, c);
        } else if ("homephone".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "TEL", "HOME", lf, c);
        } else if ("workphone".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "TEL", "WORK", lf, c);
        } else if ("cellphone".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "TEL", "CELL", lf, c);
        } else if ("categories".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "CATEGORIES", null, lf, c);
        } else if ("address".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "ADR", null, lf, c);
        } else if ("homeaddress".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "ADR", "HOME", lf, c);
        } else if ("workaddress".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "ADR", "WORK", lf, c);
        } else if ("url".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "URL", null, lf, c);
        } else if ("organization".equalsIgnoreCase(lf.getGet())) {
            addElements(vcp, "ORG", null, lf, c);
        }
    }

    private void addElements(VCardParser vcp, String key, String type,
            LiusField realField, Collection c) {
        List cards = vcp.getCards();
        for (int i = 0; i < cards.size(); i++) {
            VCard card = (VCard) cards.get(i);
            String data = (type == null) ? card.getElement(key) : card
                    .getElement(key, type);
            if (data != null) {
                LiusProxyField lpf = new LiusValueProxyField(realField);
                lpf.setValue(data);
                c.add(lpf);
            }
        }
    }
}