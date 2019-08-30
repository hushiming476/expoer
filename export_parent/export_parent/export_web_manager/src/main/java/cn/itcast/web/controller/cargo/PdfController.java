package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Export;
import cn.itcast.domain.cargo.ExportProduct;
import cn.itcast.domain.cargo.ExportProductExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.BeanMapUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class PdfController extends BaseController {
    /**
     * 导出PDF（1） 简单导出
     * http://localhost:8080/cargo/export/exportPdf.do?id=9
     */
    @RequestMapping("/exportPdf1")
    public void exportPdf1() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test02.jasper");

        //2. 创建JasperPrint打印对象，用于往模板中填充数据
        // 参数1： 编译后的jasper文件
        // 参数2： 往模板中填充的map数据
        // 参数3： 往模板中填充的数据源数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), new JREmptyDataSource());

        //3. 导出PDF
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 导出PDF（3） 通过map集合往模板中（parameter）填充数据
     */
    @RequestMapping("/exportPdf3")
    public void exportPdf3() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test03_parameter.jasper");

        // 构建map数据，map的key对应的是jasper模板中parameter参数值。
        Map<String, Object> map = new HashMap<>();
        map.put("userName","纯情的杰哥");
        map.put("email","yuanjie@itcast.cn");
        map.put("companyName","传智播客");
        map.put("deptName","Java教研部");


        //2. 创建JasperPrint打印对象，用于往模板中填充数据
        // 参数1： 编译后的jasper文件
        // 参数2： 往模板中填充的map数据
        // 参数3： 往模板中填充的数据源数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, map, new JREmptyDataSource());

        //3. 导出PDF
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 导出PDF（4） 通过jdbc数据源往jasper模板中填充数据
     */
    @Autowired
    private DataSource dataSource;

    @RequestMapping("/exportPdf4")
    public void exportPdf4() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test04_jdbc.jasper");

        //2. 创建JasperPrint打印对象，用于往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(),dataSource.getConnection());

        //3. 导出PDF
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }


    /**
     * 导出PDF（5） 通过javabean数据源往jasper模板中填充数据
     */
    @RequestMapping("/exportPdf5")
    public void exportPdf5() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test05_javabean.jasper");

        // 构造数据源（相当于调用service获取list集合）
        List<User> list = new ArrayList<>();
        for (int i=0; i<10; i++){
            User user = new User();
            user.setUserName("少杰"+i);
            user.setEmail("jieshao@it.cn"+i);
            user.setCompanyName("浪迹XX"+i);
            user.setDeptName("流浪部"+i);
            list.add(user);
        }

        // 创建javabean数据源对象
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 创建JasperPrint打印对象，用于往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(),dataSource);

        //3. 导出PDF
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 导出PDF（6） 分组报表
     */
    @RequestMapping("/exportPdf6")
    public void exportPdf6() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test06_group.jasper");

        // 构造数据源（相当于调用service获取list集合）
        List<User> list = new ArrayList<>();
        for(int j=0;j<5;j++) {
            for (int i = 0; i < 10; i++) {
                User user = new User();
                user.setUserName("少杰" + i);
                user.setEmail("jieshao@it.cn" + i);
                user.setCompanyName("浪迹XX" + j);
                user.setDeptName("流浪部" + i);
                list.add(user);
            }
        }

        // 创建javabean数据源对象
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 创建JasperPrint打印对象，用于往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(),dataSource);

        //3. 导出PDF
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 导出PDF（7） 画饼  饼图
     */
    @RequestMapping("/exportPdf7")
    public void exportPdf7() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test07_charts.jasper");

        // 构造数据源（相当于调用service获取list集合）
        List<Map<String,Object>> list = new ArrayList<>();
        for(int j=0;j<5;j++) {
            Map<String,Object> map = new HashMap<>();
            map.put("title","企业"+j);
            map.put("value",new Random().nextInt(100));
            list.add(map);
        }

        // 创建javabean数据源对象
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 创建JasperPrint打印对象，用于往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(),dataSource);

        //3. 导出PDF
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }



    /**
     * 导出PDF： 导出报运单信息
     * 请求地址：http://localhost:8080/cargo/export/exportPdf.do?id=9
     * 响应结果：下载export.pdf文件
     */
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;

    @RequestMapping("/exportPdf")
    @ResponseBody
    public void exportPdf(String id) throws Exception {
        //1. 加载export.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/export.jasper");

        //构建数据： map数据，封装报运单数据
        //A. 根据报运单id查询报运单对象
        Export export = exportService.findById(id);
        //B. 把javabean对象，封装为Map
        Map<String, Object> map = BeanMapUtils.beanToMap(export);

        //构建数据： list数据，封装报运单的商品集合
        //A. 根据报运单id，查询商品
        ExportProductExample example = new ExportProductExample();
        example.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> list = exportProductService.findAll(example);
        //B. 创建数据源对象
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);


        //2. 创建JasperPrint打印对象，用于往模板中填充数据(map数据、列表数据)
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, map, dataSource);

        //3. 导出PDF
        //3.1 设置下载的格式与编码： pdf格式、编码
        response.setContentType("application/pdf;charset=UTF-8");

        //3.2 设置下载响应头
        response.setHeader("content-disposition","attachment;filename=export.pdf");
        ServletOutputStream out = response.getOutputStream();

        //3.3 导出
        JasperExportManager.
                exportReportToPdfStream(jasperPrint, out);
        out.close();
    }
}

