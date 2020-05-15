package com.fangtan.hourse.config;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 数学算法（数学算法（方差、标准差、中位数、众数））
 * @author
 *
 */
public class MathAlgorithm {
    private final static double dmax = 999;// Double.MAX_VALUE;//Double类型的最大值，太大的double值，相乘会达到无穷大
    private final static double dmin = Double.MIN_VALUE;// Double类型的最小值
    private final static int n = 100;// 假设求取100个doubl数的方差和标准差

//    public static void main(String[] args) {
//        Random random = new Random();
//        double[] x = new double[n];
//        for (int i = 0; i < n; i++) {// 随机生成n个double数
//            x[i] = Double.valueOf(Math.floor(random.nextDouble() * (dmax - dmin)));
//            System.out.println(x[i]);
//        }
//        // 设置doubl字符串输出格式，不以科学计数法输出
//        DecimalFormat df = new DecimalFormat("#,##0.00");// 格式化设置
//        // 计算方差
//        double dV = getVariance(x);
//        System.out.println("方差=" + df.format(dV));
//        // 计算标准差
//        double dS = getStandardDiviation(x);
//        System.out.println("标准差=" + df.format(dS));
//
//
//        int[] intArr={5,10,15,8,6};
//        System.out.println(Arrays.toString(intArr)+" 中位数:"+median(intArr));
//
//        int[] intArr2={5,10,15,8,6,7};
//        System.out.println(Arrays.toString(intArr2)+" 中位数:"+median(intArr2));
//
//        int[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 1, 2, 2, 3, 4, 5};
//
//        List<Integer> modalNums = getModalNums(arr);
//        System.out.println("众数："+modalNums);
//
//        float[] arr2 = {0.1f, 1.1f, 2.1f, 3.1f, 4.1f, 5.1f, 6.1f, 7.1f, 8.1f, 9.1f, 10.1f, 1.1f, 1.1f, 2.1f, 2.1f, 3.1f, 4.1f, 5.1f};
//
//        List<Float> modalNums2 = getModalNums(arr2);
//
//        System.out.println("众数："+modalNums2);
//    }

    /**
     * 方差s^2=[(x1-x)^2 +...(xn-x)^2]/n
     * @param x
     * @return
     */
    public static double getVariance(double[] x) {
        int m = x.length;
        double sum = 0;
        for (int i = 0; i < m; i++) {// 求和
            sum += x[i];
        }
        double dAve = sum / m;// 求平均值
        double dVar = 0;
        for (int i = 0; i < m; i++) {// 求方差
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return dVar / m;
    }

    /**
     * 标准差σ=sqrt(s^2)
     * @param x
     * @return
     */
    public static double getStandardDiviation(double[] x) {
        int m = x.length;
        double sum = 0;
        for (int i = 0; i < m; i++) {// 求和
            sum += x[i];
        }
        double dAve = sum / m;// 求平均值
        double dVar = 0;
        for (int i = 0; i < m; i++) {// 求方差
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return Math.sqrt(dVar / m);
    }

    /**
     * 中位数(int)
     * @param nums: A list of integers.
     * @return: An integer denotes the middle number of the array.
     */
    public static int median(int []nums){
        if(nums.length==0)
            return 0;
        int start=0;
        int end=nums.length-1;
        int index=partition(nums, start, end);
        if(nums.length%2==0){
            while(index!=nums.length/2-1){
                if(index>nums.length/2-1){
                    index=partition(nums, start, index-1);
                }else{
                    index=partition(nums, index+1, end);
                }
            }
        }else{
            while(index!=nums.length/2){
                if(index>nums.length/2){
                    index=partition(nums, start, index-1);
                }else{
                    index=partition(nums, index+1, end);
                }
            }
        }
        return nums[index];
    }

    private static int partition(int nums[], int start, int end){
        int left=start;
        int right=end;
        int pivot=nums[left];
        while(left<right){
            while(left<right&&nums[right]>=pivot){
                right--;
            }
            if(left<right){
                nums[left]=nums[right];
                left++;
            }
            while(left<right&&nums[left]<=pivot){
                left++;
            }
            if(left<right){
                nums[right]=nums[left];
                right--;
            }
        }
        nums[left]=pivot;
        return left;
    }

    /**
     * 中位数(float)
     * @param nums : A list of integers.
     * @return: An integer denotes the middle number of the array.
     */
    public static Integer median(Integer[] nums){
        if(nums.length==0)
            return 0;
        int start=0;
        int end=nums.length-1;
        int index=partition(nums, start, end);
        if(nums.length%2==0){
            while(index!=nums.length/2-1){
                if(index>nums.length/2-1){
                    index=partition(nums, start, index-1);
                }else{
                    index=partition(nums, index+1, end);
                }
            }
        }else{
            while(index!=nums.length/2){
                if(index>nums.length/2){
                    index=partition(nums, start, index-1);
                }else{
                    index=partition(nums, index+1, end);
                }
            }
        }
        return nums[index];
    }

    private static int partition(Integer nums[], int start, int end){
        int left=start;
        int right=end;
        Integer pivot=nums[left];
        while(left<right){
            while(left<right&&nums[right]>=pivot){
                right--;
            }
            if(left<right){
                nums[left]=nums[right];
                left++;
            }
            while(left<right&&nums[left]<=pivot){
                left++;
            }
            if(left<right){
                nums[right]=nums[left];
                right--;
            }
        }
        nums[left]=pivot;
        return left;
    }

    /**
     * 众数(int)
     * 众数:在一个数组中出现次数最多的数
     * 如果存在多个众数，则一起返回
     * @param arr
     * @return
     */
    public static List<Integer> getModalNums(int[] arr) {
        int n = arr.length;

        if (n == 0) {
            return new ArrayList<Integer>();
        }

        if (n == 1) {
            return Arrays.asList(arr[0]);
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int i = 0; i < n; i++) { // 统计数组中每个数出现的频率
            Integer v = freqMap.get(arr[i]);
            // v == null 说明 freqMap 中还没有这个 arr[i] 这个键
            freqMap.put(arr[i], v == null ? 1 : v + 1);
        }

        // 将 freqMap 中所有的键值对（键为数，值为数出现的频率）放入一个 ArrayList
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freqMap.entrySet());
        // 对 entries 按出现频率从大到小排序
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> e1, Map.Entry<Integer, Integer> e2) {
                return e2.getValue() - e1.getValue();
            }
        });

        List<Integer> modalNums = new ArrayList<>();
        modalNums.add(entries.get(0).getKey()); // 排序后第一个 entry 的键肯定是一个众数

        int size = entries.size();
        for (int i = 1; i < size; i++) {
            // 如果之后的 entry 与第一个 entry 的 value 相等，那么这个 entry 的键也是众数
            if (entries.get(i).getValue().equals(entries.get(0).getValue())) {
                modalNums.add(entries.get(i).getKey());
            } else {
                break;
            }
        }

        return modalNums;
    }

    /**
     * 众数(float)
     * 众数:在一个数组中出现次数最多的数
     * 如果存在多个众数，则一起返回
     * @param arr
     * @return
     */
    public static List<Integer> getModalNums(Integer[] arr) {
        int n = arr.length;

        if (n == 0) {
            return new ArrayList<Integer>();
        }

        if (n == 1) {
            return Arrays.asList(arr[0]);
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int i = 0; i < n; i++) { // 统计数组中每个数出现的频率
            Integer v = freqMap.get(arr[i]);
            // v == null 说明 freqMap 中还没有这个 arr[i] 这个键
            freqMap.put(arr[i], v == null ? 1 : v + 1);
        }

        // 将 freqMap 中所有的键值对（键为数，值为数出现的频率）放入一个 ArrayList
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freqMap.entrySet());
        // 对 entries 按出现频率从大到小排序
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> e1, Map.Entry<Integer, Integer> e2) {
                return e2.getValue() - e1.getValue();
            }
        });

        List<Integer> modalNums = new ArrayList<>();
        modalNums.add(entries.get(0).getKey()); // 排序后第一个 entry 的键肯定是一个众数

        int size = entries.size();
        for (int i = 1; i < size; i++) {
            // 如果之后的 entry 与第一个 entry 的 value 相等，那么这个 entry 的键也是众数
            if (entries.get(i).getValue().equals(entries.get(0).getValue())) {
                modalNums.add(entries.get(i).getKey());
            } else {
                break;
            }
        }

        return modalNums;
    }
}