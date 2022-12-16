


my_list = []

with open("sig.facts") as file:
  for line in file.readlines():
    edge = line.strip().split(', ')
    print(edge)
    ctx = edge[0]
    cnt = int(edge[1])
    my_list.append((ctx, int(cnt)))

my_list.sort(key=lambda x: x[1])




for i, val in enumerate(my_list):
  print(val)



