package cn.itcast.service.system.impl;

import cn.itcast.dao.system.ModuleDao;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleDao moduleDao;
    @Autowired
    private UserService userService;

    @Override
    public PageInfo<Module> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(moduleDao.findAll());
    }

    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }

    @Override
    public List<Module> findModulesByRoleId(String roleId) {
        return moduleDao.findModulesByRoleId(roleId);
    }

    @Override
    public List<Module> findModulesByUserId(String id) {
        /**
         * 用户等级
         * 0-saas管理员: SELECT * FROM ss_module WHERE belong=0
         * 1-企业管理员:  SELECT * FROM ss_module WHERE belong=1
         * 其他：        用户角色中间表、角色权限中间表、权限表关联查询
         */
        // 根据用户的id查询
        User user = userService.findById(id);
        // 根据用户的登陆进行判断
        if (user.getDegree() == 0){
            // 说明是Saas管理员
            return moduleDao.findModulesByBelong(0);
        }
        else if (user.getDegree() == 1){
            // 说明是企业管理员（系统管理员）
            return moduleDao.findModulesByBelong(1);
        }
        else {
            // 其他非管理员用户，根据用户查询权限。
            List<Module> moduleList = moduleDao.findModulesByUserId(id);
            return moduleList;
        }
    }

    @Override
    public Module findById(String id) {
        return moduleDao.findById(id);
    }

    @Override
    public void save(Module module) {
        // 设置主键
        module.setId(UUID.randomUUID().toString());
        moduleDao.save(module);
    }

    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    @Override
    public void delete(String id) {
        moduleDao.delete(id);
    }
}
