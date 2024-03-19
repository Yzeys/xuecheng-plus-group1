package com.xuecheng.base.exception;

import lombok.Data;

@Data
public class XueChengPlusException extends Exception{
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage=errMessage;
    }

    public static void cast(String errMessage){
        throw new RuntimeException(errMessage);
    }

    public static void cast(CommonError commonError){
        throw new RuntimeException(commonError.getErrMessage());
    }
}
