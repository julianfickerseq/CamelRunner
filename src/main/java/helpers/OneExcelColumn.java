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
public class OneExcelColumn
{
    static final Logger logger = LoggerFactory.getLogger("OneExcelSheet");    

    public String columnName;
    public int[] columnTypes=new int [10];
    public String link="";
    public boolean pKey=false;
    public int columnIndex;
    public HashMap<String,ArrayList<Object>> indexToData=new HashMap<String,ArrayList<Object>>();
    
    public OneExcelColumn(String cellValue,int colIndex)
    {
        String[] vals=cellValue.split("@");
        columnName=vals[0];
        columnIndex=colIndex;
        if(vals.length>1)
        {
            if(vals[1].compareTo("PKEY")==0)
            {
                pKey=true;
            }
            else
                link=vals[1];
        }
    }

    void printDetails()
    {
        StringBuilder res=new StringBuilder();
                        
        res.append("Col:").append(columnName).append(" Ind:").append(columnIndex).append(" Link:").append(link).append(" PKey:").append(pKey).append(" Types:");
        for(int i=0;i<columnTypes.length;i++)
        {
            if(columnTypes[i]>0)
                res.append("[").append(i).append("]").append(columnTypes[i]).append(" ");
        }
        res.append((" Index Size:"+indexToData.size()));
        logger.info(res.toString());
    }
}
