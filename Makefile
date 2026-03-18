.PHONY: help dev test build clean

GRADLEW := ./gradlew

help:
	@printf "Available targets:\n"
	@printf "  make dev    Start the Spring Boot dev server\n"
	@printf "  make test   Run the test suite\n"
	@printf "  make build  Clean and build the project\n"
	@printf "  make clean  Remove build outputs\n"

dev:
	$(GRADLEW) bootRun -t

test:
	$(GRADLEW) test

build:
	$(GRADLEW) clean build

clean:
	$(GRADLEW) clean
