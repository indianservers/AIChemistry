"""Build the explicit runtime lesson catalog from editorial sources, aliases and case records.
No category or keyword fallback is allowed: an unresolved lesson fails the build script.
"""
import json,re,collections
from pathlib import Path
ROOT=Path(__file__).resolve().parent.parent
base=ROOT/'tools'
rows=json.loads((base/'lesson-editorial-seed.json').read_text(encoding='utf-8'))
sources=json.loads((base/'lesson-editorial-source-library.json').read_text(encoding='utf-8'))
def norm(value):return re.sub('[^a-z0-9]','',value.lower().replace('isation','ization').replace('behaviour','behavior'))
def read_notes(name):
 return {norm(line.split('|',1)[0]):line.split('|',1)[1] for line in (base/name).read_text(encoding='utf-8-sig').splitlines() if line.strip()}
applications=read_notes('lesson-editorial-applications.txt')
corrections=read_notes('lesson-editorial-corrections.txt')
for key,source in sources.items():
 topic=norm(key.split('|')[-1])
 if topic in applications:source['application']=applications[topic]
 if topic in corrections:source['mistake']=corrections[topic]
index={}
for key,source in sources.items():index.setdefault(norm(key.split('|')[-1]),[]).append((key,source))
aliases={line.split('|')[0]:line.split('|')[1].split(';') for line in (base/'lesson-editorial-aliases.txt').read_text(encoding='utf-8-sig').splitlines() if line.strip()}
cases={}
for line in (base/'lesson-editorial-cases.txt').read_text(encoding='utf-8-sig').splitlines():
 if not line.strip():continue
 fields=line.split('|');assert len(fields)==7,(fields[0],len(fields))
 title,summary,explanation,example,relationship,mistake,application=fields
 cases[norm(title)]=dict(summary=summary,explanation=explanation,example=example,relationship=relationship,mistake=mistake,application=application,connections=[])
def lookup(title,module):
 if norm(title) in cases:return cases[norm(title)]
 choices=index.get(norm(title),[])
 return next((s for k,s in choices if module in k),choices[0][1] if choices else None)
def clean(text):
 text=re.sub(r' Learning goal:.*','',text)
 text=re.sub(r' This illustrates the key relationship;.*','',text)
 text=re.sub(r' Check the definition, particles and evidence before deciding\.?','',text)
 text=re.sub(r' Interpret the result using the stated model and verify it against an independent observation\.?','',text)
 return text.strip()
def sentences(text):
 return [s.strip() for s in re.split(r'(?<=[.!?])\s+(?=[A-Z])|;\s+',text) if s.strip()]
def unique(items):return list(dict.fromkeys(x for x in items if x))
records=[]
for row in rows:
 title,module=row['title'],row['module']
 source=cases.get(norm(title))
 origin='new case' if source else 'existing authored record'
 if not source and title=='Core principles and applications':source=sources.get(module)
 if not source:source=row.get('authored') or lookup(title,module)
 selected=[]
 if not source:
  selected=[lookup(name,module) for name in aliases.get(title,[])]
  selected=[s for s in selected if s]
  if selected:
   source={field:'\n\n'.join(unique(clean(s[field]) for s in selected)) for field in ['summary','explanation','example','relationship','mistake','application']}
   source['connections']=unique(name for s in selected for name in s.get('connections',[]))
   origin='explicit topic alias'
 if not source:raise ValueError('Missing lesson-specific record: '+module+' / '+title)
 source={k:clean(v) if isinstance(v,str) else v for k,v in source.items()}
 if norm(title) not in cases:
  if norm(title) in applications:source['application']=applications[norm(title)]
  if norm(title) in corrections:source['mistake']=corrections[norm(title)]
 # The academic catalog previously appended these two generic paragraphs to every concept.
 if source['explanation'].startswith('At undergraduate level') or source['explanation'].startswith('At postgraduate level'):
  source['explanation']=''
 example=re.sub(r'^Example:\s*','',source['example'])
 summary=source['summary']
 if title=='Core principles and applications': summary=summary.replace(title,module)
 relationship=source['relationship']
 explanation='\n\n'.join(part for part in source['explanation'].split('\n\n') if not part.startswith(('At undergraduate level','At postgraduate level')))
 if not explanation or explanation.lower() in summary.lower():explanation=example
 mistake=source['mistake']
 if not mistake or 'as a label or a result from one instrument alone' in mistake:
  # Present a concrete interpretation rather than fabricate a misconception not in the source.
  mistake='Correct interpretation: '+summary+' '+example
 application=source['application'] or example
 if 'Used according to' in application or 'central theories' in application:application=example
 steps=unique(sentences(example)+[relationship]+sentences(explanation)+[mistake])[:6]
 if len(steps)<4:steps=unique(steps+[summary,application])
 connections=unique(source.get('connections',[])+[n for n in aliases.get(title,[]) if n!=title])
 connections=[c for c in connections if c not in ['Quantitative modelling and validation','Experimental evidence and uncertainty','Evidence and uncertainty','Molecular-level mechanism','Advanced chemical applications']]
 if not connections:connections=[relationship,summary]
 key='|'.join([row['level'],module,title])
 records.append(dict(key=key,origin=origin,summary=summary,explanation=explanation,example=example,relationship=relationship,mistake=mistake,application=application,steps=steps,connections=connections))
# Every lesson is an explicit entry; the app never guesses content from category keywords.
def q(text):return json.dumps(text,ensure_ascii=False).replace('$','\\$')
def ktlist(items):return 'listOf('+','.join(q(v) for v in items)+')'
out=ROOT/'app/src/main/java/com/indianservers/aichemistryexplorer/editorial'
out.mkdir(exist_ok=True)
parts=[]
for start in range(0,len(records),20):
 number=start//20;name=f'editorialPart{number:02d}';parts.append(name)
 lines=['package com.indianservers.aichemistryexplorer','', '// Generated by tools/rebuild-lesson-editorial.py from reviewed source and case files.',f'internal fun {name}(): List<EditorialLessonRecord> = listOf(']
 for r in records[start:start+20]:
  values=[q(r[k]) for k in ['key','origin','summary','explanation','example','relationship','mistake','application']]+[ktlist(r['steps']),ktlist(r['connections'])]
  lines.append('    EditorialLessonRecord('+',\n        '.join(values)+'),')
 lines.append(')')
 (out/f'LessonEditorialPart{number:02d}.kt').write_text('\n'.join(lines)+'\n',encoding='utf-8')
(out/'LessonEditorialIndex.kt').write_text('package com.indianservers.aichemistryexplorer\n\ninternal val editorialLessonCatalog by lazy {\n    ('+' +\n        '.join(n+'()' for n in parts)+').associateBy { it.key }\n}\n',encoding='utf-8')
report=ROOT/'outputs/lesson-content-editorial-catalog.json'
report.write_text(json.dumps(records,ensure_ascii=False,indent=2),encoding='utf-8')
print('Built',len(records),'explicit lesson records;',dict(collections.Counter(r['origin'] for r in records)))
