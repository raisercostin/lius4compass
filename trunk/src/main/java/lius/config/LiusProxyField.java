package lius.config;

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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

/**
 * Class: LiusProxyField <br>
 * This class simply maps all requests to a 'real' LiusField object. The
 * intention of this class is to serve as an entrypoint for multiple (equal)
 * fields from a single document. Changelog:
 * <ul>
 * <li>02.06.2005: Initial implementation (jf)</li>
 * </ul>
 * 
 * @see lius.config.LiusValueProxyField
 * @see lius.index.application.VCardIndexer
 * @author <a href="mailto:jf@teamskill.de">Jens Fendler </a>
 */
public abstract class LiusProxyField extends LiusField {
    private LiusField lf;

    public LiusProxyField(LiusField realField) {
        if (realField == null)
            throw new IllegalArgumentException(
                    "A valid LiusField instance is required for LiusProxyField.");
        this.lf = realField;
    }

    @Override
    public String getName() {
        return lf.getName();
    }

    @Override
    public void setName(String name) {
        lf.setName(name);
    }

    @Override
    public String getXpathSelect() {
        return lf.getXpathSelect();
    }

    @Override
    public void setXpathSelect(String xpathSelect) {
        lf.setXpathSelect(xpathSelect);
    }

    @Override
    public String getType() {
        return lf.getType();
    }

    @Override
    public void setType(String type) {
        lf.setType(type);
    }

    @Override
    public String getValue() {
        return lf.getValue();
    }

    @Override
    public void setValue(String value) {
        lf.setValue(value);
    }

    @Override
    public String getOcurSep() {
        return lf.getOcurSep();
    }

    @Override
    public void setOcurSep(String ocurSep) {
        lf.setOcurSep(ocurSep);
    }

    @Override
    public void setDateLong(long dateLong) {
        lf.setDateLong(dateLong);
    }

    @Override
    public long getDateLong() {
        return lf.getDateLong();
    }

    @Override
    public void setDate(Date date) {
        lf.setDate(date);
    }

    @Override
    public Date getDate() {
        return lf.getDate();
    }

    @Override
    public void setGetMethod(String getMethod) {
        lf.setGetMethod(getMethod);
    }

    @Override
    public String getGetMethod() {
        return lf.getGetMethod();
    }

    @Override
    public void setGet(String get) {
        lf.setGet(get);
    }

    @Override
    public String getGet() {
        return lf.getGet();
    }

    @Override
    public void setValueInputStreamReader(
            InputStreamReader valueInputStreamReader) {
        lf.setValueInputStreamReader(valueInputStreamReader);
    }

    @Override
    public InputStreamReader getValueInputStreamReader() {
        return lf.getValueInputStreamReader();
    }

    @Override
    public void setValueReader(Reader valueReader) {
        lf.setValueReader(valueReader);
    }

    @Override
    public Reader getValueReader() {
        return lf.getValueReader();
    }

    @Override
    public void setLabel(String label) {
        lf.setLabel(label);
    }

    @Override
    public String getLabel() {
        return lf.getLabel();
    }

    @Override
    public String[] getValues() {
        return lf.getValues();
    }

    @Override
    public void setValues(String[] values) {
        lf.setValues(values);
    }

    @Override
    public void setDateFormat(String dateFormat) {
        lf.setDateFormat(dateFormat);
    }

    @Override
    public String getDateFormat() {
        return lf.getDateFormat();
    }

    @Override
    public void setBoost(float boost) {
        lf.setBoost(boost);
    }

    @Override
    public float getBoost() {
        return lf.getBoost();
    }

    @Override
    public void setIsBoosted(boolean isBoostedB) {
        lf.setIsBoosted(isBoostedB);
    }

    @Override
    public boolean getIsBoosted() {
        return lf.getIsBoosted();
    }

    @Override
    public void setFragmenter(String fragmenter) {
        lf.setFragmenter(fragmenter);
    }

    @Override
    public String getFragmenter() {
        return lf.getFragmenter();
    }
}