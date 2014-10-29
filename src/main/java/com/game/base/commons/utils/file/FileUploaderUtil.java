package com.game.base.commons.utils.file;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.game.base.commons.domain.ImageUploadReponseDto;
import com.game.utils.HttpClientUtils;
import com.game.utils.HttpDataProvider;


@Component
public class FileUploaderUtil {
      

    @Autowired
    HttpClientUtils httpClientUtils;
    
    public ImageUploadReponseDto uploadImage(final byte[] datas, final int imageLength, final int imageWidth) throws Exception {
        
        String result = HttpClientUtils.getHtmlByPostMethod(httpClientUtils.getCommonHttpManager(), new HttpDataProvider() {
            
            @Override
            public String getUrl() {
                return String.format("http://upload.icourse163.org/api/photoUpload2.do?_upload_mth_=sync_parse&sitefrom=study&responsetype=xml&userdefinesize=%sx%sx1", 
                                     imageLength, imageWidth);
            }
            
            @Override
            public HttpEntity getHttpEntity() {
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("Filedata", new ByteArrayBody(datas, "image/jpeg", "filename"));
                return entity;
            }
            
            @Override
            public List<Header> getHeaders() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new InputSource(new ByteArrayInputStream(result.getBytes("UTF-8"))));
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/netease/result/code/text()");
        String value = (String)expr.evaluate(doc, XPathConstants.STRING);
        if (value.equals("999")) {
            ImageUploadReponseDto dto = new ImageUploadReponseDto();
            expr = xpath.compile("/netease/userDef1Url/text()");
            dto.setUrl((String)expr.evaluate(doc, XPathConstants.STRING));
            dto.setCode(200);
            return dto;
        }
        
        ImageUploadReponseDto dto = new ImageUploadReponseDto();
        dto.setCode(-1);
        
        return dto;
    }
}
