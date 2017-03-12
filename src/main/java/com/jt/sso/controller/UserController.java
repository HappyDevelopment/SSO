package com.jt.sso.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jt.common.service.RedisService;
import com.jt.common.vo.SysResult;
import com.jt.sso.pojo.User;
import com.jt.sso.service.BaseService;
import com.jt.sso.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 单点登录系统， 进行业务接口的对接
 * <p>
 * 检查数据是否可用 ---AJAX查询用户名是否存在
 * 用户注册接口
 * 用户登录接口
 * 通过token查询用户信息
 * Created by 王兆琦  on 2017/2/16 20.29.
 */
@Controller
public class UserController extends BaseService<User> {

    @Resource
    private UserService userService;

    @Resource
    private RedisService redisService;
    /**
     * http://sso.jt.com/user/check/{param}/{type}
     * 格式如：chenchen/1
     * 其中chenchen是校验的数据
     * Type为类型，可选参数1 username、2 phone、3 email
     *
     * @return status: 200  //200 成功，201 没有查到
     * msg: “OK”  //返回信息消息
     * data: false  //返回数据true用户已存在，false用户不存在，可以
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public SysResult checkData(@PathVariable String param, @PathVariable Integer type) {

        Boolean bool = userService.checkData(param, type);

        return SysResult.ok(bool);

    }

    /**
     * http://sso.jt.com/user/register
     * 用户注册
     *
     * @param user 输入的信息
     *             username 用户名
                    password 密码
                    phone 手机号
                    email 邮箱

     * @return //返回数据true数据可用，false数据不可用
     */
    @RequestMapping("/user/register")
    @ResponseBody
    public SysResult register(User user) throws JsonProcessingException {

        String token = userService.saveUser(user);

        return SysResult.ok(token);

    }

    /**
     * http://sso.jt.com/user/login
     *
     * @param u u 用户名
     * @param p p 密码
     * @return 登录成功，返回ticket / token
     */
    @RequestMapping("/user/login")
    @ResponseBody
    public SysResult register(String u, String p) throws JsonProcessingException {
        User _user = new User();
        _user.setUsername(u);
        _user.setPassword(p);
        String token = userService.login(_user);

        if (token == null) {
            return SysResult.build(201,"",null);
        } else {

            return SysResult.ok(token);
        }

    }


    /**
     * http://sso.jt.com/user/query/{ticket}
     *
     * @param ticket  从Cookie中查询出来的key
     * @return
     * status: 200  //200 成功，201 没有查到
     * msg: “OK”  //返回信息消息
     * data: “{“id”:1,”username”:”chenchen”,”phone”:”13587203892”,”
     * email”:”chenchen@163.com”,”created”:183732749383,
     * ”updated”:12838373932}”
     */
    @RequestMapping("/user/query/{ticket}")
    @ResponseBody
    public SysResult queryTieck(@PathVariable String ticket) {
        String userJson = redisService.get(ticket);
        return SysResult.ok(userJson);

    }
}
