package org.dromara.sureness.configuration;

import org.dromara.sureness.mgt.SecurityManager;
import org.dromara.sureness.processor.exception.DisabledAccountException;
import org.dromara.sureness.processor.exception.ExcessiveAttemptsException;
import org.dromara.sureness.processor.exception.ExpiredCredentialsException;
import org.dromara.sureness.processor.exception.IncorrectCredentialsException;
import org.dromara.sureness.processor.exception.NeedDigestInfoException;
import org.dromara.sureness.processor.exception.UnauthorizedException;
import org.dromara.sureness.processor.exception.UnknownAccountException;
import org.dromara.sureness.subject.SubjectSum;
import org.dromara.sureness.util.SurenessContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @author wangtao
 * @date 2021/7/8
 */
public class SurenessFilter implements Filter {

    private SecurityManager securityManager;

    public SurenessFilter(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    /** logger **/
    private static final Logger logger = LoggerFactory.getLogger(SurenessFilter.class);

    private static final String UPGRADE = "Upgrade";

    private static final String WEBSOCKET = "websocket";

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("servlet surenessFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("servlet surenessFilter destroyed");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        try {
            SubjectSum subject = securityManager.checkIn(servletRequest);
            // You can consider using SurenessContextHolder to bind subject in threadLocal
            // if bind, please remove it when end
            if (subject != null) {
                SurenessContextHolder.bindSubject(subject);
            }
        } catch (IncorrectCredentialsException | UnknownAccountException | ExpiredCredentialsException e1) {
            logger.debug("this request account info is illegal, {}", e1.getMessage());
            responseWrite(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Username or password is incorrect or token expired"), servletResponse);
            return;
        } catch (DisabledAccountException | ExcessiveAttemptsException e2 ) {
            logger.debug("the account is disabled, {}", e2.getMessage());
            responseWrite(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).body("Account is disabled"), servletResponse);
            return;
        } catch (NeedDigestInfoException e3) {
            logger.debug("you should try once again with digest auth information");
            responseWrite(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", e3.getAuthenticate()).build(), servletResponse);
            return;
        } catch (UnauthorizedException e4) {
            logger.debug("this account can not access this resource, {}", e4.getMessage());
            responseWrite(ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("This account has no permission to access this resource"), servletResponse);
            return;
        } catch (RuntimeException e) {
            logger.error("other exception happen: ", e);
            responseWrite(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
                    servletResponse);
            return;
        }

        try {
            // if ok, doFilter and add subject in request
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            int statusCode = ((HttpServletResponse) servletResponse).getStatus();
            String upgrade = ((HttpServletResponse) servletResponse).getHeader(UPGRADE);
            if (statusCode != HttpStatus.SWITCHING_PROTOCOLS.value() || !WEBSOCKET.equals(upgrade)) {
                SurenessContextHolder.clear();
            }
        }
    }

    /**
     * write response json data
     * @param content content
     * @param response response
     */
    private static void responseWrite(ResponseEntity content, ServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        ((HttpServletResponse)response).setStatus(content.getStatusCodeValue());
        content.getHeaders().forEach((key, value) ->
                ((HttpServletResponse) response).addHeader(key, value.get(0)));
        try (PrintWriter printWriter = response.getWriter()) {
            if (content.getBody() != null) {
                printWriter.write(content.getBody().toString());
            } else {
                printWriter.flush();
            }
        } catch (IOException e) {
            logger.error("responseWrite response error: ", e);
        }
    }
}
