package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginUserResponseVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lfy
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-05-31 10:45:25
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 登录
     * @param userInfo
     * @return
     */
    LoginUserResponseVo login(UserInfo userInfo);

    /**
     * redis保存用户认证信息
     * @param userInfo
     * @return 返回用户令牌
     */
    String saveUserAuthenticationInfo(UserInfo userInfo);

    /**
     * 用户退出
     * @param token
     */
    void logout(String token);
}
