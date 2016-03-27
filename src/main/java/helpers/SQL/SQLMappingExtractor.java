/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers.SQL;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author snuids
 */
public class SQLMappingExtractor implements Processor
{
    static final Logger logger = LoggerFactory.getLogger("ExcelDataFormat");    

    public void process(Exchange exchng) throws Exception
    {
        logger.info("Computing SQL mappings");
        ArrayList rows=(ArrayList)exchng.getIn().getBody();
        
        HashMap<String,Object>  mappings=new HashMap<String,Object>();
        HashMap<String,HashMap<String,Integer>>  mappingTypes=new HashMap<String,HashMap<String,Integer>>();
        
        if(rows.size()>0)
        {
            HashMap<String,Object> row1=(HashMap<String,Object>)rows.get(0);
            for(Iterator<String> heait=row1.keySet().iterator();heait.hasNext();)
            {
                String hea=heait.next();
                //logger.info("Key="+hea);
                mappingTypes.put(hea, new HashMap<String,Integer>());
            }
            for(int i=0;i<rows.size();i++)
            {
                for(Iterator<String> heait=row1.keySet().iterator();heait.hasNext();)
                {
                    String hea=heait.next();
                    //logger.info("Key="+hea);
                    Object obj=row1.get(hea);
                    if(obj!=null)
                    {
                        //logger.info("Object="+obj+" Type="+obj.getClass());
                        if(!mappingTypes.get(hea).containsKey(obj.getClass().toString()))
                            mappingTypes.get(hea).put(obj.getClass().toString(), new Integer(1));
                        else
                        {
                            mappingTypes.get(hea).put(obj.getClass().toString()
                                        , mappingTypes.get(hea).get(obj.getClass().toString())+1);
                        }
                    }
                    
                }
            }
            for(Iterator<String> heait=mappingTypes.keySet().iterator();heait.hasNext();)
            {
                String hea=heait.next();
                //logger.info("Key="+hea);
                HashMap<String,Integer> onehea=mappingTypes.get(hea);
                String besttype="java.lang.String";
                int besttypeval=-1;
                for(Iterator<String> typeit=onehea.keySet().iterator();typeit.hasNext();)
                {   
                    String heatyp=typeit.next();
                    //logger.info("Key="+heatyp+" Value="+onehea.get(heatyp));    
                    if(onehea.get(heatyp)>besttypeval) {
                            
                        besttypeval=onehea.get(heatyp);
                        besttype=heatyp;
                    }
                }
                if(besttype.indexOf("Long")>0 || besttype.indexOf("Integer")>0)
                {
                    HashMap<String, Object> newmap0=new HashMap<String, Object>();
                    newmap0.put("type", "long");
                    mappings.put(hea,newmap0);
                }
                else if(besttype.indexOf("Float")>0 )
                {
                    HashMap<String, Object> newmap0=new HashMap<String, Object>();
                    newmap0.put("type", "float");
                    mappings.put(hea,newmap0);
                }
                else if(besttype.indexOf("Double")>0 )
                {
                    HashMap<String, Object> newmap0=new HashMap<String, Object>();
                    newmap0.put("type", "double");
                    mappings.put(hea,newmap0);
                }
                else if(besttype.indexOf("Boolean")>0 )
                {
                    HashMap<String, Object> newmap0=new HashMap<String, Object>();
                    newmap0.put("type", "boolean");
                    mappings.put(hea,newmap0);
                }
                else if(besttype.indexOf("Timestamp")>0 )
                {
                    HashMap<String, Object> newmap0=new HashMap<String, Object>();
                    newmap0.put("type", "date");
                    newmap0.put("format","strict_date_optional_time||epoch_millis");
                    mappings.put(hea,newmap0);
                }
                else
                {
                    HashMap<String, Object> newmap0=new HashMap<String, Object>();
                    newmap0.put("type", "string");
                    newmap0.put("index", "not_analyzed");    
                    mappings.put(hea,newmap0);
                }
                
            }
            /*for(Iterator<String> mapit=mappings.keySet().iterator();mapit.hasNext();)
            {
                String maptyp=mapit.next();
                logger.info("Hea="+maptyp+" Value="+mappings.get(maptyp));    
            }*/
        }
        exchng.getOut().setHeaders(exchng.getIn().getHeaders());
        exchng.getOut().setHeader("mappings", mappings);
        exchng.getOut().setHeader("xlsdata", rows);

    }
    
}
