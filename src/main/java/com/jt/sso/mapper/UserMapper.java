package com.jt.sso.mapper;

import com.jt.common.mapper.SysMapper;
import com.jt.sso.pojo.User;

import java.util.Map;

/**
 * Created by 王兆琦  on 2017/2/16 20.41.
 */
public interface UserMapper extends SysMapper<User> {

    public int checkData(Map map);
}
