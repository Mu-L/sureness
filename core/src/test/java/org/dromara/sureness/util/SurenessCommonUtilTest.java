package org.dromara.sureness.util;


import org.dromara.sureness.util.SurenessCommonUtil;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author tomsun28
 * @date 16:29 2020-03-08
 */
public class SurenessCommonUtilTest {

    private static final String USER_AGENT = "User-Agent";
    private static final String LINUX = "Linux";

    @Test
    public void findUserAgent() {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        expect(request.getHeader(USER_AGENT)).andReturn(LINUX).anyTimes();
        replay(request);
        String userAgent = request.getHeader(USER_AGENT);
        assertNotNull(userAgent);
        assertEquals(LINUX, SurenessCommonUtil.findUserAgent(userAgent));
        verify(request);
    }

    @Test
    public void getRandomString() {
        String randomString = SurenessCommonUtil.getRandomString(6);
        assertNotNull(randomString);
        assertEquals(6, randomString.length());
    }
}