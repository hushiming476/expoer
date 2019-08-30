package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.*;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductResult;
import cn.itcast.vo.ExportResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class ExportServiceImpl implements ExportService {
    // 注入dao
    @Autowired
    private ExportDao exportDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ExportProductDao exportProductDao;
    @Autowired
    private ExtEproductDao extEproductDao;

    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Export export) {
        //1. 设置报运单的id
        export.setId(UUID.randomUUID().toString());

        //2. 获取报运单的购销合同ids，多个id用逗号隔开
        String contractIds = export.getContractIds();

        //3. 修改购销合同状态为2、获取每一个购销合同的合同号
        String contractNos = "";
        //3.1 分割字符串
        String[] array = contractIds.split(",");
        //3.2 遍历每一个购销合同的id
        for (String contractId : array) {
            //3.2.1 根据购销合同id查询
            Contract contract = contractDao.selectByPrimaryKey(contractId);
            //3.2.2 设置状态
            contract.setState(2);
            //3.2.3 获取合同号,多个合同号用空格隔开
            contractNos += contract.getContractNo() + "";
            //3.2.4 修改购销合同
            contractDao.updateByPrimaryKeySelective(contract);
        }
        //3.2.5 设置合同号到报运单中
        export.setCustomerContract(contractNos);

        //4. 根据多个购销合同id查询其所有货物
        //4.1 构造条件： 购销合同的ids
        ContractProductExample cpExample = new ContractProductExample();
        cpExample.createCriteria().andContractIdIn(Arrays.asList(array));
        //4.2 根据条件查询： select * from co_contract_product where contract_id in(1,2)
        List<ContractProduct> cpList = contractProductDao.selectByExample(cpExample);

        /**
         * 定义一个map集合，保存：货物id和商品id
         */
        Map<String,String> map = new HashMap<>();

        //5. 保存报运单（1） 保存报运商品。   货物--->商品
        for (ContractProduct contractProduct : cpList) {
            //5.2 创建商品对象
            ExportProduct exportProduct = new ExportProduct();
            //5.2 数据拷贝: 货物-->商品
            BeanUtils.copyProperties(contractProduct,exportProduct);
            //5.2 设置商品的id
            exportProduct.setId(UUID.randomUUID().toString());
            //5.2 设置报运单的id
            exportProduct.setExportId(export.getId());
            //5.3 保存商品
            exportProductDao.insertSelective(exportProduct);
            // 保存货物id与商品id到map中
            map.put(contractProduct.getId(),exportProduct.getId());
        }


        //6. 根据多个购销合同id查询其所有附件
        //6.1 购销条件：根据购销合同ids查询其所有附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria()
                .andContractIdIn(Arrays.asList(array));
        //6.2 查询获取购销合同的附件
        List<ExtCproduct> extCproductList =
                extCproductDao.selectByExample(extCproductExample);

        //7. 保存报运单（2） 保存报运附件。   货物附件--->商品附件
        //7.1 遍历购销合同附件
        for (ExtCproduct extCproduct : extCproductList) {
            //7.2 创建报运单商品附件对象
            ExtEproduct extEproduct = new ExtEproduct();
            //7.3 对象拷贝： 货物附件--->商品附件
            BeanUtils.copyProperties(extCproduct,extEproduct);
            //7.4 设置附件id
            extEproduct.setId(UUID.randomUUID().toString());
            //7.4 设置报运单id
            extEproduct.setExportId(export.getId());
            /**
             * 7.4 设置商品id
             * 分析：
             *   A. 已知
             *      购销合同的货物id
             *      extCproduct.getContractProductId()
             *   B. 求
             *      出口报运的商品id
             */
            extEproduct.setExportProductId(map.get(extCproduct.getContractProductId()));
            //7.5 保存商品附件
            extEproductDao.insertSelective(extEproduct);
        }


        //8. 保存报运单（3）
        //8.1 设置商品数量
        export.setProNum(cpList.size());
        //8.2 设置附件数量
        export.setExtNum(extCproductList.size());

        //8.3 保存报运单
        exportDao.insertSelective(export);
    }

    @Override
    public void update(Export export) {
        //1. 更新报运单
        exportDao.updateByPrimaryKeySelective(export);

        //2. 更新报运单的商品
        //2.1 获取页面提供的报运单的商品集合
        List<ExportProduct> exportProductList = export.getExportProducts();
        //2.2 判断
        if (exportProductList != null && exportProductList.size()>0){
            for (ExportProduct exportProduct : exportProductList) {
                //2.3 更新报运单商品
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Export> findByPage(ExportExample example, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(exportDao.selectByExample(example));
    }

    @Override
    public void updateExport(ExportResult exportResult) {
        //1. 修改报运单： 状态、备注
        //1.1 根据id查询报运单
        Export export =
                exportDao.selectByPrimaryKey(exportResult.getExportId());
        //1.2 修改报运单状态、备注
        export.setState(exportResult.getState());
        export.setRemark(exportResult.getRemark());
        //1.3 修改保存
        exportDao.updateByPrimaryKeySelective(export);

        //2. 修改报运商品：交税金额
        //2.1 获取电子报运返回的商品
        Set<ExportProductResult> products = exportResult.getProducts();
        //2.2 判断、遍历
        if (products != null && products.size()>0){
            for (ExportProductResult product : products) {
                // 创建要修改的对象（动态sql）
                ExportProduct exportProduct = new ExportProduct();
                // 设置id，作为修改条件
                exportProduct.setId(product.getExportProductId());
                // 设置交税金额
                exportProduct.setTax(product.getTax());
                // 修改保存
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }
}













