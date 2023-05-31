package com.jinhua.easyexcel.ext.web.controller;

import com.jinhua.easyexcel.ext.service.StreamHandleService;
import com.jinhua.easyexcel.ext.web.controller.dto.DynamicColumnDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@RestController
@RequestMapping(value = "/excel")
public class ExcelReadWriteDemoController {

    private StreamHandleService streamHandleService;

    @PostMapping(value = "/dynamic/in")
    public void dynamicColumnInput(@RequestParam(value = "file") MultipartFile multipartFile) {
        try {
            this.streamHandleService.dynamicHeadIn(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("analyze error: {}", e.getMessage());
        }
    }

    @PostMapping(value = "/dynamic/out")
    public DynamicColumnDTO dynamicColumnOutput(HttpServletResponse response) {
        this.streamHandleService.dynamicHeadOut(response);
        return null;
    }

    @PostMapping(value = "/complex-head/in")
    public void complexHeadInput(@RequestParam(value = "file") MultipartFile multipartFile) {
        try {
            this.streamHandleService.complexHeadIn(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("analyze error: {}", e.getMessage());
        }
    }

    @Autowired
    public void setStreamHandleService(StreamHandleService streamHandleService) {
        this.streamHandleService = streamHandleService;
    }
}
