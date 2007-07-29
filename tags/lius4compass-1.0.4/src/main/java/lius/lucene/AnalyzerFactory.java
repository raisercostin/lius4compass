package lius.lucene;/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *      http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */import java.lang.reflect.Constructor;import lius.config.LiusConfig;import lius.index.util.LiusUtils;import org.apache.log4j.Logger;import org.apache.lucene.analysis.Analyzer;/** * @author Rida Benjelloun (ridabenjelloun@gmail.com) */public class AnalyzerFactory {    static Logger logger = Logger.getLogger(AnalyzerFactory.class);    /**     * M�thode se basant sur la r�flexion. Elle retourne un objet de type     * "Analyzer" en se basant sur le nom de classe entr� par l'utilisateur dans     * le fichier de configuration. Elle permet �galement de construire un objet     * analyzer en utilisant une liste de mots vides, ceci se fait en appelant     * un constructeur qui se base sur un tableau de mots vides. Le tableau de     * mots vides est construit � patir du fichier de configuration. <br/><br/>     * Method that bases itself on reflection. It returns an Analyzer object     * based from the class name given by the configuration file. Also, the     * constructed Analyser object can be based on a list of stop words, by     * calling a constructor based on stop words. The list of stop words is     * taken form the configuration file.     */    public static Analyzer getAnalyzer(LiusConfig xc) {        if (xc == null)            throw new IllegalArgumentException("LiusConfig shouldn't be null.");        Analyzer an = null;        try {            Class classe = Class.forName(xc.getAnalyzer());            Object analyzerInstance = null;            if (xc.getStopWord() != null) {                Constructor[] con = classe.getConstructors();                for (int i = 0; i < con.length; i++) {                    Class[] conParam = con[i].getParameterTypes();                    if (conParam.length == 0 && an == null) {                        analyzerInstance = classe.newInstance();                        an = (Analyzer) analyzerInstance;                        logger.info("L'analyseur : " + xc.getAnalyzer()                                + " ne prend pas de mots vides");                    } else {                        for (int j = 0; j < conParam.length; j++) {                            if (conParam[j].getName().equals(                                    "[Ljava.lang.String;")) {                                an = (Analyzer) con[i]                                        .newInstance(new Object[] { xc                                                .getStopWord() });                            }                        }                    }                }            } else {                analyzerInstance = classe.newInstance();                an = (Analyzer) analyzerInstance;            }        } catch (ClassNotFoundException e) {            throw new RuntimeException("ClassNotFoundException", e);        } catch (InstantiationException e) {            throw new RuntimeException("La classe est abstract ou est une interface", e);        } catch (IllegalAccessException e) {            throw new RuntimeException("La classe n'est pas accessible", e);        } catch (java.lang.reflect.InvocationTargetException e) {            throw new RuntimeException(" Exception d�clench�e si le constructeur invoqu�", e);        } catch (IllegalArgumentException e) {            throw new RuntimeException(" Mauvais type de param�tre", e);        }        return an;    }}