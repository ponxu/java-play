package com.ponxu.test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author ponxu
 * @date 2016-10-19
 */
public class TestURI {
    public static void main(String[] args) throws URISyntaxException {
        // [scheme:][//authority][path][?query][#fragment]
        // authority为[user-info@]host[:port]
        URI uri = new URI("abc://xwz:pasw@com.go.hi:8080/a/b/c?name=xwz#add");
        System.out.println(uri.getScheme());
        System.out.println(uri.getAuthority());
        System.out.println(uri.getHost());
        System.out.println(uri.getPort());
        System.out.println(uri.getPath());
        System.out.println(uri.getFragment());
        System.out.println(uri.getQuery());
    }
}
