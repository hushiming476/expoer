package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.service.cargo.ExtCproductService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

// 导入dubbo提供的注解
// import com.alibaba.dubbo.config.annotation.Service;
@Service(timeout = 100000)
public class ExtCproductServiceImpl implements ExtCproductService {

    // 注入dao
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<ExtCproduct> findByPage(ExtCproductExample extCproductExample,
                                        int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(extCproductDao.selectByExample(extCproductExample));
    }

    @Override
    public List<ExtCproduct> findAll(ExtCproductExample extCproductExample) {
        return extCproductDao.selectByExample(extCproductExample);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ExtCproduct extCproduct) {
        // 1. 设置附件id
        extCproduct.setId(UUID.randomUUID().toString());

        // 2. 计算附件金额
        Double amount = 0d;
        if (extCproduct.getCnumber()!=null &&
                extCproduct.getPrice()!=null){
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        // 设置附件金额
        extCproduct.setAmount(amount);


        // 3. 修改购销合同总金额 += 附件金额
        // 3.1 根据附件对象中的购销合同id查询购销合同
        Contract contract =
                contractDao.selectByPrimaryKey(extCproduct.getContractId());
        // 3.2 设置总金额
        contract.setTotalAmount(contract.getTotalAmount() + amount);
        // 3.3 设置附件数量+1
        contract.setExtNum(contract.getExtNum()+1);
        // 3.4 修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        // 4. 保存附件
        extCproductDao.insertSelective(extCproduct);
    }

    @Override
    public void update(ExtCproduct extCproduct) {
        //1. 计算附件金额
        Double amount = 0d;
        if (extCproduct.getCnumber()!=null &&
                extCproduct.getPrice()!=null){
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        // 设置附件金额
        extCproduct.setAmount(amount);
        
        //2. 查询数据库中修改之前的附件金额。（因为最后要修改购销合同总金额）
        ExtCproduct dbExtCproduct = extCproductDao.selectByPrimaryKey(extCproduct.getId());
        // 获取修改前附件金额
        Double oldAmount = dbExtCproduct.getAmount();

        //3. 修改购销合同总金额 = 总金额 + amount - oldAmount
        //3.1 根据购销合同id查询
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        // 3.2 设置总金额
        contract.setTotalAmount(contract.getTotalAmount() + amount - oldAmount);
        // 3.3 修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        // 4. 修改附件
        extCproductDao.updateByPrimaryKeySelective(extCproduct);
    }

    @Override
    public void delete(String id) {
        //1. 根据附件id查询附件
        ExtCproduct extCproduct =
                extCproductDao.selectByPrimaryKey(id);

        //2. 获取附件金额
        Double amount = extCproduct.getAmount();

        //3. 修改购销合同
        //3.1 根据购销合同id查询
        Contract contract =
                contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //3.2 修改购销合同总金额 -= 附件金额
        contract.setTotalAmount(contract.getTotalAmount()-amount);
        //3.2 修改购销合同附件数量
        contract.setExtNum(contract.getExtNum() - 1);
        //3.3 修改保存
        contractDao.updateByPrimaryKeySelective(contract);

        //4. 删除附件
        extCproductDao.deleteByPrimaryKey(id);
    }
}
