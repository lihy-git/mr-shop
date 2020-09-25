package com.baidu.shop.web;

import com.baidu.shop.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author lihongyang
 * @Date 2020/9/23
 * @Version V1.0
 **/
@Controller
//@RestController --> @Controller和@ResponseBody --> 不能返回视图了
@RequestMapping(value = "item")
public class PageController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private PageService pageService;

    @GetMapping(value = "/{spuId}.html")
    public String test(@PathVariable(value = "spuId") Integer spuId , ModelMap modelMap, HttpServletRequest httpServletRequest){

        Map<String,Object> map = pageService.getPageInfoBySpuId(spuId);
        modelMap.putAll(map);

        return "item";
    }

}
