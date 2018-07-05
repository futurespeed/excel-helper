package org.fs.excel.parse.event;

import org.fs.excel.parse.ParseContext;

public interface ParseEventHandler {
    void onReady(ParseContext parseContext);

    void onRowRead(ParseContext parseContext);

    void onPageChange(ParseContext parseContext);

    void onFinish(ParseContext parseContext);
}
