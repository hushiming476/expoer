package cn.itcast.service.system.impl;

import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    // 注入dao
    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<User> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(userDao.findAll(companyId));
    }

    @Override
    public User findById(String userId) {
        return userDao.findById(userId);
    }

    @Override
    public boolean delete(String userId) {
        //1. 根据用户id，查询用户角色中间表
        long count = userDao.findUserRoleByUserId(userId);
        //2. 判断
        if (count == 0) {
            userDao.delete(userId);
            return true;
        }
        return false;
    }

    @Override
    public void save(User user) {
        user.setId(UUID.randomUUID().toString());
        // 加密加盐
        if (user.getPassword() != null){
            String encodePwd =
                    new Md5Hash(user.getPassword(),user.getEmail()).toString();
            user.setPassword(encodePwd);
        }
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public void changeRole(String userId, String[] roleIds) {
        //DELETE FROM pe_role_user WHERE user_id=''
        //INSERT INTO pe_role_user(user_id,role_id)VALUES();

        //1） 先解决用户角色关系
        userDao.deleUserRoleByUserId(userId);

        //2） 给用户添加角色
        if (roleIds != null && roleIds.length > 0){
            for (String roleId : roleIds) {
                userDao.saveUserRole(userId,roleId);
            }
        }
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
