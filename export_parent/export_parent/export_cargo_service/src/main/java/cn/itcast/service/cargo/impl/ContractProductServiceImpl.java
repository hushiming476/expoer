package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ContractProductDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.vo.ContractProductVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

// 导入dubbo提供的注解
// import com.alibaba.dubbo.config.annotation.Service;
@Service(timeout = 100000)
public class ContractProductServiceImpl implements ContractProductService {

    // 注入dao
    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExtCproductDao extCproductDao;

    @Override
    public PageInfo<ContractProduct> findByPage(
            ContractProductExample contractProductExample,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(contractProductDao.selectByExample(contractProductExample));
    }

    @Override
    public List<ContractProduct> findAll(ContractProductExample contractProductExample) {
        return contractProductDao.selectByExample(contractProductExample);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ContractProduct contractProduct) {
        // 1.设置货物id
        contractProduct.setId(UUID.randomUUID().toString());

        // 2. 计算货物金额
        Double amount = 0d;
        if (contractProduct.getCnumber() != null &&
                contractProduct.getPrice() != null) {
            amount = contractProduct.getCnumber() * contractProduct.getPrice();
        }
        // 设置货物金额
        contractProduct.setAmount(amount);

        // 3. 保存货物
        contractProductDao.insertSelective(contractProduct);

        // 4. 修改购销合同总金额。 销合同总金额 += 货物金额
        // 4.1 根据购销合同id查询
        Contract contract =
                contractDao.selectByPrimaryKey(contractProduct.getContractId());
        // 4.2 设置购销合同总金额
        contract.setTotalAmount(contract.getTotalAmount() + amount);
        // 4.3 设置购销合同的货物数量加1
        contract.setProNum(contract.getProNum() + 1);
        // 4.4 保存购销合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void update(ContractProduct contractProduct) {
        //1. 计算修改后的货物金额
        Double amount = 0d;
        if (contractProduct.getPrice()!=null && contractProduct.getCnumber()!=null){
            amount = contractProduct.getPrice() * contractProduct.getCnumber();
        }
        contractProduct.setAmount(amount);

        //2. 根据货物id查询货物对象（数据库中的货物，也是修改前的货物金额）
        ContractProduct dbContractProdcut =
                contractProductDao.selectByPrimaryKey(contractProduct.getId());

        //3. 获取修改前的货物金额
        Double oldAmount = dbContractProdcut.getAmount();

        //4. 【修改货物】
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        //5. 【修改购销合同总金额】
        //5.1 根据购销合同id查询购销合同对象
        Contract contract =
                contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //5.2 设置购销合同总金额 = 购销合同总金额 + 修改后 - 修改前
        contract.setTotalAmount(contract.getTotalAmount() + amount - oldAmount);
        //5.3 修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        //1. 根据货物id查询, 获取货物金额
        ContractProduct contractProduct =
                contractProductDao.selectByPrimaryKey(id);
        // 获取货物金额
        Double cpAmount = contractProduct.getAmount();

        //2. 根据货物id查询，获取所有附件。累加附件金额。 删除货物附件。
        //A. 查询条件：根据货物id查询附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(id);
        //B. 查询货物的所有附件
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);
        //C. 保存附件金额
        Double extcAmount = 0d;
        //D. 遍历附件
        if (extCproductList != null && extCproductList.size() > 0) {
            for (ExtCproduct extCproduct : extCproductList) {
                //E. 累计附件金额
                extcAmount += extCproduct.getAmount();
                //F. 删除货物附件
                extCproductDao.deleteByPrimaryKey(extCproduct.getId());
            }
        }

        //3. 修改购销合同
        //A. 根据货物的购销合同id，查询购销合同对象
        Contract contract =
                contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //B. 修复购销合同总金额 = 购销合同总金额 - 货物金额 - 附件金额
        contract.setTotalAmount(contract.getTotalAmount() - cpAmount - extcAmount);
        //C. 修改货物数量、附件数量
        contract.setProNum(contract.getProNum() - 1);
        contract.setExtNum(contract.getExtNum() - extCproductList.size());
        //D. 修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //4. 删除货物
        contractProductDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<ContractProductVo> findByShipTime(String shipTime, String companyId) {
        return contractProductDao.findByShipTime(shipTime,companyId);
    }
}
