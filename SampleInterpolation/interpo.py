# -*- coding:utf-8 -*-

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

"""
@brief: 获得分段二次插值函数
@param: x       插值节点的横坐标集合
@param: fx      插值节点的纵坐标集合 
@return: 参数所指定的插值节点集合对应的插值函数
"""
def get_sub_two_interpolation_func(x=[], fx=[]):
    def sub_two_interpolation_func(Lx):
        result = 0
        for index in range(len(x) - 2):
            print Lx,x[index],x[index + 2]
            # if Lx >= x[index] and Lx <= x[index + 2]: # 看当前的插值的位置在哪个区间 就用哪个区间的函数
            result = fx[index] * (Lx - x[index + 1]) * (Lx - x[index + 2]) / (x[index] - x[index + 1]) / (
            x[index] - x[index + 2]) + \
                     fx[index + 1] * (Lx - x[index]) * (Lx - x[index + 2]) / (x[index + 1] - x[index]) / (
                     x[index + 1] - x[index + 2]) + \
                     fx[index + 2] * (Lx - x[index]) * (Lx - x[index + 1]) / (x[index + 2] - x[index]) / (
                     x[index + 2] - x[index + 1])
        return result

    return sub_two_interpolation_func


if __name__ == '__main__':
    ''' 插值节点, 这里用二次函数生成插值节点，每两个节点x轴距离位10 '''
    # sr_x = [i for i in range(-50, 51, 10)]
    # sr_fx = [i ** 2 for i in sr_x]
    sr_x = [ 420.0, 460.0, 260.0]
    sr_fx =[ 1280-700.0, 1280-540.0, 1280-540.0] # 左下角为原点


    Lx = get_sub_two_interpolation_func(sr_x, sr_fx)  # 获得插值函数
    tmp_x = [i for i in range(260, 460)]  # 测试用例
    tmp_y = [Lx(i) for i in tmp_x]  # 根据插值函数获得测试用例的纵坐标

    ''' 画图 '''
    import matplotlib.pyplot as plt

    plt.figure("play")
    ax1 = plt.subplot(111)
    plt.sca(ax1)
    plt.plot(sr_x, sr_fx, linestyle=' ', marker='o', color='b')
    plt.plot(tmp_x, tmp_y, linestyle='--', color='r')
    plt.show()