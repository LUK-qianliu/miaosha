package com.qianliu.demo.service;

import com.qianliu.demo.dao.UserDAO;
import com.qianliu.demo.domain.User;
import com.qianliu.demo.vo.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class UserService {
    @Resource
    UserDAO userDAO;

    /**
     * 通过id获取数据
     * @param id
     * @return
     */
    public User getById(int id){
        return userDAO.getById(id);
    }

    /**
     *测试mybatis的事务：已知数据库中存在一行id为1的数据，我们此时插入一条数据为2的，
     * 再插入一条数据为1的数据，看是否回滚
     * @return
     */
    @Transactional //@Transactional标签表示这是一个事务
    public Boolean tx() {
        User user1 = new User(2,"xiaofang");
        userDAO.insert(user1);

        User user2 = new User(1,"xiaofang");
        userDAO.insert(user2);

        //如果插入成功，直接返回true，否则抛出异常
        return true;
    }

}
