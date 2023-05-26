package com.sixsixdog.redis.tools.log;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * @Package: com.sixsixdog.redis.tools.log
 * @ClassName: ColorLog
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-25 14:23
 * @Description:
 */
public class ColorLog {
    private static final String ANSI_RESET ="\u001B[0m";
    private static final String ANSI_BLACK ="\u001B[100m";
    private static final String ANSI_RED ="\u001B[91m";
    private static final String ANSI_GREEN ="\u001B[92m";
    private static final String ANSI_YELLOW ="\u001B[93m";
    private static final String ANSI_BLUE ="\u001B[94m";
    private static final String ANSI_PURPLE ="\u001B[95m";
    private static final String ANSI_CYAN ="\u001B[96m";
    private static final String ANSI_WHITE ="\u001B[37m";
    private static final String PREFIX =ANSI_RED+"C"+ANSI_PURPLE+"o"+ANSI_CYAN+"r"+ANSI_BLUE+"g"+ANSI_YELLOW+"i"+ANSI_RESET+": ";
    private static final PropertyPlaceholderHelper resolver = new PropertyPlaceholderHelper("{","}");


    public void info(String str){
        outWarp(ANSI_GREEN + str + ANSI_RESET);
    }
    public void info(String str,Object... objs){
        outWarp(ANSI_GREEN + placeHolderHandler(str,objs) + ANSI_RESET);
    }
    public void debug(String str){
        outWarp(ANSI_BLUE + str + ANSI_RESET);
    }
    public void debug(String str,Object... objs){
        outWarp(ANSI_BLUE + placeHolderHandler(str,objs) + ANSI_RESET);
    }

    public void error(String str){
        outWarp(ANSI_RED + str + ANSI_RESET);
    }
    public void error(String str,Object... objs){
        outWarp(ANSI_RED + placeHolderHandler(str,objs) + ANSI_RESET);
    }
    public void warn(String str){
        outWarp(ANSI_YELLOW + str + ANSI_RESET);
    }
    public void warn(String str,Object... objs){
        outWarp(ANSI_YELLOW + placeHolderHandler(str,objs) + ANSI_RESET);
    }

    //参数替换
    String placeHolderHandler(String str,Object... objs){
        ArrayDeque<Object> listDeque = new ArrayDeque<Object>(Arrays.asList(objs));
        String sb = resolver.replacePlaceholders(str, (s) -> {
            Object poll = listDeque.poll();
//            if(poll==null)
//                return "";
            return String.valueOf(poll);
        });
        return sb;
    }
    //输出包装
    void outWarp(String s){
        System.out.println(PREFIX+s);
    }

    /**
     * 获取输出颜色
     */
    void outPutAllColor(){
        String REST ="\u001B[0m";
        for (Integer i = 1; i < 108; i++) {
            String CLR ="\u001B["+i+"m";
            System.out.println(CLR+i+REST);
        }
    }

}
