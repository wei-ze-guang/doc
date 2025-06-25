package vmoption;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试OOP
 */
public class Main {
    public static void main(String[] args) {
//        int times = 4000000;
//        List<String> list = new ArrayList<String>();
//        for (int i = 0; i < times; i++) {  //这里测试堆OOP
//            list.add(String.valueOf(i));
//        }
//        System.out.println(list);
//        digui();  //这是测试 StackOverflowError

        int threads = Short.MAX_VALUE;
        for (int i = 0; i < 1; i++) {
            new ThreadOom().start();
        }

    }

    static public void digui(){
        digui();
    }


    static class ThreadOom extends Thread{

        static List<String> list = new ArrayList<String>();

        private void digui(){

            while(true){
                list.add(list.size()+"");
            }
        }
        @Override
        public void run() {
            digui();
        }
    }
}
