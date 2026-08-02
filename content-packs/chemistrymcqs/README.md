# Chemistry MCQ content release

This directory is the publisher-side source for the downloadable MCQ packs. Questions are not compiled into the Android application.

## Production URLs

- Catalog: `https://www.indianservers.com/edtech/chemistrymcqs/catalog.json`
- Taxonomy: `https://www.indianservers.com/edtech/chemistrymcqs/taxonomy.json`
- Selective packs: `https://www.indianservers.com/edtech/chemistrymcqs/packs/<pack-file>.zip`
- Complete bundle: `https://www.indianservers.com/edtech/chemistrymcqs.zip`

## Source layout

```text
content-packs/chemistrymcqs/
├── release.config.json
├── taxonomy.json
├── templates/
│   ├── pack.json
│   └── question.json
└── source-packs/
    ├── class-06-en/
    │   ├── pack.json
    │   ├── questions.jsonl
    │   ├── concept-index.json
    │   └── assets/
    └── ...
```

The canonical question schema is:

`app/src/main/assets/contentpack/question-schema-v1.json`

Copy that file to the public website as:

`/edtech/chemistrymcqs/schema/question-schema-v1.json`

## Validation and release commands

From the workspace root:

```powershell
node tools\validate-content-packs.mjs content-packs\chemistrymcqs
node tools\build-content-packs.mjs content-packs\chemistrymcqs
```

The validator intentionally fails until all nine declared packs exist and contain exactly 3,600 valid questions. The builder requires a JDK `jar` command, calculates all hashes and sizes, invokes the validator, creates the nine versioned ZIP files, creates `chemistrymcqs.zip`, generates `catalog.json`, and writes `dist/release-report.json`.

Upload the contents of `dist/public/chemistrymcqs/` to `/edtech/chemistrymcqs/`, then upload `dist/chemistrymcqs.zip` to `/edtech/chemistrymcqs.zip`.

## Required Chemistry packs

| Pack ID | Questions |
|---|---:|
| `chemistry-class-06-en` | 150 |
| `chemistry-class-07-en` | 150 |
| `chemistry-class-08-en` | 150 |
| `chemistry-class-09-en` | 300 |
| `chemistry-class-10-en` | 400 |
| `chemistry-class-11-en` | 650 |
| `chemistry-class-12-en` | 750 |
| `chemistry-undergraduate-en` | 650 |
| `chemistry-postgraduate-en` | 400 |
| **Total** | **3,600** |

## Release rules

1. Store one question object per line in `questions.jsonl`.
2. Use stable question, category, subcategory and concept IDs.
3. Do not rename an ID when correcting content; increment its `revision`.
4. Increment the pack `version` whenever any included question or asset changes.
5. Generate `questionsSha256` after the final JSONL file is written.
6. ZIP the contents of the pack directory, not the directory itself. `pack.json` must be at the ZIP root.
7. Calculate the final ZIP SHA-256 and exact byte size.
8. Put those values in `catalog.json`.
9. Upload ZIP files first, verify them, and upload `catalog.json` last.
10. Never publish placeholder hashes or a catalog that points to incomplete packs.

## Web-server headers

Recommended headers:

```text
catalog.json:
  Content-Type: application/json
  Cache-Control: public, max-age=300

taxonomy.json:
  Content-Type: application/json
  Cache-Control: public, max-age=3600

*.zip:
  Content-Type: application/zip
  Cache-Control: public, max-age=31536000, immutable
  Accept-Ranges: bytes
```

Pack filenames are versioned and therefore immutable. A corrected pack receives a new filename and catalog version.

## App import behavior

The Android app:

1. Downloads only `catalog.json` initially.
2. Downloads a selected class or level pack through WorkManager.
3. Confirms HTTPS host, expected byte count and ZIP SHA-256.
4. Extracts into a private staging directory with traversal and size protection.
5. Validates `pack.json` and every JSONL question.
6. Confirms the questions-file SHA-256 and declared question count.
7. Imports into Room inside one transaction.
8. Deletes the ZIP and extracted JSON after import.
9. Keeps only optional media assets and normalized Room records.

The same runtime can serve Physics by changing the subject, catalog URL and allow-listed host configuration.
