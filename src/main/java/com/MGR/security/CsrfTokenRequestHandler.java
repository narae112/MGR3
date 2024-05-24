//package com.MGR.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.security.web.csrf.CsrfTokenRequestResolver;
//import org.springframework.util.Assert;
//
//public interface CsrfTokenRequestHandler extends CsrfTokenRequestResolver {
//
//    //토큰 예외처리기, 메서드 더 이상 생성하면 안됨
//
//    @Override
//    default String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
//        Assert.notNull(request, "request cannot be null"); //http 요청이 없음
//        Assert.notNull(csrfToken, "csrfToken cannot be null"); //토큰이 없음
//        String actualToken = request.getHeader(csrfToken.getHeaderName()); //헤더 이름을 가져와서 헤더토큰 값을 요청에서 찾음
//        if (actualToken == null) {
//            actualToken = request.getParameter(csrfToken.getParameterName());
//            //헤더에서 토큰 값을 못찾으면 요청 파라미터에서 토큰 찾음
//        }
//        return actualToken; //토큰 반환
//    }
//
//}
