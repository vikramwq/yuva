package com.multitv.yuv.utilities;

/**
 * Created by naseeb on 11/4/2016.
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private ExceptionHandler() {

    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        Tracer.error("MKR", "****intex tv carshes*** " + e.getMessage());
        e.printStackTrace();
        System.exit(0);
    }

    public static void attach() {
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

}
