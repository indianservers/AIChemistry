import urllib.request,json,time
from pathlib import Path
for p in Path('app/src/main/assets/drawchemistry').glob('[0-9]*.json'):
 d=json.loads(p.read_text())
 if 'mol' not in d:
  try:d['mol']=urllib.request.urlopen(f'https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/{d["cid"]}/SDF?record_type=2d',timeout=30).read().decode();p.write_text(json.dumps(d),encoding='utf-8')
  except Exception as e:print(e)
  time.sleep(.3)
