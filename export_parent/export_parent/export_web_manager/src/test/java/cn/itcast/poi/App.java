package cn.itcast.poi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class App {
    // 导出excel
    @Test
    public void write() throws Exception {
        //1. 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        //2. 创建工作表
        Sheet sheet = workbook.createSheet();
        //3. 创建第一行, 从0开始
        Row row = sheet.createRow(0);
        //4. 创建单元格, 创建第一行的第一列
        Cell cell = row.createCell(0);
        /**
         * 给单元格设置样式（了解）
         */
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderLeft(BorderStyle.DASHED);
        cellStyle.setBorderBottom(BorderStyle.DASHED);
        cellStyle.setBorderRight(BorderStyle.DASHED);
        cellStyle.setBorderTop(BorderStyle.DASHED);
        cell.setCellStyle(cellStyle);

        //5. 设置单元格内容
        cell.setCellValue("第一行的第一列");
        //6. 导出excel到指定路径
        workbook.write(new FileOutputStream("E:\\test.xlsx"));
        //7.关闭
        workbook.close();
    }

    // 导入excel
    @Test
    public void read() throws Exception {
        //1. 创建工作簿
        Workbook workbook = new XSSFWorkbook(new FileInputStream("E:\\test.xlsx"));
        //2. 获取工作表
        Sheet sheet = workbook.getSheetAt(0);
        //3. 获取第一行
        Row row = sheet.getRow(0);
        //4. 获取第一行的第一列
        Cell cell = row.getCell(0);
        //5. 测试
        System.out.println(cell);
        System.out.println("获取总行数：" + sheet.getPhysicalNumberOfRows());
        System.out.println("获取总列数：" + row.getPhysicalNumberOfCells());

        //关闭释放资源
        workbook.close();
    }
}
