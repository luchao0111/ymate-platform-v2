/*
 * Copyright 2007-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.platform.webmvc.view.impl;

import freemarker.template.Configuration;
import net.ymate.platform.core.support.FreemarkerConfigBuilder;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.webmvc.IWebMvc;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.view.AbstractView;
import org.apache.commons.lang.StringUtils;

import java.io.*;

/**
 * Freemarker视图
 *
 * @author 刘镇 (suninformation@163.com) on 2011-10-31 下午08:45:22
 * @version 1.0
 */
public class FreemarkerView extends AbstractView {

    static Configuration __freemarkerConfig;

    String __path;

    public static FreemarkerView bind() {
        return new FreemarkerView();
    }

    public static FreemarkerView bind(String path) {
        return new FreemarkerView(path);
    }

    public static FreemarkerView bind(IWebMvc owner, String path) {
        return new FreemarkerView(owner, path);
    }

    /**
     * 构造器
     *
     * @param owner 所属MVC框架管理器
     * @param path  FTL文件路径
     */
    public FreemarkerView(IWebMvc owner, String path) {
        __doViewInit(owner);
        __path = path;
    }

    public FreemarkerView() {
        __doViewInit(WebContext.getContext().getOwner());
    }

    public FreemarkerView(String path) {
        this(WebContext.getContext().getOwner(), path);
    }

    /**
     * @return 返回当前模板引擎配置对象
     */
    public Configuration getEngineConfig() {
        return __freemarkerConfig;
    }

    @Override
    protected synchronized void __doViewInit(IWebMvc owner) {
        super.__doViewInit(owner);
        // 初始化Freemarker模板引擎配置
        if (__freemarkerConfig == null) {
            try {
                FreemarkerConfigBuilder _builder = FreemarkerConfigBuilder.create();
                if (__baseViewPath.startsWith("/WEB-INF")) {
                    __freemarkerConfig = _builder.addTemplateFileDir(new File(RuntimeUtils.getRootPath(), StringUtils.substringAfter(__baseViewPath, "/WEB-INF/"))).build();
                } else {
                    __freemarkerConfig = _builder.addTemplateFileDir(new File(__baseViewPath)).build();
                }
            } catch (IOException e) {
                throw new Error(RuntimeUtils.unwrapThrow(e));
            }
        }
    }

    protected void __doProcessPath() {
        if (StringUtils.isNotBlank(__contentType)) {
            WebContext.getResponse().setContentType(__contentType);
        }
        if (StringUtils.isBlank(__path)) {
            String _mapping = WebContext.getRequestContext().getRequestMapping();
            if (_mapping.endsWith("/")) {
                _mapping = _mapping.substring(0, _mapping.length() - 1);
            }
            __path = _mapping + ".ftl";
        } else {
            if (__path.startsWith(__baseViewPath)) {
                __path = StringUtils.substringAfter(__path, __baseViewPath);
            }
            if (!__path.endsWith(".ftl")) {
                __path += ".ftl";
            }
        }
    }

    @Override
    protected void __doRenderView() throws Exception {
        __doProcessPath();
        __freemarkerConfig.getTemplate(__path, WebContext.getContext().getLocale()).process(__attributes, WebContext.getResponse().getWriter());
    }

    @Override
    public void render(OutputStream output) throws Exception {
        __doProcessPath();
        __freemarkerConfig.getTemplate(__path, WebContext.getContext().getLocale()).process(__attributes, new BufferedWriter(new OutputStreamWriter(output)));
    }
}
