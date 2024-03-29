# 将数据转换为每个节点的关系列表
from collections import defaultdict
import sys


input_file = sys.argv[1]+'.txt'
max_element_size = 999999999999999
power = 0
if len(sys.argv) > 2:
    power = int(sys.argv[2])
    max_element_size = pow(2,power)

# 使用defaultdict初始化一个字典，每个键的默认值是一个空列表
relations = defaultdict(list)

element_size = 0


# 读取文件并填充relations字典
with open(input_file, 'r') as f:
    for line in f:
        node1, node2 = line.strip().split()
        relations[node1].append(node2)
        element_size += 1
        if element_size >= max_element_size:
            break
        

# 将字典转换为一个列表，每个元素是一个元组(node, [related_nodes])
relations_list = [(node, nodes) for node, nodes in relations.items()]


# 按照相关节点列表的长度排序，从高到低
sorted_relations = sorted(relations_list, key=lambda x: len(x[1]), reverse=True)
    

# 准备写入到文件
if power == 0:
    output = sys.argv[1] + '-all.txt'
else:
    output = sys.argv[1] + '-' + str(power) + '.txt'
with open(output, 'w') as f:
    for node, nodes in sorted_relations:
        # 写入节点、相关节点列表长度和相关节点列表
        f.write(f"{node} {len(nodes)}\n")

# 输出文件路径以确认写入成功
print(output)