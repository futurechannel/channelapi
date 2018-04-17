package com.channel.api.util;

/**
 * Created by gq on 2018/4/17.
 */
public class NumUtils {

    public static int randBoolean(int start, int end, int per) {

        int num = (int) (Math.random() * end) + start;
        if (num <= per) {
            return 1;
        }

        return 0;
    }

    public static int rand(int start,int end){
        return (int) (Math.random() * end) + start;
    }

    public static void main(String[] args) {
        for(int i=0;i<10000;i++){
            int j=rand(1,100);
            if(j<1||j>100){
                System.out.println("====");
            }
            //System.out.println(rand(1,100));
        }
//        int j=0;
//        for(int i=1;i<=10000;i++){
//            if(i%2==randBoolean(1,100,50)){
//                j++;
//            }
//        }
//
//        System.out.println(j);
    }


}
