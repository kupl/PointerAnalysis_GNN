import os




def choose_mid(feature_method_map, methods):
  my_list = []
  for i, val in enumerate(feature_method_map):
    my_list.append((i, len(feature_method_map[i])))
  my_list.sort(key=lambda x: x[1])
  mid_idx = my_list[len(feature_method_map)/2][0]
  return mid_idx



def get_method_of_features(feat_idx):
  query = "bloxbatch -db doop/last-analysis -query RMFeature{} | sort > methods.facts".format(feat_idx)
  os.system(query)
  methods = set()
  with open("methods.facts") as file:
    for line in file.readlines():
      method = line.strip()
      methods.add(method)
  print("Feature{} : {}".format(feat_idx,len(methods))) 
  return methods

def learn(): 
  pgms = ['luindex']
  features_len = 20
  feature_method_map = {}
  #run_analyis('ci')
  for feat_idx in range(features_len):
    feature_method_map[feat_idx] = get_method_of_features(feat_idx)
  all_methods = feature_method_map[0] | feature_method_map[1]
  idx = choose_mid(feature_method_map, all_methods)
  print(idx)
  #query = "bloxbatch -db doop/last-analysis -query RMFeature{} | sort > methods.facts".format(0)
  #os.system(query)

  

if __name__ == '__main__':
  learn()

