import urllib.request,json,time
from pathlib import Path
cids=[962,280,297,222,1118,944,5234,702,176,180,241,1140,996,6115,1049,8078,5793,5984,750,2519,2244,3672,1983,5997,5957,243]
names=['Water','Carbon dioxide','Methane','Ammonia','Sulfuric acid','Nitric acid','Sodium chloride','Ethanol','Acetic acid','Acetone','Benzene','Toluene','Phenol','Aniline','Pyridine','Cyclohexane','Glucose','Fructose','Glycine','Caffeine','Aspirin','Ibuprofen','Paracetamol','Cholesterol','ATP','Benzoic acid']
root=Path('app/src/main/assets/drawchemistry'); records=[]
def get(path):
 for attempt in range(3):
  try:
   with urllib.request.urlopen('https://pubchem.ncbi.nlm.nih.gov/rest/pug/'+path,timeout=45) as r:return json.load(r)
  except Exception as e:
   if attempt==2: raise
   time.sleep(2+attempt)
for cid,name in zip(cids,names):
 p=root/f'{cid}.json'
 if p.exists():records.append({'cid':cid,'name':name});continue
 try:
  two=get(f'compound/cid/{cid}/JSON?record_type=2d')['PC_Compounds'][0];time.sleep(.3)
  try:three=get(f'compound/cid/{cid}/JSON?record_type=3d')['PC_Compounds'][0]
  except Exception:three=None
  p.write_text(json.dumps({'name':name,'cid':cid,'retrieved':'2026-10-18','source':'PubChem PUG REST','two':two,'three':three}),encoding='utf-8')
  records.append({'cid':cid,'name':name}); print(name, bool(three),flush=True);time.sleep(.3)
 except Exception as e:print(name,str(e),flush=True)
(root/'library.json').write_text(json.dumps(records),encoding='utf-8')
