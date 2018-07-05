package org.fs.excel.parse.event;

import org.fs.excel.parse.ParseContext;

public class ParseEventAdapter implements ParseEventHandler {
    public void onReady(ParseContext parseContext){}

    public void onRowRead(ParseContext parseContext){}

    public void onPageChange(ParseContext parseContext){}

    public void onFinish(ParseContext parseContext){}
}
