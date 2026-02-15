# Publishing to Maven Central (Sonatype OSSRH) + GitHub Packages

This document captures the practical setup steps needed to publish this project using the GitHub Actions workflow `.github/workflows/publish.yml`.

## What the workflow expects

When you run the **publish** workflow with `dryRun=false`, it deploys to:

1. **Maven Central via Sonatype OSSRH (s01.oss.sonatype.org)**
2. **GitHub Packages**

Maven Central publishing requires **signed artifacts** (PGP/GPG).

## Prerequisites

- Install GnuPG locally (macOS):

```bash
brew install gnupg
```

- You have (or have created) a **Sonatype OSSRH account** for `https://s01.oss.sonatype.org/`.
- Your groupId (`io.github.yaravind`) is approved/verified in Sonatype.
- You have generated a **PGP key** for signing.

## PGP key creation (local)

### Generate a signing key

Run:

```bash
gpg --full-generate-key
```

Recommended selections (what we used in this setup):

- Key type: **ECC (sign only)**
- Curve: **Curve 25519**

These choices create a modern signing-capable key suitable for artifact signing.

### Find the key ID / fingerprint

List secret keys with long IDs:

```bash
gpg --list-secret-keys --keyid-format=long
```

Use the **ID from the `sec`/`pub` line** (the part after the `/`) or the full fingerprint.

### Export the private key (ASCII armored)

Export the secret key in ASCII armored form:

```bash
gpg --armor --export-secret-keys <KEYID>
```

Important:

- Copy **the entire output**, including:
  - `-----BEGIN PGP PRIVATE KEY BLOCK-----`
  - everything in between
  - `-----END PGP PRIVATE KEY BLOCK-----`

This full block becomes the GitHub secret `GPG_PRIVATE_KEY`.

## Sonatype (OSSRH) credentials

You need credentials for `s01.oss.sonatype.org`.

- If you use **Sonatype User Tokens**:
  - put the token “username” in `OSSRH_USERNAME`
  - put the token “password” in `OSSRH_PASSWORD`

## Configure GitHub repository secrets

Add these in:

**GitHub repo → Settings → Secrets and variables → Actions → New repository secret**

### Required for Maven Central

- `OSSRH_USERNAME`
- `OSSRH_PASSWORD`
- `GPG_PRIVATE_KEY` (the full armored private key block)
- `GPG_PASSPHRASE` (the passphrase you set when creating the key)

### Used for GitHub Packages

- `GITHUB_TOKEN` is provided automatically by GitHub Actions.
- The workflow requests `packages: write` permission.

## Run the publish workflow

Go to:

**Actions → publish → Run workflow**

Recommended sequence:

1. **Dry run**
   - `dryRun: true`
   - `publishVersion: X.Y.Z` (recommended to set for releases)

2. **Real publish**
   - `dryRun: false`
   - `publishVersion: X.Y.Z`

Notes:

- `publishVersion` should match the release tag version (e.g., tag `v1.2.3` → publishVersion `1.2.3`).
- The workflow will build/verify first, then deploy.

## Common failure modes

### 1) Sonatype “not authorized” / namespace not found

This usually means Sonatype hasn’t granted publishing rights for the groupId yet.

Fix: ensure the Sonatype project exists and your account has access to the `io.github.yaravind` namespace.

### 2) GPG signing failures

If you see signing failures:

- Confirm the secret `GPG_PRIVATE_KEY` includes the full BEGIN/END block.
- Confirm `GPG_PASSPHRASE` is correct.

### 3) GitHub Packages 401/403 during deploy

If GitHub Packages deploy fails, it’s usually an auth/settings issue.

- Ensure the workflow run has `packages: write` permission.
- Ensure the deployment URL matches the repository.

If you hit this, copy the Actions log snippet and we can adjust the workflow to pass credentials explicitly (if needed).

## Quick checklist

- [ ] OSSRH account works on `s01.oss.sonatype.org`
- [ ] GroupId namespace approved for publishing
- [ ] PGP key generated (ECC sign-only, Curve25519)
- [ ] Secrets set: `OSSRH_USERNAME`, `OSSRH_PASSWORD`, `GPG_PRIVATE_KEY`, `GPG_PASSPHRASE`
- [ ] Run `publish` with `dryRun=true` then `dryRun=false`
