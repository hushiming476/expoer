package cn.itcast.service.system.impl;

import cn.itcast.dao.system.RoleDao;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(roleDao.findAll(companyId));
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public void save(Role role) {
        // 设置主键
        role.setId(UUID.randomUUID().toString());
        roleDao.save(role);
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    // 给角色分配权限
    @Override
    public void updateRoleModule(String roleId, String moduleIds) {
        //1) 先解除角色权限的关系
        roleDao.deleteRoleModuleByRoleId(roleId);

        //2) 给角色添加权限
        if (moduleIds != null && !"".equals(moduleIds.trim())){
            // 分割字符串
            String[] array = moduleIds.split(",");
            // 遍历选中的每一个角色
            if (array != null && array.length>0){
                for (String moduleId : array) {
                    //给角色添加权限
                    roleDao.saveRoleModule(roleId,moduleId);
                }
            }
        }
    }

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public List<Role> findRoleByUserId(String id) {
        return roleDao.findRoleByUserId(id);
    }

    @Override
    public void delete(String id) {
        roleDao.delete(id);
    }
}
