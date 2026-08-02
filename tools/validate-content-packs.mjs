import { createHash } from "node:crypto";
import { createReadStream, existsSync, readFileSync, readdirSync } from "node:fs";
import { createInterface } from "node:readline";
import { basename, join, resolve } from "node:path";

const root = resolve(process.argv[2] ?? "content-packs/chemistrymcqs");
const config = readJson(join(root, "release.config.json"));
const sourceRoot = join(root, "source-packs");
const allowedDifficulties = new Set(["foundational", "intermediate", "advanced", "challenge"]);
const allowedCognitiveLevels = new Set([
  "recall",
  "understanding",
  "application",
  "analysis",
  "calculation",
  "data_interpretation",
  "experimental_reasoning",
  "mechanism",
  "advanced_reasoning"
]);
const globalIds = new Set();
const normalizedQuestions = new Map();
const report = {
  total: 0,
  packs: {},
  categories: {},
  concepts: {},
  difficulties: {},
  cognitiveLevels: {},
  correctPositions: { a: 0, b: 0, c: 0, d: 0 },
  media: 0
};
const errors = [];

if (!existsSync(sourceRoot)) {
  fail(`Missing source-packs directory: ${sourceRoot}`);
}

for (const expected of config.packs) {
  const packRoot = join(sourceRoot, expected.packId.replace(`${config.subject}-`, ""));
  if (!existsSync(packRoot)) {
    errors.push(`${expected.packId}: missing source directory ${packRoot}`);
    continue;
  }
  await validatePack(packRoot, expected);
}

if (report.total !== config.expectedQuestionCount) {
  errors.push(`Expected ${config.expectedQuestionCount} total questions; found ${report.total}`);
}

printReport();
if (errors.length) {
  console.error(`\nValidation failed with ${errors.length} error(s):`);
  errors.slice(0, 200).forEach((error) => console.error(`- ${error}`));
  if (errors.length > 200) console.error(`- ...and ${errors.length - 200} more`);
  process.exit(1);
}
console.log("\nContent-pack validation passed.");

async function validatePack(packRoot, expected) {
  const manifestPath = join(packRoot, "pack.json");
  const questionsPath = join(packRoot, "questions.jsonl");
  const conceptIndexPath = join(packRoot, "concept-index.json");
  if (!existsSync(manifestPath) || !existsSync(questionsPath) || !existsSync(conceptIndexPath)) {
    errors.push(`${expected.packId}: pack.json, questions.jsonl and concept-index.json are required`);
    return;
  }
  const manifest = readJson(manifestPath);
  const conceptIndex = readJson(conceptIndexPath);
  const conceptIds = new Set((conceptIndex.concepts ?? []).map((entry) =>
    typeof entry === "string" ? entry : entry.id
  ));

  check(manifest.schemaVersion === 1, `${expected.packId}: schemaVersion must be 1`);
  check(manifest.packId === expected.packId, `${expected.packId}: manifest packId mismatch`);
  check(manifest.subject === config.subject, `${expected.packId}: subject mismatch`);
  check(manifest.level === expected.level, `${expected.packId}: level mismatch`);
  check((manifest.class ?? null) === (expected.class ?? null), `${expected.packId}: class mismatch`);
  check(manifest.questionCount === expected.questionCount, `${expected.packId}: manifest count mismatch`);
  check(manifest.questionsFile === "questions.jsonl", `${expected.packId}: questionsFile must be questions.jsonl`);
  check(/^[0-9a-f]{64}$/i.test(manifest.questionsSha256), `${expected.packId}: invalid questionsSha256`);

  const actualHash = await sha256(questionsPath);
  check(
    actualHash === String(manifest.questionsSha256).toLowerCase(),
    `${expected.packId}: questionsSha256 mismatch; expected ${actualHash}`
  );

  let packCount = 0;
  const stream = createInterface({
    input: createReadStream(questionsPath, { encoding: "utf8" }),
    crlfDelay: Infinity
  });
  for await (const rawLine of stream) {
    const line = packCount === 0 ? rawLine.replace(/^\uFEFF/, "") : rawLine;
    if (!line.trim()) continue;
    let question;
    try {
      question = JSON.parse(line);
    } catch (error) {
      errors.push(`${expected.packId} line ${packCount + 1}: invalid JSON (${error.message})`);
      continue;
    }
    packCount++;
    validateQuestion(question, expected, conceptIds, packRoot);
  }
  report.packs[expected.packId] = packCount;
  report.total += packCount;
  check(packCount === expected.questionCount, `${expected.packId}: expected ${expected.questionCount}, found ${packCount}`);
}

function validateQuestion(q, expected, conceptIds, packRoot) {
  const owner = q.id || `${expected.packId}:unknown`;
  const requiredText = [
    "id", "subject", "language", "level", "category", "subcategory", "conceptId",
    "concept", "difficulty", "cognitiveLevel", "type", "question", "correctOptionId",
    "explanation", "hint", "status"
  ];
  requiredText.forEach((field) => check(
    typeof q[field] === "string" && q[field].trim(),
    `${owner}: ${field} is required`
  ));
  check(q.schemaVersion === 1, `${owner}: schemaVersion must be 1`);
  check(q.subject === config.subject, `${owner}: subject mismatch`);
  check(q.level === expected.level, `${owner}: level mismatch`);
  check((q.class ?? null) === (expected.class ?? null), `${owner}: class mismatch`);
  check(q.type === "single_choice", `${owner}: type must be single_choice`);
  check(q.status === "published", `${owner}: status must be published`);
  check(Number.isInteger(q.revision) && q.revision > 0, `${owner}: revision must be positive`);
  check(Number.isInteger(q.estimatedSeconds) && q.estimatedSeconds >= 10 && q.estimatedSeconds <= 3600,
    `${owner}: invalid estimatedSeconds`);
  check(allowedDifficulties.has(q.difficulty), `${owner}: invalid difficulty`);
  check(allowedCognitiveLevels.has(q.cognitiveLevel), `${owner}: invalid cognitiveLevel`);
  check(typeof q.question === "string" && q.question.length >= 12 && q.question.length <= 1500,
    `${owner}: question length must be 12–1500`);
  check(typeof q.explanation === "string" && q.explanation.length >= 40,
    `${owner}: explanation must contain at least 40 characters`);
  check(typeof q.hint === "string" && q.hint.length >= 8, `${owner}: hint is too short`);
  check(Array.isArray(q.options) && q.options.length === 4, `${owner}: exactly four options are required`);

  const optionIds = new Set((q.options ?? []).map((option) => option.id));
  check(optionIds.size === 4, `${owner}: option IDs must be unique`);
  check(optionIds.has(q.correctOptionId), `${owner}: correctOptionId is invalid`);
  for (const option of q.options ?? []) {
    check(typeof option.text === "string" && option.text.trim(), `${owner}: blank option ${option.id}`);
    check(
      typeof q.optionExplanations?.[option.id] === "string" &&
        q.optionExplanations[option.id].length >= 12,
      `${owner}: option ${option.id} requires an explanation`
    );
  }
  check(Array.isArray(q.relatedConcepts) && q.relatedConcepts.length >= 2,
    `${owner}: at least two relatedConcepts are required`);
  check(Array.isArray(q.syllabusReferences) && q.syllabusReferences.length >= 1,
    `${owner}: syllabusReferences are required`);
  check(Array.isArray(q.tags) && q.tags.length >= 1, `${owner}: tags are required`);
  check(conceptIds.has(q.conceptId), `${owner}: conceptId is absent from concept-index.json`);
  check(!globalIds.has(q.id), `${owner}: duplicate question ID`);
  globalIds.add(q.id);

  const normalized = normalizeQuestion(q.question);
  if (normalizedQuestions.has(normalized)) {
    errors.push(`${owner}: duplicate question text also used by ${normalizedQuestions.get(normalized)}`);
  } else {
    normalizedQuestions.set(normalized, owner);
  }

  if (q.media?.path) {
    const media = resolve(packRoot, q.media.path);
    check(media.startsWith(resolve(packRoot)) && existsSync(media), `${owner}: missing media ${q.media.path}`);
    report.media++;
  }
  increment(report.categories, q.category);
  increment(report.concepts, q.conceptId);
  increment(report.difficulties, q.difficulty);
  increment(report.cognitiveLevels, q.cognitiveLevel);
  increment(report.correctPositions, q.correctOptionId);
}

function normalizeQuestion(text) {
  return String(text)
    .toLowerCase()
    .replace(/[0-9]+(?:\.[0-9]+)?/g, "#")
    .replace(/[^\p{L}#]+/gu, " ")
    .trim();
}

function increment(target, key) {
  target[key] = (target[key] ?? 0) + 1;
}

function check(condition, message) {
  if (!condition) errors.push(message);
}

function readJson(file) {
  try {
    return JSON.parse(readFileSync(file, "utf8").replace(/^\uFEFF/, ""));
  } catch (error) {
    fail(`Cannot read ${file}: ${error.message}`);
  }
}

async function sha256(file) {
  const hash = createHash("sha256");
  for await (const chunk of createReadStream(file)) hash.update(chunk);
  return hash.digest("hex");
}

function fail(message) {
  console.error(message);
  process.exit(1);
}

function printReport() {
  console.log(`Subject: ${config.subject}`);
  console.log(`Source: ${basename(root)}`);
  console.log(`Questions: ${report.total}/${config.expectedQuestionCount}`);
  console.log("Packs:", report.packs);
  console.log("Difficulties:", report.difficulties);
  console.log("Cognitive levels:", report.cognitiveLevels);
  console.log("Correct positions:", report.correctPositions);
  console.log(`Categories: ${Object.keys(report.categories).length}`);
  console.log(`Concepts: ${Object.keys(report.concepts).length}`);
  console.log(`Questions with media: ${report.media}`);
}
