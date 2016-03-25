/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author snuids
 */
public class OneExcel
{
    static final Logger logger = LoggerFactory.getLogger("OneExcel");    

    public ArrayList<OneExcelSheet> sheets=new ArrayList<OneExcelSheet>();
    
    public void printDetails()
    {
        logger.info("Sheets:"+sheets.size());
        for(OneExcelSheet she:sheets)
        {
            she.printDetails();
        }        
    }
    
    public ArrayList<HashMap<String,Object>> GenerateResult()
    {
        logger.info("Merging sheets");
        for(OneExcelSheet she:sheets)
        {
            she.prepareMerge();
        }
        printDetails();
        ArrayList<HashMap<String,Object>> results=new ArrayList<HashMap<String,Object>>();
        if(sheets.size()>0)
        {
            OneExcelSheet firstsheet=sheets.get(0);
            for(ArrayList row:firstsheet.data)
            {
                //logger.info("New row");
                HashMap<String,Object> newrow=new HashMap<String, Object>();
                int coln=0;
                for(Object cell:row)
                {
                    if(firstsheet.columns.size()>coln)
                    {
                        OneExcelColumn curcol=firstsheet.columns.get(coln);
                        
                        //logger.info("Col:"+curcol.columnName+"Data:"+cell.toString());
                        if(!newrow.containsKey(curcol.columnName))
                        {
                            newrow.put(curcol.columnName, cell);
                        }
                        if(curcol.link.length()>0)
                        {
                            HashMap<Integer,Integer> tabousheets=new HashMap<Integer,Integer>();
                            tabousheets.put(0,1);
                            AddColumnsFromLink(curcol.link,cell.toString(),tabousheets,newrow);
                        }                        
                    }
                    coln++;
                } 
                results.add(newrow);
            }
        }
        return results;
    }

    private void AddColumnsFromLink(String link, String currentValue,HashMap<Integer, Integer> tabousheets, HashMap<String, Object> newrow)
    {
        //logger.info("Looking for table with link:"+link);
        int sheetind=0;
        for(OneExcelSheet she:sheets)
        {
            if(tabousheets.containsKey(sheetind))
            {
                sheetind++;
                continue;
            }
            
            if(she.links.containsKey(link))
            {
                OneExcelColumn ncol=she.links.get(link);
                if(ncol.indexToData.containsKey(currentValue))
                {
                    ArrayList ndata=ncol.indexToData.get(currentValue);
                    int coln=0;
                    for(Object cell:ndata)
                    {
                        if(she.columns.size()>coln)
                        {
                            String colname=she.columns.get(coln).columnName;
                            OneExcelColumn curcol=she.columns.get(coln);
                            //logger.info("Adding Col:"+colname+"Data:"+cell.toString());
                            if(!newrow.containsKey(colname))
                            {
                                newrow.put(colname, cell);
                            }
                            if(curcol.link.length()>0)
                            {
                                tabousheets.put(sheetind,1);
                                AddColumnsFromLink(curcol.link,cell.toString(),tabousheets,newrow);
                            }  
                        }
                        coln++;
                    } 
                }
            }
            
            sheetind++;
        }      
    }
}
