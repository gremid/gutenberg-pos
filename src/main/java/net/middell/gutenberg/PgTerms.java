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
package net.middell.gutenberg;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

/**
 * @author <a href="http://gregor.middell.net/">Gregor Middell</a>
 */
public class PgTerms {

  private static final Model memoryModel = ModelFactory.createDefaultModel();

  public static final String NS = "http://www.gutenberg.org/2009/pgterms/";

  public static Property name = memoryModel.createProperty(NS, "name");

  public static Property downloads = memoryModel.createProperty(NS, "downloads");

}
