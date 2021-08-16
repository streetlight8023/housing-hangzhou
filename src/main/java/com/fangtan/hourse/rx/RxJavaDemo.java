package com.fangtan.hourse.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import reactor.util.annotation.NonNull;

public class RxJavaDemo {


    public static void main(String[] args) {
        test1();
    }

    public static void test1(){
        // 第一步：初始化Observable
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
                e.onNext(4);
            }
            // 第三步：订阅
        }).subscribe(new Observer<Integer>() {

            // 第二步：初始化Observer
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposable = d;
            }


            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println(integer);
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("onError : value : " + e.getMessage() + "\n" );
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete" );

            }
        });
    }


}
