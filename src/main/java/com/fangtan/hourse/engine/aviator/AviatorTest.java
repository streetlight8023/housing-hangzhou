package com.fangtan.hourse.engine.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class AviatorTest {
    public static void main(String[] args) {
        test8();
    }


    public static void test8() {
        Map<String, Object> env = new HashMap<String, Object>();

        final Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(date);
        env.put("date", date);
        env.put("dateStr", dateStr);

        Boolean result = (Boolean) AviatorEvaluator.execute("date==dateStr",env);
        System.out.println(result);

        result = (Boolean) AviatorEvaluator.execute("date > '2009-12-20 00:00:00:00' ", env);
        System.out.println(result);

        result = (Boolean) AviatorEvaluator.execute("date < '2200-12-20 00:00:00:00' ", env);
        System.out.println(result);

        result = (Boolean) AviatorEvaluator.execute("date == date ", env);
        System.out.println(result);
    }


    public static void test7() {
        String email = "hello2018@gmail.com";
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("email", email);
        String username = (String) AviatorEvaluator.execute("email=~/([\\w0-8]+)@\\w+[\\.\\w+]+/ ? $1 : 'unknow' ", env);
        System.out.println(username);
    }

    public static void test6() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("a", -5);
        String result = (String) AviatorEvaluator.execute("a>0? 'yes':'no'", env);
        System.out.println(result);
    }

    public static void test5() {
        final List<String> list = new ArrayList<>();
        list.add("hello");
        list.add(" world");

        final int[] array = new int[3];
        array[0] = 0;
        array[1] = 1;
        array[2] = 3;

        final Map<String, Date> map = new HashMap<>();
        map.put("date", new Date());

        Map<String, Object> env = new HashMap<>();
        env.put("list", list);
        env.put("array", array);
        env.put("map", map);

        System.out.println(AviatorEvaluator.execute(
                "list[0]+':'+array[0]+':'+'today is '+map.date", env));
    }

    public static void test4() {
        String expression = "a+(b-c)>100";
        // 编译表达式
        Expression compiledExp = AviatorEvaluator.compile(expression);

        Map<String, Object> env = new HashMap<>();
        env.put("a", 100.3);
        env.put("b", 45);
        env.put("c", -199.100);

        // 执行表达式
        Boolean result = (Boolean) compiledExp.execute(env);
        System.out.println(result);
    }

    public static void test3() {

        AviatorEvaluator.addFunction(new MultiplyFunction());
        // 方式1
        System.out.println(AviatorEvaluator.execute("multiply(12.23, -2.3)"));
        // 方式2
        Map<String, Object> params = new HashMap<>();
        params.put("a", 12.23);
        params.put("b", -2.3);
        System.out.println(AviatorEvaluator.execute("multiply(a, b)", params));

    }

    /**
     * 调用java函数
     */
    public static void test2() {
        Map<String, Object> map = new HashMap<>();
        map.put("s1", "123qwer");
        map.put("s2", "123");
        if (Boolean.valueOf(AviatorEvaluator.execute("string.startsWith(s1,s2)", map).toString())) {
            System.out.println("答案 正确");
        } else {
            System.out.println("请 再接再励");
        }
    }

    public static void test1() {
        // exec执行方式，无需传递Map格式
        String age = "18";
        System.out.println(AviatorEvaluator.exec("'His age is '+ age +'!'", age));

        // execute执行方式，需传递Map格式
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("age", "18");
        System.out.println(AviatorEvaluator.execute("'His age is '+ age +'!'", map));

    }

    static class MultiplyFunction extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

            double num1 = FunctionUtils.getNumberValue(arg1, env).doubleValue();
            double num2 = FunctionUtils.getNumberValue(arg2, env).doubleValue();
            return new AviatorDouble(num1 * num2);
        }

        @Override
        public String getName() {
            return "multiply";
        }

    }

}
