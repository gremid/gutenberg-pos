## Synopsis

Applies part-of-speech (POS) tagging to a sample of Project Gutenberg
texts.

## Code Example

    mvn compile exec:java
    find samples/tei/

## Motivation

Originally created as a performance test for a text repository project
at [Huygens ING](https://www.huygens.knaw.nl/), this is an attempt to
create a densely marked-up corpus of freely available texts for
various purposes, specifically for testing.

## Installation/ Requirements

* Java v8
* Apache Maven

## Contributors

For the POS tagging, software from
the [Stanford NLP Group](https://nlp.stanford.edu/software/) is used.

## License

Available under Apache License v2.0.
