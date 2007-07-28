package lius.search;

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
import java.util.List;
import java.util.Map;

import lius.config.LiusField;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class LiusHit {
    private double score;
    private List<LiusField> liusFields;
    private Map liusFieldsMap;
    private int docId;

    public List<LiusField> getLiusFields() {
        return liusFields;
    }

    public void setLiusFields(List<LiusField> liusFields) {
        this.liusFields = liusFields;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public Map getLiusFieldsMap() {
        return liusFieldsMap;
    }

    public void setLiusFieldsMap(Map liusFieldsMap) {
        this.liusFieldsMap = liusFieldsMap;
    }
}
