package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.vo.ContractProductVo;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.DownloadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/cargo/contract")
public class OutProductController extends BaseController{

    @Reference
    private ContractProductService contractProductService;

    /**
     * 进入出货表导出页面
     * 请求地址：http://localhost:8080/cargo/contract/print.do
     */
    @RequestMapping("/print")
    public String print(){
        return "cargo/print/contract-print";
    }

    /**
     * 导出出货表（1）传统方式实现：XSSF
     * 请求地址：http://localhost:8080/cargo/contract/printExcel.do?inputDate=2015-06
     */
    @RequestMapping("/printExcel1")
    public void printExcel1(String inputDate) throws Exception {
        //第一步：导出第一行
        //A. 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        //B. 创建工作表
        Sheet sheet = workbook.createSheet("出货表");
        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0,0,1,8));
        // 设置列宽
        sheet.setColumnWidth(0,256*6);
        sheet.setColumnWidth(1,256*26);
        sheet.setColumnWidth(2,256*11);
        sheet.setColumnWidth(3,256*29);
        sheet.setColumnWidth(4,256*12);
        sheet.setColumnWidth(5,256*15);
        sheet.setColumnWidth(6,256*10);
        sheet.setColumnWidth(7,256*10);
        sheet.setColumnWidth(8,256*10);

        //C. 创建第一行： 标题行  2012年8月份出货表
        Row row = sheet.createRow(0);
        // 设置行高
        row.setHeightInPoints(36);
        //D. 创建第一行的第二列（合并单元格）
        Cell cell = row.createCell(1);
        // 构建标题: 2019-06  2019-6
        String bigTitle = inputDate.replaceAll("-0","-").replaceAll("-","年") + "月份出货表";
        cell.setCellValue(bigTitle);
        // 设置单元格样式
        cell.setCellStyle(this.bigTitle(workbook));

        //第二步：导出第二行
        String[] titles = {"客户","订单号","货号","数量","工厂","工厂交期","	船期","贸易条款"};
        row = sheet.createRow(1);
        // 行高
        row.setHeightInPoints(26.3f);
        // 创建第二行的每一列
        for (int i=0; i<titles.length; i++){
            cell = row.createCell(i+1);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(this.title(workbook));
        }

        //第三步：导出数据行
        List<ContractProductVo> list =
                contractProductService.findByShipTime(inputDate, getLoginCompanyId());
        if (list != null && list.size()>0){
            int index = 2;
            for (ContractProductVo cp : list) {
                // 创建数据行，从第三行开始
                row = sheet.createRow(index++);
                // 行高
                row.setHeightInPoints(24);

                cell = row.createCell(1);
                cell.setCellValue(cp.getCustomName());
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(2);
                cell.setCellValue(cp.getContractNo());
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(3);
                cell.setCellValue(cp.getProductNo());
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(4);
                if (cp.getCnumber() != null){
                    cell.setCellValue(cp.getCnumber());
                }else{
                    cell.setCellValue(0);
                }
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(5);
                cell.setCellValue(cp.getFactoryName());
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(6);
                cell.setCellValue(cp.getDeliveryPeriod());
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(7);
                cell.setCellValue(cp.getShipTime());
                cell.setCellStyle(this.text(workbook));

                cell = row.createCell(8);
                cell.setCellValue(cp.getTradeTerms());
                cell.setCellStyle(this.text(workbook));
            }
        }

        //第四步：导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        // 构造缓冲流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 把excel文件流写入缓存流。  excel文件流--->缓冲流---->response输出流
        workbook.write(bos);
        // 下载
        downloadUtil.download(bos,response,"出货表.xlsx");
        workbook.close();

    }

    /**
     * 导出出货表（2）模板导出。先读取一个制作好的excel样式的模板，再设置数据。
     */
    @RequestMapping("/printExcel2")
    public void printExcel2(String inputDate) throws Exception {
        //第一步：导出第一行
        // 获取excel文件流
        InputStream in =
                session.getServletContext()
                        .getResourceAsStream("/make/xlsprint/tOUTPRODUCT.xlsx");
        //A. 创建工作簿
        Workbook workbook = new XSSFWorkbook(in);
        //B. 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        //C. 获取第一行： 标题行  2012年8月份出货表
        Row row = sheet.getRow(0);
        //D. 获取第一行的第二列（合并单元格）
        Cell cell = row.getCell(1);
        // 构建标题: 2019-06  2019-6
        String bigTitle = inputDate.replaceAll("-0","-").replaceAll("-","年") + "月份出货表";
        cell.setCellValue(bigTitle);


        //第二步：获取第三行
        row = sheet.getRow(2);
        CellStyle[] cellStyles = new CellStyle[8];
        // 获取第三行的每一列
        for (int i=0; i<8; i++){
            cell = row.getCell(i+1);
            // 获取单元格的样式
            cellStyles[i] = cell.getCellStyle();
        }


        //第三步：导出数据行
        List<ContractProductVo> list =
                contractProductService.findByShipTime(inputDate, getLoginCompanyId());
        if (list != null && list.size()>0){
            int index = 2;
            for (ContractProductVo cp : list) {
                // 创建数据行，从第三行开始
                row = sheet.createRow(index++);

                cell = row.createCell(1);
                cell.setCellValue(cp.getCustomName());
                cell.setCellStyle(cellStyles[0]);

                cell = row.createCell(2);
                cell.setCellValue(cp.getContractNo());
                cell.setCellStyle(cellStyles[1]);

                cell = row.createCell(3);
                cell.setCellValue(cp.getProductNo());
                cell.setCellStyle(cellStyles[2]);

                cell = row.createCell(4);
                if (cp.getCnumber() != null){
                    cell.setCellValue(cp.getCnumber());
                }else{
                    cell.setCellValue(0);
                }
                cell.setCellStyle(cellStyles[3]);

                cell = row.createCell(5);
                cell.setCellValue(cp.getFactoryName());
                cell.setCellStyle(cellStyles[4]);

                cell = row.createCell(6);
                cell.setCellValue(cp.getDeliveryPeriod());
                cell.setCellStyle(cellStyles[5]);

                cell = row.createCell(7);
                cell.setCellValue(cp.getShipTime());
                cell.setCellStyle(cellStyles[6]);

                cell = row.createCell(8);
                cell.setCellValue(cp.getTradeTerms());
                cell.setCellStyle(cellStyles[7]);
            }
        }

        //第四步：导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        // 构造缓冲流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 把excel文件流写入缓存流。  excel文件流--->缓冲流---->response输出流
        workbook.write(bos);
        // 下载
        downloadUtil.download(bos,response,"出货表.xlsx");
        workbook.close();

    }

    /**
     * 导出出货表（3）SXSSF 导出百万数据
     */
    @RequestMapping("/printExcel")
    public void printExcel(String inputDate) throws Exception {
        //第一步：导出第一行
        //A. 创建工作簿
        Workbook workbook = new SXSSFWorkbook();
        //B. 创建工作表
        Sheet sheet = workbook.createSheet("出货表");
        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0,0,1,8));
        // 设置列宽
        sheet.setColumnWidth(0,256*6);
        sheet.setColumnWidth(1,256*26);
        sheet.setColumnWidth(2,256*11);
        sheet.setColumnWidth(3,256*29);
        sheet.setColumnWidth(4,256*12);
        sheet.setColumnWidth(5,256*15);
        sheet.setColumnWidth(6,256*10);
        sheet.setColumnWidth(7,256*10);
        sheet.setColumnWidth(8,256*10);

        //C. 创建第一行： 标题行  2012年8月份出货表
        Row row = sheet.createRow(0);
        // 设置行高
        row.setHeightInPoints(36);
        //D. 创建第一行的第二列（合并单元格）
        Cell cell = row.createCell(1);
        // 构建标题: 2019-06  2019-6
        String bigTitle = inputDate.replaceAll("-0","-").replaceAll("-","年") + "月份出货表";
        cell.setCellValue(bigTitle);
        // 设置单元格样式
        cell.setCellStyle(this.bigTitle(workbook));

        //第二步：导出第二行
        String[] titles = {"客户","订单号","货号","数量","工厂","工厂交期","	船期","贸易条款"};
        row = sheet.createRow(1);
        // 行高
        row.setHeightInPoints(26.3f);
        // 创建第二行的每一列
        for (int i=0; i<titles.length; i++){
            cell = row.createCell(i+1);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(this.title(workbook));
        }

        //第三步：导出数据行
        List<ContractProductVo> list =
                contractProductService.findByShipTime(inputDate, getLoginCompanyId());
        if (list != null && list.size()>0){
            int index = 2;
            for (ContractProductVo cp : list) {
                for(int j=0; j<7000;j++) {
                    // 创建数据行，从第三行开始
                    row = sheet.createRow(index++);
                    // 行高
                    row.setHeightInPoints(24);

                    cell = row.createCell(1);
                    cell.setCellValue(cp.getCustomName());
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(2);
                    cell.setCellValue(cp.getContractNo());
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(3);
                    cell.setCellValue(cp.getProductNo());
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(4);
                    if (cp.getCnumber() != null) {
                        cell.setCellValue(cp.getCnumber());
                    } else {
                        cell.setCellValue(0);
                    }
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(5);
                    cell.setCellValue(cp.getFactoryName());
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(6);
                    cell.setCellValue(cp.getDeliveryPeriod());
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(7);
                    cell.setCellValue(cp.getShipTime());
//                    cell.setCellStyle(this.text(workbook));

                    cell = row.createCell(8);
                    cell.setCellValue(cp.getTradeTerms());
//                    cell.setCellStyle(this.text(workbook));
                }
            }
        }

        //第四步：导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        // 构造缓冲流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 把excel文件流写入缓存流。  excel文件流--->缓冲流---->response输出流
        workbook.write(bos);
        // 下载
        downloadUtil.download(bos,response,"出货表.xlsx");
        workbook.close();

    }



    //大标题的样式
    public CellStyle bigTitle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)16);
        font.setBold(true);//字体加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);				//横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);		//纵向居中
        return style;
    }

    //小标题的样式
    public CellStyle title(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short)12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);				//横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);		//纵向居中
        style.setBorderTop(BorderStyle.THIN);						//上细线
        style.setBorderBottom(BorderStyle.THIN);					//下细线
        style.setBorderLeft(BorderStyle.THIN);						//左细线
        style.setBorderRight(BorderStyle.THIN);						//右细线
        return style;
    }

    //文字样式
    public CellStyle text(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short)10);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);				//横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);		//纵向居中
        style.setBorderTop(BorderStyle.THIN);						//上细线
        style.setBorderBottom(BorderStyle.THIN);					//下细线
        style.setBorderLeft(BorderStyle.THIN);						//左细线
        style.setBorderRight(BorderStyle.THIN);						//右细线

        return style;
    }

}
