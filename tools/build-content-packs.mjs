import { createHash } from "node:crypto";
import {
  copyFileSync,
  existsSync,
  mkdirSync,
  readFileSync,
  rmSync,
  statSync,
  writeFileSync
} from "node:fs";
import { spawnSync } from "node:child_process";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(process.argv[2] ?? "content-packs/chemistrymcqs");
const workspace = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const config = readJson(join(root, "release.config.json"));
const sourceRoot = join(root, "source-packs");
const distRoot = join(root, "dist");
const publicRoot = join(distRoot, "public", "chemistrymcqs");
const packOutput = join(publicRoot, "packs");
const schemaSource = join(workspace, "app", "src", "main", "assets", "contentpack", "question-schema-v1.json");
const validatorScript = join(workspace, "tools", "validate-content-packs.mjs");

safeRemove(distRoot, root);
mkdirSync(packOutput, { recursive: true });

for (const expected of config.packs) {
  const sourceName = expected.packId.replace(`${config.subject}-`, "");
  const packRoot = join(sourceRoot, sourceName);
  const manifestPath = join(packRoot, "pack.json");
  const questionsPath = join(packRoot, "questions.jsonl");
  requireFile(manifestPath);
  requireFile(questionsPath);
  const manifest = readJson(manifestPath);
  manifest.questionsSha256 = sha256File(questionsPath);
  writeJson(manifestPath, manifest);
}

run(process.execPath, [validatorScript, root]);

const catalogPacks = [];
for (const expected of config.packs) {
  const sourceName = expected.packId.replace(`${config.subject}-`, "");
  const packRoot = join(sourceRoot, sourceName);
  const manifest = readJson(join(packRoot, "pack.json"));
  const fileName = `${sourceName}-v${manifest.version}.zip`;
  const zipPath = join(packOutput, fileName);
  run("jar", ["--create", "--file", zipPath, "-C", packRoot, "."]);
  const zipStat = statSync(zipPath);
  catalogPacks.push({
    packId: manifest.packId,
    title: manifest.title,
    subject: manifest.subject,
    level: manifest.level,
    class: manifest.class ?? null,
    language: manifest.language,
    version: manifest.version,
    questionCount: manifest.questionCount,
    downloadUrl: `${config.packBaseUrl}${fileName}`,
    downloadBytes: zipStat.size,
    sha256: sha256File(zipPath)
  });
}

copyFileSync(join(root, "taxonomy.json"), join(publicRoot, "taxonomy.json"));
mkdirSync(join(publicRoot, "schema"), { recursive: true });
copyFileSync(schemaSource, join(publicRoot, "schema", "question-schema-v1.json"));

const catalog = {
  catalogVersion: 1,
  subject: config.subject,
  updatedAt: new Date().toISOString(),
  totalQuestions: catalogPacks.reduce((total, pack) => total + pack.questionCount, 0),
  packs: catalogPacks
};
writeJson(join(publicRoot, "catalog.json"), catalog);

const completeBundle = join(distRoot, `${config.subject}mcqs.zip`);
run("jar", ["--create", "--file", completeBundle, "-C", join(distRoot, "public"), "."]);

const release = {
  generatedAt: new Date().toISOString(),
  catalog: join(publicRoot, "catalog.json"),
  completeBundle,
  completeBundleBytes: statSync(completeBundle).size,
  completeBundleSha256: sha256File(completeBundle),
  packs: catalogPacks.map((pack) => ({
    packId: pack.packId,
    url: pack.downloadUrl,
    bytes: pack.downloadBytes,
    sha256: pack.sha256
  }))
};
writeJson(join(distRoot, "release-report.json"), release);
console.log(JSON.stringify(release, null, 2));

function readJson(file) {
  return JSON.parse(readFileSync(file, "utf8").replace(/^\uFEFF/, ""));
}

function writeJson(file, value) {
  mkdirSync(dirname(file), { recursive: true });
  writeFileSync(file, `${JSON.stringify(value, null, 2)}\n`, "utf8");
}

function sha256File(file) {
  return createHash("sha256").update(readFileSync(file)).digest("hex");
}

function requireFile(file) {
  if (!existsSync(file) || !statSync(file).isFile()) {
    throw new Error(`Missing required file: ${file}`);
  }
}

function run(command, args) {
  const result = spawnSync(command, args, { stdio: "inherit", shell: false });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error(`${command} exited with code ${result.status}`);
}

function safeRemove(target, allowedRoot) {
  const resolvedTarget = resolve(target);
  const resolvedRoot = resolve(allowedRoot);
  if (!resolvedTarget.startsWith(`${resolvedRoot}\\`) && !resolvedTarget.startsWith(`${resolvedRoot}/`)) {
    throw new Error(`Refusing to remove directory outside ${resolvedRoot}`);
  }
  rmSync(resolvedTarget, { recursive: true, force: true });
}
