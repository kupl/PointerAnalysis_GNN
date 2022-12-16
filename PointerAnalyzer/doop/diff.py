



s1 = set()
with open("sig.facts") as file:
  for line in file.readlines():
    line = line.strip()
    s1.add(line)

s2 = set()
with open("sig1.facts") as file:
  for line in file.readlines():
    line = line.strip()
    s2.add(line)



#print(len(s2-s1))
for i, val in enumerate(s1-s2): 
  print(val)




