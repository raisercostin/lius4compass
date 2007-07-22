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
package lius.test.junit;

import java.io.File;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import lius.config.LiusConfig;
import lius.config.LiusConfigBuilder;
import lius.index.util.LiusUtils;
import lius.lucene.LuceneActions;
import lius.search.LiusHitList;
import lius.search.SearchIndex;

import org.springframework.core.io.ClassPathResource;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LiusTestSearch extends TestCase {
    private static String indexDir;
    private String indexDir1;
    private LiusConfig liusConfig;
    private SearchIndex searchIndex;

    public LiusTestSearch(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        searchIndex = new SearchIndex();
        searchIndex.setLuceneActions(new LuceneActions());
        liusConfig = LiusConfigBuilder.getSingletonInstance().getLiusConfig(
                new ClassPathResource("liusConfig.xml"));
        String sep = File.separator;
        StringTokenizer st = new StringTokenizer(System
                .getProperty("java.class.path"), File.pathSeparator);
        File classDir = new File(st.nextToken());
        indexDir = classDir.getParent() + sep + "indexDir";
        indexDir1 = classDir.getParent() + sep + "indexDir1";
    }

    public void testSearchIndexPrinLs() {
        LiusHitList res = searchIndex.search("reda", indexDir, liusConfig);
        System.out.println(res.size());
        LiusUtils.printResultsFormLiusHitsList(res);
    }

    public void testMultiIndexSearchPrinLs() {
        String[] indexs = { indexDir, indexDir1 };
        LiusHitList res = searchIndex.multiIndexSearch("la*", indexs,
                liusConfig);
        LiusUtils.printResultsFormLiusHitsList(res);
    }
}