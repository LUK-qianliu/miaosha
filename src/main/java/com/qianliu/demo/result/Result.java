package com.qianliu.demo.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    /**
     * 成功的时候调用的构造方法
     * @param data result的data属性
     */
    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    /**
     * 失败时调用的构造方法，返回定义的数据类型
     * @param codeMsg codeMsg种的code和msg属性对应Result类种的code和msg属性
     */
    private Result(CodeMsg codeMsg){
        if(codeMsg==null){
            return;
        }

        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    /**
     * 成功的时候调用的方法，返回参数的类型已定义
     * @param data Result的data属性
     * @param <T> 声明为泛型方法
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * 失败的时候调用，返回已定义的参数类型
     * @param codeMsg 对应result的code和msg属性
     * @param <T> 定义为泛型方法
     * @return
     */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }
}
