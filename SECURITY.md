# Security Policy

## Reporting a Vulnerability

If you believe you’ve found a security vulnerability, please do not open a public issue.

- Email: security@example.org (replace with a real contact for your project)
- Provide a clear description, affected versions, and a proof of concept if possible.
- We will acknowledge receipt within 72 hours and provide a timeline for remediation when possible.

## Supported Versions

This is a starter project; generally the latest `main` branch is supported. Tag releases will include security notes when applicable.

## Dependencies

- Keep dependencies updated. Use Dependabot/Renovate in CI to detect vulnerable libraries.
- Prefer LTS versions of frameworks. Avoid unmaintained libraries.

## Secret Management

- Do not commit secrets. Use environment variables, Docker secrets, or a secret manager.
- Rotate secrets periodically and immediately upon suspected exposure.

## Hardening Guidelines

- Enable HTTPS in production (TLS termination at the ingress/proxy).
- Add `spring-boot-starter-security` when enabling authentication; deny by default.
- Validate and sanitize all inputs. Enforce strict Content-Type headers.
- Implement rate limiting and account lockout for auth endpoints.
- Log security-relevant events with redaction of sensitive data.

## Data Privacy

- Store only necessary PII and PHI. Apply data minimization principles.
- Encrypt data at rest where applicable and in transit always.
- Comply with applicable regulations (HIPAA/GDPR). Consult counsel for requirements.
