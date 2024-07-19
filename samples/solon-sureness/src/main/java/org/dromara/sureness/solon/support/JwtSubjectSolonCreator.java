package org.dromara.sureness.solon.support;

import org.dromara.sureness.subject.Subject;
import org.dromara.sureness.subject.SubjectCreate;
import org.dromara.sureness.subject.support.JwtSubject;
import org.dromara.sureness.util.JsonWebTokenUtil;
import org.noear.solon.core.handle.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JwtSubject creator
 * only support solon HTTP request
 * @author tomsun28
 * @date 2021/5/7 20:51
 */
public class JwtSubjectSolonCreator implements SubjectCreate {

    private static final Logger logger = LoggerFactory.getLogger(JwtSubjectSolonCreator.class);

    private static final String BEARER = "Bearer";
    private static final String AUTHORIZATION = "Authorization";

    @Override
    public boolean canSupportSubject(Object context) {
        // support bearer jwt
        // ("Authorization", "Bearer eyJhbGciOiJIUzUxMi...")  --- jwt auth
        if (context instanceof Context) {
            String authorization = ((Context)context).header(AUTHORIZATION);
            if (authorization != null && authorization.startsWith(BEARER)) {
                String jwtValue = authorization.replace(BEARER, "").trim();
                return !JsonWebTokenUtil.isNotJsonWebToken(jwtValue);
            }
        }
        return false;
    }

    @Override
    public Subject createSubject(Object context) {
        String authorization = ((Context)context).header(AUTHORIZATION);

        if (authorization != null && authorization.startsWith(BEARER)) {
            // jwt token
            String jwtValue = authorization.replace(BEARER, "").trim();
            if (JsonWebTokenUtil.isNotJsonWebToken(jwtValue)) {
                if (logger.isInfoEnabled()) {
                    logger.info("can not create JwtSubject by this request message, is not jwt");
                }
                return null;
            }

            String remoteHost = ((Context) context).ip();
            String requestUri = ((Context) context).path();
            String requestType = ((Context) context).method();
            String targetUri = requestUri.concat("===").concat(requestType.toLowerCase());

            return JwtSubject.builder(jwtValue)
                    .setRemoteHost(remoteHost)
                    .setTargetResource(targetUri)
                    .build();
        }

        return null;
    }
}