# Password Manager Project

## Project Purpose

This is a desktop password manager application designed to securely store and manage user credentials, passwords, and other sensitive information. The application provides:

- Secure storage of login credentials, notes, and other sensitive data
- Password generation and strength evaluation
- Two-factor authentication support (including TOTP)
- Encrypted database storage
- Search and organization capabilities for stored items
- Cross-platform desktop access to passwords

The goal is to provide a secure, user-friendly alternative to commercial password managers with full transparency of the codebase.

## Technology Stack

- **Language**: Java 25
- **Build Tool**: Maven (pom.xml)
- **GUI Framework**: JavaFX
- **Encryption**: Java Cryptography Architecture (JCA)
- **Testing**: JUnit 6
- **Version Control**: Git

## Instructions for AI Agents

### General Guidelines
- Always follow existing code conventions for style, naming, and architecture
- Always use latest available syntax for Java
- Prefer using established Java security APIs for cryptographic operations
- Maintain backward compatibility with existing data formats
- Write unit tests for new functionality
- Document public APIs with JavaDoc

### Unit Tests
- Use latest JUnit API
- Prefer @ParameterizedTest with @MethodSource over multiple methods

### Configuration Management

Commit Messages:
- Commit title must be "Password Manager <version>"
- Take version from pom.xml
- Include motivation for changes in commit body
- Add `Co-Authored-By: GigaCode <gigacode@sber.ru>` when AI-assisted
- Reference issues when applicable
