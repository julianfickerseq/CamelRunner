/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers.JSHelper;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *
 * @author snuids
 */
public class JSHelper implements Processor
{
    private final Map<String, Object> cache=new HashMap<String, Object>();
    
    public void process(Exchange exchng) throws Exception
    {
        exchng.getIn().setHeader("jsHelper", this);
    }
    
    /**
     * **** cache method *****
     */
    public void cache(String key, Object obj) {
        this.cache.put(key, obj);
    }

    public Object cache(String key) {
        return this.cache.get(key);
    }
}
