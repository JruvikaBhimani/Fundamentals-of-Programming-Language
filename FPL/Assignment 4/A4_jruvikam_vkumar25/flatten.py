#Problem 3

l = [[[1], 2],[[[[[3]]]]],[4,[5],[[6]]]]
def flatten(super_list):
    for sub_list in super_list:
        if isinstance(sub_list,list): 
            for sub_sub_list in flatten(sub_list):
                yield sub_sub_list
        else:
            yield sub_list

ans = list(x for x in flatten(l))
print("Result : ", ans)
