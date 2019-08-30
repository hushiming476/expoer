package cn.itcast.service.system;

import cn.itcast.domain.system.Module;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ModuleService {

    //根据id查询
    Module findById(String moduleId);

    //根据id删除
    void delete(String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    PageInfo<Module> findByPage(int pageNum,int pageSize);

    // 查询全部
    List<Module> findAll();

    // 查询角色已经拥有的权限. 查询条件：角色ID
    List<Module> findModulesByRoleId(String roleId);

    // 根据用户id查询用户的权限（根据用户的degree级别判断）
    List<Module> findModulesByUserId(String id);
}