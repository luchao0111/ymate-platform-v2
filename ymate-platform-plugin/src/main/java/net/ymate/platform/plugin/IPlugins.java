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
package net.ymate.platform.plugin;

/**
 * 插件框架模块管理器接口
 *
 * @author 刘镇 (suninformation@163.com) on 15/7/9 下午2:09
 * @version 1.0
 */
public interface IPlugins {

    String MODULE_NAME = "plugin";

    /**
     * @return 获取插件默认工厂配置, 若插件模块被禁用则返回null
     */
    IPluginConfig getConfig();

    /**
     * 通过ID获取默认插件工厂中的插件实例
     *
     * @param id 插件唯一ID
     * @return 返回插件实例
     */
    IPlugin getPlugin(String id);

    /**
     * 通过接口类型获取默认插件工厂中的插件实例
     *
     * @param clazz 插件接口类
     * @param <T>   插件接口类型
     * @return 返回插件实例
     */
    <T> T getPlugin(Class<T> clazz);
}
