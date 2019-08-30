package cn.itcast.service.system;

import cn.itcast.domain.system.Role;
import com.github.pagehelper.PageInfo;

import java.util.List;


public interface RoleService {

    //根据id查询
    Role findById(String id);

    //查询全部
    PageInfo<Role> findByPage(String companyId,int pageNum,int pageSize);

	//根据id删除
    void delete(String id);

	//添加
    void save(Role role);

	//更新
    void update(Role role);

    //给角色分配权限
    void updateRoleModule(String roleId, String moduleIds);

    List<Role> findAll(String companyId);

    //根据用户id查询角色
    List<Role> findRoleByUserId(String id);
}