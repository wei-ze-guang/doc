package suanfa;

import java.util.Arrays;

/**
 * 这里是练习算法的
 */
public class Main {
    public static void main(String[] args) throws Exception {
        int[] arr = new int[]{1,2,3,4,5,6,7,8,9};
        int target = 15;
        for(int i = 0 ; i < arr.length ; i++){
            for(int j = i+1 ; j < arr.length ; j++){
                if(arr[i] + arr[j] == target){
                    System.out.println(Arrays.toString(new int[]{arr[i], arr[j]}));
                }
            }
        }
    }
}
