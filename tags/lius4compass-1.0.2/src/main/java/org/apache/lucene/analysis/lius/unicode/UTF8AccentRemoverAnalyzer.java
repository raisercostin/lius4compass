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
package org.apache.lucene.analysis.lius.unicode;

/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
import java.io.Reader;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class UTF8AccentRemoverAnalyzer extends Analyzer {
    /** Creates a new instance of AccentUnicodeAnalyzer */
    public UTF8AccentRemoverAnalyzer() {
        this(FRENCH_STOP_WORDS);
    }

    private Set stopSet;

    /** Builds an analyzer with the given stop words. */
    public UTF8AccentRemoverAnalyzer(String[] stopWords) {
        stopSet = StopFilter.makeStopSet(stopWords);
    }

    public final static String[] FRENCH_STOP_WORDS = { "a", "afin", "ai",
            "ainsi", "apr�s", "attendu", "au", "aujourd", "auquel", "aussi",
            "autre", "autres", "aux", "auxquelles", "auxquels", "avait",
            "avant", "avec", "avoir", "c", "car", "ce", "ceci", "cela",
            "celle", "celles", "celui", "cependant", "certain", "certaine",
            "certaines", "certains", "ces", "cet", "cette", "ceux", "chez",
            "ci", "combien", "comme", "comment", "concernant", "contre", "d",
            "dans", "de", "debout", "dedans", "dehors", "del�", "depuis",
            "derri�re", "des", "d�sormais", "desquelles", "desquels",
            "dessous", "dessus", "devant", "devers", "devra", "divers",
            "diverse", "diverses", "doit", "donc", "dont", "du", "duquel",
            "durant", "d�s", "elle", "elles", "en", "entre", "environ", "est",
            "et", "etc", "etre", "eu", "eux", "except�", "hormis", "hors",
            "h�las", "hui", "il", "ils", "j", "je", "jusqu", "jusque", "l",
            "la", "laquelle", "le", "lequel", "les", "lesquelles", "lesquels",
            "leur", "leurs", "lorsque", "lui", "l�", "ma", "mais", "malgr�",
            "me", "merci", "mes", "mien", "mienne", "miennes", "miens", "moi",
            "moins", "mon", "moyennant", "m�me", "m�mes", "n", "ne", "ni",
            "non", "nos", "notre", "nous", "n�anmoins", "n�tre", "n�tres",
            "on", "ont", "ou", "outre", "o�", "par", "parmi", "partant", "pas",
            "pass�", "pendant", "plein", "plus", "plusieurs", "pour",
            "pourquoi", "proche", "pr�s", "puisque", "qu", "quand", "que",
            "quel", "quelle", "quelles", "quels", "qui", "quoi", "quoique",
            "revoici", "revoil�", "s", "sa", "sans", "sauf", "se", "selon",
            "seront", "ses", "si", "sien", "sienne", "siennes", "siens",
            "sinon", "soi", "soit", "son", "sont", "sous", "suivant", "sur",
            "ta", "te", "tes", "tien", "tienne", "tiennes", "tiens", "toi",
            "ton", "tous", "tout", "toute", "toutes", "tu", "un", "une", "va",
            "vers", "voici", "voil�", "vos", "votre", "vous", "vu", "v�tre",
            "v�tres", "y", "�", "�a", "�s", "�t�", "�tre", "�", "l'" };

    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new StandardTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopSet);
        result = new UTF8AccentRemoverFilter(result);
        return result;
    }
}
