package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.service.cargo.ContractService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.UUID;

// 导入dubbo提供的注解
// import com.alibaba.dubbo.config.annotation.Service;
@Service
public class ContractServiceImpl implements ContractService {

    // 注入dao
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<Contract> findByPage(ContractExample contractExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(contractDao.selectByExample(contractExample));
    }

    @Override
    public List<Contract> findAll(ContractExample contractExample) {
        return contractDao.selectByExample(contractExample);
    }

    @Override
    public Contract findById(String id) {
        return contractDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Contract contract) {
        // 设置id
        contract.setId(UUID.randomUUID().toString());

        // 设置状态
        contract.setState(0);
        // 设置创建时间
        contract.setCreateTime(new Date());
        // 设置货物数、附件数
        contract.setProNum(0);
        contract.setExtNum(0);

        // 设置购销合同总金额为0
        contract.setTotalAmount(0d);

        // 保存
        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        contractDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Contract> findByDeptId(String deptId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(contractDao.findByDeptId(deptId));
    }
}
