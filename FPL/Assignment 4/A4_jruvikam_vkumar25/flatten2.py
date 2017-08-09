#Problem 4

l = [[[1], 2],[[[[[3]]]]],[4,[5],[[6]]],[[[[[[[[[[7]]]]]]]]]]]

def thunk(i):
    return [i]

def flatten2(super_list,thk):
    new_list = []
    for sub_list in super_list:
        if isinstance(sub_list, list):
            new_list.extend(flatten2(sub_list,thk))
        else:
            new_list.extend(thk(sub_list))
    return new_list

result = flatten2(l,thunk)
print("Result : ", result)
