/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers.Excel;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.spi.DataFormat;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author snuids
 */

public class ExcelDataFormat implements DataFormat {

    static final Logger logger = LoggerFactory.getLogger("ExcelDataFormat");    

    public enum ImportType 
    {
        ARRAY,FORMATTED 
    }
    
    ImportType importType=ImportType.ARRAY;

    public ImportType getImportType()
    {
        return importType;
    }

    public void setImportType(ImportType importType)
    {
        this.importType = importType;
    }
    
    
    
    public void marshal(Exchange exchng, Object o, OutputStream out) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object unmarshal(Exchange exchng, InputStream in) throws Exception
    {        
        logger.info("Unmarshalling XLS");
        Object res=exchng.getIn().getBody();
        GenericFile genfile=(GenericFile)res;
        
        if(genfile.getFileNameOnly().endsWith("xlsx"))
        {
            return unmarshalXLSX(exchng, in);
    
        }
        HSSFWorkbook workbook = new HSSFWorkbook(in);   
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    
        
        //Get first sheet from the workbook
        HSSFSheet sheet = workbook.getSheetAt(0);
        
        if(importType != ImportType.FORMATTED)
        {
            return marshalAsArray(sheet.iterator());
        }
        else
        {
            OneExcel    excel=new OneExcel();
            for(int i=0;i<workbook.getNumberOfSheets();i++)
            {
                OneExcelSheet onesheet=marshalAsStructure(workbook.getSheetAt(i).iterator(),evaluator);
                logger.info("Loading sheet:"+i);
                logger.info("Data:"+onesheet.data.size());
                if(onesheet.data.size()>0)
                    excel.sheets.add(onesheet);
            }
            logger.info("Total sheets:"+excel.sheets.size());
            
            
            ArrayList<HashMap<String,Object>> resu=excel.GenerateResult();
            HashMap<String,Object> mappings=excel.GenerateMappings();
            
            exchng.getOut().setHeader("mappings", mappings);
            exchng.getOut().setHeader("xlsdata", resu);
            
            return resu;
            
        }
    }
    
    private Object unmarshalXLSX(Exchange exchng, InputStream in) throws Exception
    {
         
        XSSFWorkbook workbook = new XSSFWorkbook(in);   
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
        //Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        if(importType != ImportType.FORMATTED)
        {
            return marshalAsArray(sheet.iterator());
        }
        else
        {
            OneExcel    excel=new OneExcel();
            for(int i=0;i<workbook.getNumberOfSheets();i++)
            {
                OneExcelSheet onesheet=marshalAsStructure(workbook.getSheetAt(i).iterator(),evaluator);
                logger.info("Loading sheet:"+i);
                logger.info("Data:"+onesheet.data.size());
                if(onesheet.data.size()>0)
                    excel.sheets.add(onesheet);
            }
            logger.info("Total sheets:"+excel.sheets.size());
            
            
            ArrayList<HashMap<String,Object>> resu=excel.GenerateResult();
            HashMap<String,Object> mappings=excel.GenerateMappings();
            
            exchng.getOut().setHeader("mappings", mappings);
            exchng.getOut().setHeader("xlsdata", resu);
            
            return resu;
            
        }
    }

    public Object marshalAsArray(Iterator<Row> sheet)
    {
        
        ArrayList<ArrayList<Object>> results=new ArrayList<ArrayList<Object>>();
        
        for (Iterator<Row> rowIterator = sheet; rowIterator.hasNext();)
        {
            ArrayList newrow=new ArrayList();
            results.add(newrow);
            Row row=rowIterator.next();
            
            for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();)
            {
                Cell cell=cellIterator.next();
                logger.info("Cell type:"+cell.getCellType());
                switch(cell.getCellType())
                {                    
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            //logger.info(cell.getCellType()+"="+cell.getDateCellValue());
                            newrow.add(cell.getDateCellValue());
                        }
                        else
                        {
                            //logger.info(cell.getCellType()+"="+cell.getNumericCellValue());
                            newrow.add(cell.getNumericCellValue());
                        }
                        break;
                    default:
                        //logger.info(cell.getCellType()+"="+cell.getStringCellValue());
                        newrow.add(cell.getStringCellValue());
                        break;
                        
                }
                
            }
        }
        return results;  
    }
    
    public OneExcelSheet marshalAsStructure(Iterator<Row> sheet,FormulaEvaluator evaluator)
    {
        logger.info("Evaluating formulas.");
        evaluator.evaluateAll();
        logger.info("Done...");
        OneExcelSheet onesheet=new OneExcelSheet();
        
        ArrayList<String> headers=null;
        
        
        for (Iterator<Row> rowIterator = sheet; rowIterator.hasNext();)
        {
            Row row=rowIterator.next();
        
            if(headers==null)
            {
                headers=new ArrayList<String>();
                int coln=0;
                for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();)
                {
                    try
                    {                        
                        Cell cell=cellIterator.next();
                        logger.info("Header:"+cell.getStringCellValue());
                        String headn=cell.getStringCellValue().replace(" ","");
                        headers.add(headn);
                        OneExcelColumn col=new OneExcelColumn(headn,coln);                        
                        onesheet.columns.add(col);
                    }
                    catch(Exception e)
                    {
                        logger.error("Unable to decode cell header. Ex="+e.getMessage(),e);
                    }
                    coln++;
                }
            }
            else
            {
                ArrayList<Object> newrow=new ArrayList<Object>();
                onesheet.data.add(newrow);                

                int coln=0;
                
                //for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();)
                for(int cn=0; cn<row.getLastCellNum(); cn++)
                {                    
                    Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
                    //Cell cell=cellIterator.next();
                    //logger.info("Cell type:"+cell.getCellType());
                    
                    
                    switch(evaluator.evaluateInCell(cell).getCellType())
                    {                    
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                //logger.info(cell.getCellType()+"="+cell.getDateCellValue());
                                newrow.add(cell.getDateCellValue());
                                if(onesheet.columns.size()>coln)                    
                                    onesheet.columns.get(coln).columnTypes[9]++;
                            }
                            else
                            {
                                //logger.info(cell.getCellType()+"="+cell.getNumericCellValue());
                                newrow.add(cell.getNumericCellValue());
                                if(onesheet.columns.size()>coln)                    
                                    onesheet.columns.get(coln).columnTypes[cell.getCellType()]++;
                            }
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA:                            
                            
                            int value=evaluator.evaluateFormulaCell(cell);
                            value=cell.getCachedFormulaResultType();
                            
                            newrow.add(value);
                            if(onesheet.columns.size()>coln)                    
                                onesheet.columns.get(coln).columnTypes[0]++;
                            break;
                        default:
                            //logger.info(cell.getCellType()+"="+cell.getStringCellValue());
                            
                            String cellstr=new String(cell.getStringCellValue().getBytes(),Charset.forName("UTF-8"));
                            newrow.add(cellstr);
                            if(onesheet.columns.size()>coln)                    
                                onesheet.columns.get(coln).columnTypes[cell.getCellType()]++;
                    
                            break;

                    }
                    coln++;
                }
            }
        }        
        
        return onesheet;  
    }
    
    private String getCellHeader(ArrayList<String> headers, int columnIndex)
    {
        if(columnIndex<headers.size())
        {
            String res=headers.get(columnIndex);
            if(res.isEmpty())
                return "COL"+columnIndex;
            else
                return res;
        }
        else
            return "COL"+columnIndex;
    }
}

