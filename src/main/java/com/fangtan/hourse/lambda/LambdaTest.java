package com.fangtan.hourse.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * 测试lambda的  跟主流程不相关
 */
public class LambdaTest {


    public static void main(String[] args) {
//        consumerTest();
//        predicateTest();
//        predicateNeget();
//        predicateAnd();
//        functionAndThenTest();
        functionComposeTest();
    }

    private static void consumerTest() {
        Consumer<Mask> brand = m -> m.setBrand("3M");
        Consumer<Mask> type = m -> m.setType("N95");
        Consumer<Mask> price = m -> m.setPrice(19.9);
        Consumer<Mask> print = System.out::println;

        brand.andThen(type)
                .andThen(price)
                .andThen(print)
                .accept(new Mask());
    }

    private static void predicateTest() {
        Mask mask = new Mask("Honeywell", "N95", 19.5);
        Predicate<Mask> isN95 = m -> "N95".equals(m.getType());
        System.out.println(isN95.test(mask));
    }


    private static void predicateNeget() {
        Mask mask = new Mask("Honeywell", "N95", 19.5);
        Predicate<Mask> isN95 = m -> "N95".equals(m.getType());
        System.out.println(isN95.negate().test(mask));
    }

    private static void predicateAnd() {
        Mask mask = new Mask("Honeywell", "N95", 19.5);
        Predicate<Mask> isN95 = m -> "N95".equals(m.getType());
        Predicate<Mask> isHoneywell = m -> "Honeywe ll".equals(m.getBrand());
        System.out.println(isN95.and(isHoneywell).test(mask));
    }

    private static void functionAndThenTest(){
        Function<Integer, Integer> plusTwo = x -> x + 2;
        Function<Integer, Integer> timesThree = x -> x * 3;
        System.out.println(plusTwo.andThen(plusTwo).apply(1));
        System.out.println(plusTwo.andThen(timesThree).apply(1));
        System.out.println(plusTwo.andThen(timesThree).apply(2));
        System.out.println(plusTwo.andThen(timesThree).apply(3));
    }
    private static void functionComposeTest(){
        Function<Integer, Integer> plusTwo = x -> x + 2;
        Function<Integer, Integer> timesThree = x -> x * 3;
//        System.out.println(plusTwo.andThen(plusTwo).apply(1));
        System.out.println(plusTwo.compose(timesThree).compose(plusTwo).apply(1));
//        System.out.println(plusTwo.andThen(timesThree).apply(2));
//        System.out.println(plusTwo.andThen(timesThree).apply(3));
    }


}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Mask {
    private String brand;
    private String type;
    private Double price;

}