package com.yupi.usercenter.service;

import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user 用户
     * @param loginUser
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
     User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 根据传入的数字判断性别
     */
    List<User> getUserBySex(int gender);

    /**
     * 根据post传入查询
     * @param gender
     * @return
     */
    List<User> getUserBySexGo(int gender);

    /**
     * 通过mp来查询试试看
     * @param gender
     * @return
     */
    List<User> getUserByQuery(int gender);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
