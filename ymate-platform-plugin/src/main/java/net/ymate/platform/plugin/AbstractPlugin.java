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
 * 插件启动器接口抽象实现，完成必要参数的赋值动作
 *
 * @author 刘镇 (suninformation@163.com) on 2012-4-20 下午5:30:30
 * @version 1.0
 */
public abstract class AbstractPlugin implements IPlugin {

    private IPluginContext __context;

    private boolean __inited;

    private boolean __started;

    @Override
    public void init(IPluginContext context) throws Exception {
        this.__context = context;
        this.__inited = true;
    }

    @Override
    public IPluginContext getPluginContext() {
        return __context;
    }

    @Override
    public boolean isInited() {
        return __inited;
    }

    @Override
    public boolean isStarted() {
        return __started;
    }

    @Override
    public void startup() throws Exception {
        if (__inited) {
            this.__started = true;
        }
    }

    @Override
    public void shutdown() throws Exception {
        if (__started) {
            this.__started = false;
        }
    }

    @Override
    public void destroy() throws Exception {
        if (__inited) {
            shutdown();
            //
            this.__inited = false;
            this.__context = null;
        }
    }
}
