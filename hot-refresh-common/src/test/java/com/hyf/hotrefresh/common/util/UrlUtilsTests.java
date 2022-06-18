package com.hyf.hotrefresh.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class UrlUtilsTests {

    @Test
    public void testClean() {
        String url = "ddd/";
        assertEquals(UrlUtils.clean(url), "/ddd");

        url = "http:sss/ss/";
        assertEquals(UrlUtils.clean(url), "http:sss/ss");
    }

    @Test
    public void testConcat() {
        assertEquals(UrlUtils.concat("aa", "bb"), "/aa/bb");
        assertEquals(UrlUtils.concat("aa/", "bb"), "/aa/bb");
        assertEquals(UrlUtils.concat("aa", "/bb"), "/aa/bb");
        assertEquals(UrlUtils.concat("aa/", "/bb"), "/aa/bb");
        assertEquals(UrlUtils.concat("aa/", "bb/"), "/aa/bb");
        assertEquals(UrlUtils.concat("/aa", "bb/"), "/aa/bb");
        assertEquals(UrlUtils.concat("/aa", "/bb"), "/aa/bb");
        assertEquals(UrlUtils.concat("http:aa", "/bb"), "http:aa/bb");
        assertEquals(UrlUtils.concat("http://aa", "bb"), "http://aa/bb");
        assertEquals(UrlUtils.concat("http://aa", "/bb"), "http://aa/bb");

    }
}
