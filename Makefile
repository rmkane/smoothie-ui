.PHONY: help dev prod build package test clean compile verify install

MVN       := mvn
ARTIFACT  := smoothies
VERSION   := 1.0-SNAPSHOT
JAR       := target/$(ARTIFACT)-$(VERSION).jar
MAIN_CLASS := org.example.smoothies.SmoothieApp

.DEFAULT_GOAL := help

## help: Show this help message
help:
	@echo "Smoothie Maker — available targets:"
	@echo ""
	@grep -E '^## ' $(MAKEFILE_LIST) | sed 's/## /  /' | column -t -s ':'
	@echo ""
	@echo "Examples:"
	@echo "  make dev          # run with Spring Boot (hot classpath via devtools if added)"
	@echo "  make prod         # build fat JAR and run it"
	@echo "  make test         # run unit tests"

## dev: Run the app via Spring Boot (development)
dev:
	$(MVN) spring-boot:run

## prod: Build the fat JAR and run it (production-like)
prod: package
	java -jar $(JAR)

## build: Package the app without running tests
build:
	$(MVN) package -DskipTests

## package: Build the executable Spring Boot JAR
package:
	$(MVN) package -DskipTests

## test: Run unit tests
test:
	$(MVN) test

## compile: Compile main and test sources
compile:
	$(MVN) compile test-compile

## verify: Run tests and package
verify:
	$(MVN) verify

## install: Install artifact to local Maven repository
install:
	$(MVN) install

## clean: Remove build output
clean:
	$(MVN) clean

## run: Alias for dev
run: dev

## jar-path: Print the path to the packaged JAR
jar-path:
	@echo $(JAR)
