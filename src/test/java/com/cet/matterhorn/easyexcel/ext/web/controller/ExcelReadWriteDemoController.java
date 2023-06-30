package com.cet.matterhorn.easyexcel.ext.web.controller;

import com.cet.matterhorn.easyexcel.ext.service.StreamHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @GetMapping(value = "/dynamic/out")
    public void dynamicColumnOutput(HttpServletResponse response) {
        String originalName = "dynamic_column.xlsx";
        String fileName;
        try {
            fileName = URLEncoder.encode(originalName, StandardCharsets.UTF_8.name())
                    .replace("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            response.setStatus(500);
            return;
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        try {
            this.streamHandleService.dynamicHeadOut(response.getOutputStream());
        } catch (IOException e) {
            log.error("output error: {}", e.getMessage());
        }
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
