package com.multitv.yuv.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by naseeb on 10/24/2016.
 */

public class ExceptionUtils {

    public static void printStacktrace(Exception e) {
        if(e != null) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            Tracer.error("error", writer.toString());
        }
    }
}
