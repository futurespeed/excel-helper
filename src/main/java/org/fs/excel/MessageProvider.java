package org.fs.excel;

import java.util.Properties;

public class MessageProvider {
    private Properties properties;

    public MessageProvider(String path){
        if(null == path){
            path = "/org/fs/excel/message.properties";
        }
        try {
            properties = new Properties();
            properties.load(MessageProvider.class.getResourceAsStream(path));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
