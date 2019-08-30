package cn.itcast.service.system.impl;

import cn.itcast.dao.system.SysLogDao;
import cn.itcast.domain.system.SysLog;
import cn.itcast.service.system.SysLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl implements SysLogService {

    // 注入日志dao
    @Autowired
    private SysLogDao sysLogDao;

    @Override
    public PageInfo<SysLog> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(sysLogDao.findAll(companyId));
    }

    @Override
    public void save(SysLog log) {
        sysLogDao.save(log);
    }
}
