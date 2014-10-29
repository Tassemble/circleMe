package com.game.base.commons.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.lf5.util.StreamUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tassemble.common.utils.GsonUtils;

import com.game.base.dao.BaseTestCase;


public class FileUploaderUtilTest extends BaseTestCase{

    
    @Autowired
    FileUploaderUtil fileUploaderUtil;
    
    
    
    @Test
    public void test() throws IOException, Exception {
        File file = new File("/Users/chq/Pictures/2.jpg");
        
        
        
        FileInputStream fis = new FileInputStream(file);
        
        GsonUtils.printJson(fileUploaderUtil.uploadImage(StreamUtils.getBytes(fis), 300, 300));
    }
}
