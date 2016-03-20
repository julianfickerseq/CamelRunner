/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
        HSSFWorkbook workbook = new HSSFWorkbook(in);   
        
        //Get first sheet from the workbook
        HSSFSheet sheet = workbook.getSheetAt(0);
        
        if(importType == ImportType.FORMATTED)
        {
            return marshalAsStructure(sheet);
        }
        else
        {
            return marshalAsArray(sheet);
        }
    }

    public Object marshalAsArray(HSSFSheet sheet)
    {
        ArrayList<ArrayList<Object>> results=new ArrayList<ArrayList<Object>>();
        
        for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext();)
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
                            logger.info(cell.getCellType()+"="+cell.getDateCellValue());
                            newrow.add(cell.getDateCellValue());
                        }
                        else
                        {
                            logger.info(cell.getCellType()+"="+cell.getNumericCellValue());
                            newrow.add(cell.getNumericCellValue());
                        }
                        break;
                    default:
                        logger.info(cell.getCellType()+"="+cell.getStringCellValue());
                        newrow.add(cell.getStringCellValue());
                        break;
                        
                }
                
            }
        }
        return results;  
    }
    
    public Object marshalAsStructure(HSSFSheet sheet)
    {
        ArrayList<HashMap<String,Object>> results=new ArrayList<HashMap<String,Object>>();
        
        ArrayList<String> headers=null;
        
        
        for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext();)
        {
            Row row=rowIterator.next();
        
            if(headers==null)
            {
                headers=new ArrayList<String>();
                for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();)
                {
                    try
                    {                        
                        Cell cell=cellIterator.next();
                        logger.info("Header:"+cell.getStringCellValue());
                        headers.add(cell.getStringCellValue());
                    }
                    catch(Exception e)
                    {
                        logger.error("Unable to decode cell header. Ex="+e.getMessage(),e);
                    }
                }
            }
            else
            {
                HashMap<String,Object> newrow=new HashMap<String,Object>();
                results.add(newrow);                

                for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();)
                {
                    Cell cell=cellIterator.next();
                    logger.info("Cell type:"+cell.getCellType());
                    switch(cell.getCellType())
                    {                    
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                logger.info(cell.getCellType()+"="+cell.getDateCellValue());
                                newrow.put(getCellHeader(headers,cell.getColumnIndex()),cell.getDateCellValue());
                            }
                            else
                            {
                                logger.info(cell.getCellType()+"="+cell.getNumericCellValue());
                                newrow.put(getCellHeader(headers,cell.getColumnIndex()),cell.getNumericCellValue());
                            }
                            break;
                        default:
                            logger.info(cell.getCellType()+"="+cell.getStringCellValue());
                            newrow.put(getCellHeader(headers,cell.getColumnIndex()),cell.getStringCellValue());
                            break;

                    }
                }
            }
        }
        return results;  
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

