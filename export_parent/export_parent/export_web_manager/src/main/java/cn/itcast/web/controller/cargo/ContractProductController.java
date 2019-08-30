package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.FileUploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController{

    // 注入dubbo的服务对象
    // import com.alibaba.dubbo.config.annotation.Reference;
    @Reference(retries = 0)
    private ContractProductService contractProductService;
    @Reference
    private FactoryService factoryService;

    /**
     * 1. 分页查询
     * @param pageNum  当前页，默认1
     * @param pageSize 页大小，默认5
     * @return
     */
    @RequestMapping("/list")
    public String list(String contractId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize){

        //1.1 查询并保存货物的工厂
        //A.构造条件
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        //B.条件查询
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        //C.保存
        request.setAttribute("factoryList",factoryList);

        //1.2 查询购销合同的货物列表
        ContractProductExample cpExample = new ContractProductExample();
        cpExample.createCriteria().andContractIdEqualTo(contractId);
        PageInfo<ContractProduct> pageInfo =
                contractProductService.findByPage(cpExample, pageNum, pageSize);
        request.setAttribute("pageInfo",pageInfo);

        //1.3 存储购销合同id  (注意这里，一定要保存)
        request.setAttribute("contractId",contractId);

        //1.4 进入货物列表和添加页面
        return "cargo/product/product-list";
    }

    /**
     * 2. 添加或者修改
     */
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @RequestMapping("/edit")
    public String edit(ContractProduct contractProduct, MultipartFile productPhoto) throws Exception {
        // 设置购销合同货物所属企业信息
        contractProduct.setCompanyId(getLoginCompanyId());
        contractProduct.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(contractProduct.getId())){
            // 上传图片到七牛云
            if (productPhoto != null) {
                String img = "http://" +fileUploadUtil.upload(productPhoto);
                contractProduct.setProductImage(img);
            }
            // 添加
            contractProductService.save(contractProduct);
        }
        else {
            // 修改
            contractProductService.update(contractProduct);
        }
        // 进入货物列表：传入购销合同id
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractProduct.getContractId();
    }

    /**
     * 3. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //3.1 查询货物的工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);

        //3.2 根据货物id查询
        ContractProduct contractProduct = contractProductService.findById(id);
        request.setAttribute("contractProduct",contractProduct);
        return "cargo/product/product-update";
    }

    /**
     * 4. 删除货物
     * @param id 货物id
     * @param contractId 购销合同id
     * @return
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId){
        // 删除货物
        contractProductService.delete(id);
        // 进入货物列表：传入购销合同id
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractId;
    }

    /**
     * 5. 上传货物（1）进入货物上传页面
     * 请求地址：http://localhost:8080/cargo/contractProduct/toImport.do?contractId=1
     */
    @RequestMapping("/toImport")
    public String toImport(String contractId){
        request.setAttribute("contractId",contractId);
        return "cargo/product/product-import";
    }

    /**
     * 5. 上传货物（2）上传excel，导入excel数据到数据库
     * 请求地址：/cargo/contractProduct/import.do
     * 请求参数：
     *     A. 普通参数：contractId
     *     B. 页面文件域： <input type="file" name="file">
     */
    @RequestMapping("/import")
    public String importExcel(String contractId,MultipartFile file) throws Exception {
        //1. 创建工作簿
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        //2. 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        //3. 获取工作表的总行数
        int totalRow = sheet.getPhysicalNumberOfRows();

        //4. 遍历每一行，把读取的每一行封装为货物对象。最后保存货物。
        //4.1 从第二行开始遍历
        for (int i=1; i<totalRow; i++){
            //4.2 获取每一行
            Row row = sheet.getRow(i);
            //4.3 创建货物对象
            ContractProduct cp = new ContractProduct();
            //4.4 读取单元格信息，封装到货物对象中。
            // 生产厂家	货号	数量	包装单位(PCS/SETS)	装率	箱数	单价	货物描述	要求
            cp.setFactoryName(row.getCell(1).getStringCellValue());
            cp.setProductNo(row.getCell(2).getStringCellValue());
            cp.setCnumber((int) row.getCell(3).getNumericCellValue());
            cp.setPackingUnit(row.getCell(4).getStringCellValue());
            cp.setLoadingRate(row.getCell(5).getNumericCellValue() + "");
            cp.setBoxNum((int) row.getCell(6).getNumericCellValue());
            cp.setPrice(row.getCell(7).getNumericCellValue());
            cp.setProductDesc(row.getCell(8).getStringCellValue());
            cp.setProductRequest(row.getCell(9).getStringCellValue());
            //4.5 根据工厂名称查询获取并设置工厂id
            FactoryExample factoryExample = new FactoryExample();
            factoryExample.createCriteria().andFactoryNameEqualTo(cp.getFactoryName());
            List<Factory> list = factoryService.findAll(factoryExample);
            if (list != null && list.size()>0){
                cp.setFactoryId(list.get(0).getId());
            }
            //4.6 设置货物所属企业
            cp.setCompanyId(getLoginCompanyId());
            cp.setCompanyName(getLoginCompanyName());
            //4.8 设置购销合同id
            cp.setContractId(contractId);

            //4.7 保存货物
            contractProductService.save(cp);
        }

        // 上传成功，保存数据
        request.setAttribute("contractId",contractId);
        // 上传成功，回到当前页面
        return "cargo/product/product-import";
    }
}
