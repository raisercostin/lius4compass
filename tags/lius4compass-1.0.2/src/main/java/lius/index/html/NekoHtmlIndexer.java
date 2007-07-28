package lius.index.html;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;

import lius.config.LiusConfig;
import lius.index.BaseIndexer;
import lius.index.ParsingResult;
import lius.index.util.LiusUtils;
import lius.index.xml.XmlFileIndexer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.jdom.input.DOMBuilder;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Classe permettant d'indexer des fichiers HTML <br/><br/> Class for indexing
 * HTML files.
 * 
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 * @author Nicolas Belisle (nicolas.belisle@doculibre.com)
 */
public class NekoHtmlIndexer extends BaseIndexer {
    public static Logger logger = Logger.getLogger(NekoHtmlIndexer.class);
    private XmlFileIndexer xfi;

    public NekoHtmlIndexer() {
        xfi = new XmlFileIndexer();
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean isConfigured(LiusConfig liusConfig) {
        boolean ef = false;
        if (liusConfig.getHtmlFields() != null)
            return ef = true;
        return ef;
    }

    @Override
    public Collection getConfigurationFields(LiusConfig liusConfig) {
        return liusConfig.getHtmlFields();
    }

    private File omitXMLDeclaration(InputStream fis)
            throws FileNotFoundException, IOException {
        BufferedWriter out = null;
        BufferedReader in = null;
        File liusTmp;
        try {
            String line = null;
            liusTmp = File.createTempFile("tmp", "LiusNekoHtml.xml");
            in = new BufferedReader(new InputStreamReader(fis));
            FileOutputStream fos = new FileOutputStream(liusTmp);
            out = new BufferedWriter(new OutputStreamWriter(fos));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("<?xml")) {
                    int offset = line.indexOf("?>");
                    out.write(line.substring(offset + 2));
                } else {
                    out.write(line);
                }
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
        return liusTmp;
    }

    private org.jdom.Document parse(InputStream is) {
        File newTmpFile = null;
        org.jdom.Document jdomDoc = null;
        FileInputStream fis = null;
        try {
            newTmpFile = omitXMLDeclaration(is);
            DOMParser parser = new DOMParser();
            fis = new FileInputStream(newTmpFile.getAbsolutePath());
            parser.parse(new InputSource(fis));
            org.w3c.dom.Document domDoc = parser.getDocument();
            jdomDoc = convert(domDoc);
        } catch (SAXException e) {
            LiusUtils.doOnException(e);
        } catch (IOException e) {
            LiusUtils.doOnException(e);
        } catch (Exception e) {
            LiusUtils.doOnException(e);
        } finally {
            try {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } finally {
                    if (newTmpFile != null) {
                        FileUtils.forceDelete(newTmpFile);
                    }
                }
            } catch (IOException ioe) {
                LiusUtils.doOnException(ioe);
            }
        }
        return jdomDoc;
    }

    public org.jdom.Document convert(org.w3c.dom.Document domDoc) {
        DOMBuilder builder = new DOMBuilder();
        org.jdom.Document jdomDoc = builder.build(domDoc);
        return jdomDoc;
    }

    @Override
    public ParsingResult parseResource(LiusConfig liusConfig,
            Resource resource) {
        try {
            org.jdom.Document jdomDoc = this.parse(resource.getInputStream());
            return xfi.getPopulatedLiusFields(jdomDoc,
                    getConfigurationFields(liusConfig), resource);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}