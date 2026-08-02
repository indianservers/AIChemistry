# Source packs

Create one directory for every entry in `release.config.json`:

```text
class-06-en/
class-07-en/
class-08-en/
class-09-en/
class-10-en/
class-11-en/
class-12-en/
undergraduate-en/
postgraduate-en/
```

Each directory must contain:

```text
pack.json
questions.jsonl
concept-index.json
assets/
```

Start from the files under `../templates/`. The release builder replaces `questionsSha256`, validates all content and produces uploadable ZIP files.
