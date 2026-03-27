.PHONY: help dev test build clean db-up db-down docker-dev

GRADLEW := ./gradlew
JWT_SECRET := $(shell openssl rand -base64 32)

ifneq (,$(wildcard .env))
	include .env
	export
endif

help:
	@printf "Available targets:\n"
	@printf "  make dev    Start the Spring Boot dev server\n"
	@printf "  make test   Run the test suite\n"
	@printf "  make build  Clean and build the project\n"
	@printf "  make clean  Remove build outputs\n"
	@printf "  make db-up  Start PostgreSQL in Docker\n"
	@printf "  make db-down Stop PostgreSQL in Docker\n"
	@printf "  make db-prune   Stop PostgreSQL and remove its Docker volume\n"
	@printf "  make docker-dev Start the API and PostgreSQL with Docker Compose\n"

dev: db-up
	JWT_SECRET=$(JWT_SECRET) $(GRADLEW) bootRun -t

test: db-up
	JWT_SECRET=$(JWT_SECRET) $(GRADLEW) test

build:
	$(GRADLEW) clean build

clean:
	$(GRADLEW) clean

db-up:
	docker compose up -d postgres

db-down:
	docker compose down

db-prune:
	docker compose down -v

docker-dev:
	docker compose up --build
