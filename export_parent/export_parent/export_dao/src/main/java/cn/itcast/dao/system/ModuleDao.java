package cn.itcast.dao.system;

import cn.itcast.domain.system.Module;

import java.util.List;

public interface ModuleDao {

    //根据id查询
    Module findById(String moduleId);

    //根据id删除
    void delete(String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    //查询角色已经拥有的权限. 查询条件：角色ID
    List<Module> findModulesByRoleId(String roleId);

    // 根据belong查询权限
    List<Module> findModulesByBelong(int belong);

    // 根据用户查询权限。
    List<Module> findModulesByUserId(String id);
}