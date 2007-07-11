package lius.index.zip;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lius.index.Indexer;
import lius.index.IndexerFactory;

import org.apache.log4j.Logger;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class ZIPIndexer extends Indexer {
    static Logger logger = Logger.getLogger(ZIPIndexer.class);

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean isConfigured() {
        boolean ef = false;
        if (getLiusConfig().getZipFields() != null)
            return ef = true;
        return ef;
    }

    @Override
    public Collection getConfigurationFields() {
        return getLiusConfig().getZipFields();
    }

    @Override
    public String getContent() {
        StringBuffer content = new StringBuffer();
        List indexers = IndexerFactory.getIndexersFromZipInputStream(
                getStreamToIndex(), getLiusConfig());
        for (int i = 0; i < indexers.size(); i++) {
            content.append(((Indexer) indexers.get(i)).getContent());
        }
        return content.toString();
    }

    @Override
    public Collection getPopulatedLiusFields() {
        Collection resColl = new ArrayList();
        Indexer indexer = null;
        List indexers = IndexerFactory.getIndexersFromZipInputStream(
                getStreamToIndex(), getLiusConfig());
        for (int i = 0; i < indexers.size(); i++) {
            indexer = (Indexer) indexers.get(i);
            resColl.addAll(indexer.getPopulatedLiusFields());
        }
        return resColl;
    }
}
