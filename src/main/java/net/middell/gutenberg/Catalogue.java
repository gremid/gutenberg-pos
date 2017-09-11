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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.impl.RDFDefaultErrorHandler;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * @author <a href="http://gregor.middell.net/">Gregor Middell</a>
 */
public class Catalogue implements Iterable<Model>, Closeable {

  private final URL source;

  private InputStream stream = null;

  public static Catalogue cached(File cacheFile) throws IOException {
    if (!cacheFile.isFile()) {
      try (
        final InputStream in = new Catalogue().source.openStream();
        final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(cacheFile))
      ) {
        final byte[] buf = new byte[8192];
        while (true) {
          final int read = in.read(buf);
          if (read < 0) {
            break;
          }
          out.write(buf, 0, read);
        }

      }
    }

    return new Catalogue(cacheFile.toURI().toURL());
  }

  public Catalogue() throws MalformedURLException {
    this(new URL("https://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.bz2"));
  }

  public Catalogue(URL source) {
    this.source = source;
  }

  @Override
  public Iterator<Model> iterator() {
    try {
      stream = source.openStream();
      final BZip2CompressorInputStream compressedStream = new BZip2CompressorInputStream(stream);
      final TarArchiveInputStream catalogArchiveStream = new TarArchiveInputStream(compressedStream);

      return new Iterator<Model>() {

        ArchiveEntry nextEntry = null;

        @Override
        public boolean hasNext() {
          try {
            if (nextEntry == null) {
              nextEntry = catalogArchiveStream.getNextEntry();
            }
            return (nextEntry != null);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }

        @Override
        public Model next() {
          final Model model = ModelFactory.createDefaultModel();

          final RDFReader modelReader = model.getReader();
          modelReader.setErrorHandler(new RDFDefaultErrorHandler() {
            @Override
            public void warning(Exception e) {
              // ignore warnings
            }
          });
          modelReader.read(model, new FilterInputStream(catalogArchiveStream) {

            @Override
            public void close() throws IOException {
              // no-op
            }
          }, null);

          nextEntry = null;
          return model;
        }
      };
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    if (stream != null) {
      stream.close();
      stream = null;
    }
  }
}
