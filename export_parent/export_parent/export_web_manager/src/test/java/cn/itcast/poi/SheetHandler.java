package cn.itcast.poi;

import cn.itcast.vo.ContractProductVo;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 自定义handler对象
 *       实现：XSSFSheetXMLHandler.SheetContentsHandler
 */
public class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    /**
     * 需求：
     * 1）每一行创建一个 vo对象
     * 2）当此行解析结束之后，打印vo对象
     */
    private ContractProductVo vo;

    /**
     * 开始解析某一行
     * @param i 行号
     */
    public void startRow(int i) {
        //实例化vo对象
        if (i>=2) {
            vo = new ContractProductVo();
        }
    }

    /**
     * 此行解析结束
     * @param i 行号
     */
    public void endRow(int i) {
        System.out.println(vo);
    }

    /**
     * 获取当前行，每一个单元个的数据
     * @param cellName    当前单元格名称  (A1,A2,A3)
     * @param cellValue       当前单元格数据
     * @param xssfComment  注释
     */
    public void cell(String cellName, String cellValue, XSSFComment xssfComment) {
        String name = cellName.substring(0,1);
        if(vo != null) {
            switch (name) {
                case "B" :{
                    vo.setCustomName(cellValue);
                    break;
                }
                case "C" :{
                    vo.setContractNo(cellValue);
                    break;
                }
                case "D" :{
                    vo.setProductNo(cellValue);
                    break;
                }
                case "E" :{
                    vo.setCnumber(Integer.parseInt(cellValue));
                    break;
                }
                case "F" :{
                    vo.setFactoryName(cellValue);
                    break;
                }
                case "G" :{
                    try {
                        vo.setDeliveryPeriod(new SimpleDateFormat("yyyy-MM-dd").parse(cellValue) );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "H" :{
                    try {
                        vo.setShipTime(new SimpleDateFormat("yyyy-MM-dd").parse(cellValue) );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "I" :{
                    vo.setTradeTerms(cellValue);
                    break;
                }
                default:{
                    break;
                }
            }
        }
    }
}