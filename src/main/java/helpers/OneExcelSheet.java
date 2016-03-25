/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author snuids
 */
public class OneExcelSheet
{
    static final Logger logger = LoggerFactory.getLogger("OneExcelSheet");    

    public ArrayList<ArrayList<Object>> data=new ArrayList<ArrayList<Object>>();
    public ArrayList<OneExcelColumn> columns=new ArrayList<OneExcelColumn>();    
    public HashMap<String,OneExcelColumn> links=new HashMap<String,OneExcelColumn>();
    
    public void printDetails()
    {
        logger.info("Columns:"+columns.size());
        for(OneExcelColumn col:columns)
        {
            col.printDetails();
        }
        
    }

    void prepareMerge()
    {
        for(OneExcelColumn col:columns)
        {
            if(col.link.length()>0)
            {
                links.put(col.link,col);
                for(int row=0;row<data.size();row++)
                {
                    ArrayList currow=data.get(row);
                    if(currow.size()>col.columnIndex)
                    {                        
                        if(!col.indexToData.containsKey(currow.get(col.columnIndex).toString()))
                        {
                            col.indexToData.put(currow.get(col.columnIndex).toString(),currow);
                        }
                    }
                        
                }
            }
        }
    }
}
