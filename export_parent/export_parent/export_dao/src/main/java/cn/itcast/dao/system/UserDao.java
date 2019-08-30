package cn.itcast.dao.system;

import cn.itcast.domain.system.User;

import java.util.List;


public interface UserDao {

	//根据企业id查询全部
	List<User> findAll(String companyId);

	//根据id查询
    User findById(String userId);

	//根据id删除
	void delete(String userId);

	//保存
	void save(User user);

	//更新
	void update(User user);

	// 根据用户id，查询用户角色中间表
    long findUserRoleByUserId(String userId);

    void deleUserRoleByUserId(String userId);

    //给用户添加角色
	void saveUserRole(String userId, String roleId);

    User findByEmail(String email);
}