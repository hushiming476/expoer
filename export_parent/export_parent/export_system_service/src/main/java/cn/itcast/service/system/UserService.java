package cn.itcast.service.system;

import cn.itcast.domain.system.User;
import com.github.pagehelper.PageInfo;


public interface UserService {

	//根据企业id查询全部
	PageInfo<User> findByPage(String companyId,int pageNum,int pageSize);

	//根据id查询
    User findById(String userId);

	//根据id删除
	boolean delete(String userId);

	//保存
	void save(User user);

	//更新
	void update(User user);

	//给用户分配角色
    void changeRole(String userId, String[] roleIds);

    // 登陆时候根据邮箱查询
    User findByEmail(String email);
}