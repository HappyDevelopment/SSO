package com.jt.sso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jt.common.service.RedisService;
import com.jt.sso.mapper.UserMapper;
import com.jt.sso.pojo.User;
import com.jt.sso.utils.ObjectMapperUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 王兆琦  on 2017/2/16 20.36.
 */
@Service
public class UserService extends BaseService<User> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    /**
     * @param param 前台数据
     * @param type  填写的内容类型
     * @return 布尔值
     */
    public Boolean checkData(String param, Integer type) {

        Map<String, String> map = new HashMap<String, String>();

        // 填写时那种类型的参数
        if (type == 1) {
            map.put("typename", "username");
        } else if (type == 2) {
            map.put("typename", "phone");
        } else {
            map.put("typename", "email");
        }

        // 填写数据
        map.put("param", param);

        //ture  已存在
        return userMapper.checkData(map) > 0 ? true : false;
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    public String  saveUser(User user) throws JsonProcessingException {

        //密码加密
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));

        //防止唯一性错误，  故意设置
        user.setEmail(user.getPhone());

        user.setCreated(new Date());

        user.setUpdated(user.getCreated());
        userMapper.insertSelective(user);

        // 需要返回页面token ， 让其自动登录
        String token = DigestUtils.md5Hex("TOKEN_" + user.getUsername() +
                user.getId() + System.currentTimeMillis());

        // 把用户json数据保存到缓存服务器中
        redisService.set(token, ObjectMapperUtil.getInstance().writeValueAsString(user));

        return token;
    }

    /**
     * 登录， 需要连接缓存服务器 ， 保存登录成功的 唯一动态 不可逆的token
     *
     * @param user
     * @return
     */
    public String login(User user) throws JsonProcessingException {

        // 查询时， 先从数据库中查出来次用户名的密码， 在进行与填写密码判断，
        // 优点速度快
        User param = new User();
        param.setUsername(user.getUsername());

        // 次查询语句是，传入的对象参数不为null进行where条件的设置
        User currUser = super.queryByWhere(param);

        if (currUser == null) {
            return null;
        }

        //和填写的密码判断
        String md5Password = DigestUtils.md5Hex(user.getPassword());

        // 可能查询出来的用户对象为 null

        if (currUser.getPassword().equals(md5Password)) {
            //用户登录成功
//唯一性，动态性，不可逆md5hash,其它不常用加密算法
            String token = DigestUtils.md5Hex("TOKEN_" + user.getUsername() +
                    currUser.getId() + System.currentTimeMillis());

            // 把用户json数据保存到缓存服务器中
            redisService.set(token, ObjectMapperUtil.getInstance().writeValueAsString(currUser));

            return token;

        } else {

            return null;
        }
    }
}
