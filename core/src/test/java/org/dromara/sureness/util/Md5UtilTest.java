package org.dromara.sureness.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.dromara.sureness.util.Md5Util;

/**
 * @author tomsun28
 * @date 16:28 2020-03-08
 */
public class Md5UtilTest {

    @Test
    public void md5() {
        String md5 = Md5Util.md5("tom--hello");
        assertNotNull(md5);
    }
}