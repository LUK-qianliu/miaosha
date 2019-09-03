package com.qianliu.demo.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qianliu.demo.access.UserContext;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;



@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	private Logger logger = LoggerFactory.getLogger(UserArgumentResolver.class);

	@Autowired
	MiaoshaUserService userService;

	/**
	 * 获取参数类型，通过这种方式指定需要处理的参数类型
	 * @param parameter
	 * @return
	 */
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return clazz == MiaoshaUser.class;
	}

	/**
	 *
	 * @param parameter
	 * @param mavContainer
	 * @param webRequest
	 * @param binderFactory
	 * @return
	 * @throws Exception
	 */
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//		// 取出request和response
//		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
//
//		// 取出传入的参数和cookie中是否有有“token”
//		String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
//		String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
//		//logger.info("paramToken:=========================="+paramToken);
//		//logger.info("cookieToken:==========================="+cookieToken);
//		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
//			return null;
//		}
//		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//		return userService.getUserByToken(response, token);
		return UserContext.getUser();
	}

	/**
	 * 遍历cookie，通过name其中的cookie
	 * @param request
	 * @param cookiName
	 * @return
	 */
//	private String getCookieValue(HttpServletRequest request, String cookiName) {
//		Cookie[]  cookies = request.getCookies();
//		for(Cookie cookie : cookies) {
//			if(cookie.getName().equals(cookiName)) {
//				return cookie.getValue();
//			}
//		}
//		return null;
//	}

}
