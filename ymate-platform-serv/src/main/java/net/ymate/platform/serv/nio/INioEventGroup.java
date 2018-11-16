/*
 * Copyright 2007-2018 the original author or authors.
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
package net.ymate.platform.serv.nio;

import net.ymate.platform.serv.IEventGroup;
import net.ymate.platform.serv.IListener;
import net.ymate.platform.serv.nio.support.NioEventProcessor;

import java.nio.channels.SelectionKey;

/**
 * @author 刘镇 (suninformation@163.com) on 2018/11/16 1:40 AM
 * @version 1.0
 */
public interface INioEventGroup<LISTENER extends IListener<INioSession>> extends IEventGroup<INioCodec, LISTENER, INioSession> {

    NioEventProcessor processor(SelectionKey key);

    NioEventProcessor processor();
}
