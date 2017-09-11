/*
 * Copyright © 2017 Gregor Middell (http://gregor.middell.net/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.middell.nlp;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Properties;

import static net.middell.gutenberg.Text.TEI_NS_URI;

/**
 * @author <a href="http://gregor.middell.net/">Gregor Middell</a>
 */
public class Pipeline {

  private final StanfordCoreNLP nlp;

  public Pipeline() {
    final Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
    nlp = new StanfordCoreNLP(props);
  }


  public Annotation annotate(String text) {
    final Annotation annotation = new Annotation(text);
    nlp.annotate(annotation);
    return annotation;
  }

  public void writeAnnotated(XMLStreamWriter xml, String text) throws XMLStreamException {
    int offset = 0;
    xml.writeStartElement(TEI_NS_URI, "ab");
    for (CoreMap sentence: annotate(text).get(SentencesAnnotation.class)) {
      boolean sentenceStarted = false;
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        final int tokenStart = token.beginPosition();
        writeText(xml, text.substring(offset, tokenStart));
        if (!sentenceStarted) {
          xml.writeStartElement(TEI_NS_URI, "s");
          sentenceStarted = true;
        }
        xml.writeStartElement(TEI_NS_URI, "w");
        xml.writeAttribute("lemma", token.get(LemmaAnnotation.class));
        xml.writeAttribute("type", token.get(PartOfSpeechAnnotation.class));
        xml.writeAttribute("function", token.get(NamedEntityTagAnnotation.class));
        writeText(xml, text.substring(tokenStart, offset = token.endPosition()));
        xml.writeEndElement();
      }
      if (sentenceStarted) {
        xml.writeEndElement();
      }
    }
    xml.writeEndElement();
  }

  private void writeText(XMLStreamWriter xml, String text) throws XMLStreamException {
    final int length = text.length();
    int offset = 0;
    int lb;
    while (offset < length) {
      lb = text.indexOf('\n', offset);
      if (offset < lb) {
        xml.writeCharacters(text.substring(offset, lb));
      }
      if (lb >= 0) {
        xml.writeEmptyElement(TEI_NS_URI, "lb");
        offset = lb + 1;
      } else {
        xml.writeCharacters(text.substring(offset, length));
        break;
      }
    }
  }
}
