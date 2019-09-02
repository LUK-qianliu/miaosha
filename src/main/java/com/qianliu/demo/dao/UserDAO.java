package com.qianliu.demo.dao;

import com.qianliu.demo.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserDAO {

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    public User getById(@Param("id") int id);

    /**
     * 插入数据
     * @param user user的id和name属性对应上#{id},#{name}
     * @return
     */
    @Insert("insert into user(id,name) values (#{id},#{name})")
    public int insert(User user);
}
