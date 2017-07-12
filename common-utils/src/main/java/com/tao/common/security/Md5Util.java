package com.tao.common.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by michael on 17-7-12.
 */
public final class Md5Util {

    private Md5Util() {}

    /**
     * md5加密
     * @param str
     * @return
     */
    public static String md5(String str) {
        return DigestUtils.md5Hex(str.getBytes());
    }
}
