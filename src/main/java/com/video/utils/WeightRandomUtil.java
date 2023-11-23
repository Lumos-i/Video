package com.video.utils;

import com.video.pojo.Advertising;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author zrq
 * @ClassName Test
 * @date 2022/11/5 18:54
 * @Description TODO
 */
@Component
public class WeightRandomUtil {
    private List<Advertising> options;
    private double[] cumulativeProbabilities;
    private Random rnd;

    public WeightRandomUtil(List<Advertising> options) {
        this.options = options;
        this.rnd = new Random();
        prepare();
    }

    /**
     * prepare()方法计算每个选项的累计概率，保存在数组cumulativeProbabilities中
     */
    private void prepare() {
        int weights = 0;
        for (Advertising advertising : options) {
            weights += advertising.getWeight();
        }
        cumulativeProbabilities = new double[options.size()];
        int sum = 0;
        for (int i = 0; i < options.size(); i++) {
            sum += options.get(i).getWeight();
            cumulativeProbabilities[i] = sum / (double) weights;
        }
    }

    /**
     * nextItem()方法根据权重随机选择一个，具体就是，首先生成一个0～1的数，
     * 然后使用二分查找，如果没找到，返回结果是-（插入点）-1，所以-index-1就是插入点，插入点的位置就对应选项的索引。
     *
     * @return
     */
    public Integer nextItem() {
        double randomValue = rnd.nextDouble();
        int index = Arrays.binarySearch(cumulativeProbabilities, randomValue);
        if (index < 0) {
            index = -index - 1;
        }
        return options.get(index).getId();
    }
}
