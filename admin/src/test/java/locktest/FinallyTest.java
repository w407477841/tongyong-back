package locktest;

public class FinallyTest {

    public static void main(String[] args) {

        try {

            main1();

        }finally {
            System.out.println("finally");
        }

        System.out.println("xiamian");

    }

    public static void main1(){
        throw new RuntimeException("哈哈");
    }

}
