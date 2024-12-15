package com.qiu.qiuxun.utils;

import java.util.List;
import java.util.Objects;

/**
 * @description: 算法工具类
 * @className: AlgorithmUtils.java
 * @author: qiu
 * @createTime: 2024/3/10 19:59
 */
public class AlgorithmUtils {

    /**
     * @description: 编辑距离算法
     * @params: [tags1, tags2]
     * @return: int
     * @author: qiu
     * @dateTime: 2024/3/10 20:08
     * url：<a href="https://blog.csdn.net/tianjindong0804/article/details/115803158">详细解释</a>
     */
    public static int minDistance(List<String> tags1, List<String> tags2) {
        if (tags1 == null || tags2 == null) {
            throw new RuntimeException("参数不能为空");
        }
        int[][] dp = new int[tags1.size() + 1][tags2.size() + 1];
        //初始化DP数组
        for (int i = 0; i <= tags1.size(); i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i <= tags2.size(); i++) {
            dp[0][i] = i;
        }
        int cost;
        for (int i = 1; i <= tags1.size(); i++) {
            for (int j = 1; j <= tags2.size(); j++) {
                if (Objects.equals(tags1.get(i - 1), tags2.get(j - 1))) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                dp[i][j] = Math.min(dp[i - 1][j] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost));
            }
        }
        return dp[tags1.size()][tags2.size()];
    }

}
