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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lius.config.LiusConfig;
import lius.index.BaseIndexer;
import lius.index.IndexService;
import lius.index.Indexer;
import lius.index.IndexerFactory;
import lius.index.MimeTypeUtils;
import lius.index.ParsingResult;
import lius.index.util.LiusUtils;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class ZIPIndexer extends BaseIndexer {
    static Logger logger = Logger.getLogger(ZIPIndexer.class);

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean isConfigured(LiusConfig liusConfig) {
        boolean ef = false;
        if (liusConfig.getZipFields() != null)
            return ef = true;
        return ef;
    }

    @Override
    public Collection getConfigurationFields(LiusConfig liusConfig) {
        return liusConfig.getZipFields();
    }

    @Override
    public ParsingResult parseResource(LiusConfig liusConfig, Resource resource) {
        try {
            ParsingResult resColl = new ParsingResult("");
            List files = LiusUtils.unzip(resource.getInputStream());
            for (int i = 0; i < files.size(); i++) {
                FileSystemResource fileSystemResource = new FileSystemResource(
                        (File) files.get(i));
                IndexService indexer = IndexerFactory.getIndexer(
                        fileSystemResource, liusConfig);
                resColl.addAll(indexer.getLiusDocument(fileSystemResource));
            }
            resColl.reinit();
            return resColl;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
