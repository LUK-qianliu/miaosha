package com.qianliu.demo.service;

import com.qianliu.demo.dao.MiaoshaUserDAO;
import com.qianliu.demo.domain.MiaoshaUser;
import com.qianliu.demo.exception.GlobalException;
import com.qianliu.demo.redis.RedisService;
import com.qianliu.demo.redis.domain.impl.UserKey;
import com.qianliu.demo.result.CodeMsg;
import com.qianliu.demo.util.MD5Util;
import com.qianliu.demo.util.UUIDUtil;
import com.qianliu.demo.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Resource
    MiaoshaUserDAO miaoshaUserDao;

    @Autowired
    RedisService redisService;

    /**
     *
     * 通过id查询MiaoshaUser
     * @param id
     * @return
     */
    public MiaoshaUser getById(long id) {

        //取出缓存
        MiaoshaUser user = redisService.get(UserKey.getById, "" + id, MiaoshaUser.class);
        if(user != null) {
            return user;
        }
        //取数据库
        user = miaoshaUserDao.getById(id);
        if(user != null) {
            redisService.set(UserKey.getById, ""+id, user);
        }
        return miaoshaUserDao.getById(id);
    }

    /**
     * 更改用户密码
     * @param token
     * @param id
     * @param formPass
     * @return
     */
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(UserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token, token, user);
        return true;
    }

    /**
     * 用户登录
     * @param response
     * @param loginVo
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成的token,放入redis和cookie中
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);

        return token;
    }

    /**
     * 从redis中取出token对应MiaoshaUser
     * @param token
     * @return
     */
    public MiaoshaUser getUserByToken(HttpServletResponse response , String token) {
        if(StringUtils.isEmpty(token))
            return null;

        MiaoshaUser user = redisService.get(UserKey.token, token, MiaoshaUser.class);

        // 延长cookie有效期
        if(user != null){
            addCookie(response,token,user);
        }

        return user;
    }

    /**
     * 工具类：向redis中写入cookie，并且向客户端写入cookie
     * @param response
     * @param user
     */
    private void addCookie(HttpServletResponse response,String token, MiaoshaUser user) {
        redisService.set(UserKey.token,token,user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds()); // cookie有效期和redis数据中token效期一直
        cookie.setPath("/");
        response.addCookie(cookie); // cookie写入客户端
    }


}
