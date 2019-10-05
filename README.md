# WordAnalyzer
Text Analyzing Tool

## Quickstart

- Get the code
  - `git clone https://github.com/amilamanoj/word-analyzer.git`
- Build the project
    - Execute `mvn install` on command-line
- Download and extract latest Wiktionary database dump:
    - https://dumps.wikimedia.org/enwiktionary/latest/enwiktionary-latest-pages-articles.xml.bz2
- Parse the database dump
- Configure the Wiktionary path in `src/main/resources/application.properties`
- Run the application:
    - Execute `mvn spring-boot:run` on command-line
- Open the application
    - Open `localhost:8080` on browser
