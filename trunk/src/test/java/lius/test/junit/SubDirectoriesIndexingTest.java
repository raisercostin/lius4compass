package lius.test.junit;

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

import junit.framework.TestCase;
import lius.config.LiusConfig;
import lius.config.LiusConfigBuilder;
import lius.lucene.LuceneActions;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class SubDirectoriesIndexingTest extends TestCase {
    private String indexDir;
    private File toIndex;
    private LiusConfig lc;
    private Resource liusConfig;
    private LuceneActions luceneActions;

    public SubDirectoriesIndexingTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        indexDir = new File("target/indexDir1").getAbsolutePath();
        liusConfig = new ClassPathResource("liusConfig.xml");
        luceneActions = new LuceneActions();
        luceneActions.newIndex(indexDir);
        lc = LiusConfigBuilder.getSingletonInstance().getLiusConfig(liusConfig);
    }

    public void testSubDirectoriesIndexing() throws IOException {
        toIndex = new ClassPathResource("lius/ExempleFiles").getFile();
        luceneActions.indexSubDirectories(toIndex, indexDir, lc);
    }
}
