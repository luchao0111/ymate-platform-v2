/*
 * Copyright 2007-2107 the original author or authors.
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
package net.ymate.platform.core.beans.intercept;

import net.ymate.platform.core.beans.annotation.After;
import net.ymate.platform.core.beans.annotation.Before;
import net.ymate.platform.core.beans.annotation.Clean;
import net.ymate.platform.core.beans.annotation.Proxy;
import net.ymate.platform.core.beans.proxy.IProxy;
import net.ymate.platform.core.beans.proxy.IProxyChain;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拦截器代理，支持@Before和@After方法注解
 *
 * @author 刘镇 (suninformation@163.com) on 15/5/19 下午12:01
 * @version 1.0
 */
@Proxy
public class InterceptProxy implements IProxy {

    private static Map<Class<?>, List<Class<? extends IInterceptor>>> __beforeInterceptsCache;

    private static Map<Class<?>, List<Class<? extends IInterceptor>>> __afterInterceptsCache;

    private static final Object __beforeCacheLocker = new Object();

    private static final Object __afterCacheLocker = new Object();

    public InterceptProxy() {
        __beforeInterceptsCache = new ConcurrentHashMap<Class<?>, List<Class<? extends IInterceptor>>>();
        __afterInterceptsCache = new ConcurrentHashMap<Class<?>, List<Class<? extends IInterceptor>>>();
    }

    public Object doProxy(IProxyChain proxyChain) throws Throwable {
        // 尝试处理@Before注解
        if (proxyChain.getTargetClass().isAnnotationPresent(Before.class)
                || proxyChain.getTargetMethod().isAnnotationPresent(Before.class)) {

            InterceptContext _context = new InterceptContext(IInterceptor.Direction.BEFORE,
                    proxyChain.getProxyFactory().getOwner(),
                    proxyChain.getTargetObject(),
                    proxyChain.getTargetMethod(),
                    proxyChain.getMethodParams());
            //
            for (Class<? extends IInterceptor> _interceptClass : __doGetBeforeIntercepts(proxyChain.getTargetClass(), proxyChain.getTargetMethod())) {
                IInterceptor _interceptor = _interceptClass.newInstance();
                // 执行前置拦截器，若其结果对象不为空则返回并停止执行
                Object _resultObj = _interceptor.intercept(_context);
                if (_resultObj != null) {
                    return _resultObj;
                }
            }
        }
        // 若前置拦截器未返回结果，则正常执行目标方法
        Object _returnValue = proxyChain.doProxyChain();
        // 尝试处理@After注解
        if (proxyChain.getTargetClass().isAnnotationPresent(After.class)
                || proxyChain.getTargetMethod().isAnnotationPresent(After.class)) {

            // 初始化拦截器上下文对象，并将当前方法的执行结果对象赋予后置拦截器使用
            InterceptContext _context = new InterceptContext(IInterceptor.Direction.AFTER,
                    proxyChain.getProxyFactory().getOwner(),
                    proxyChain.getTargetObject(),
                    proxyChain.getTargetMethod(),
                    proxyChain.getMethodParams());
            _context.setResultObject(_returnValue);
            //
            for (Class<? extends IInterceptor> _interceptClass : __doGetAfterIntercepts(proxyChain.getTargetClass(), proxyChain.getTargetMethod())) {
                IInterceptor _interceptor = _interceptClass.newInstance();
                // 执行后置拦截器，所有后置拦截器的执行结果都将被忽略
                _interceptor.intercept(_context);
            }
        }
        return _returnValue;
    }

    private List<Class<? extends IInterceptor>> __doGetBeforeIntercepts(Class<?> targetClass, Method targetMethod) {
        if (__beforeInterceptsCache.containsKey(targetClass)) {
            return __beforeInterceptsCache.get(targetClass);
        }
        synchronized (__beforeCacheLocker) {
            List<Class<? extends IInterceptor>> _classes = __beforeInterceptsCache.get(targetClass);
            if (_classes != null) {
                return _classes;
            }
            _classes = new ArrayList<Class<? extends IInterceptor>>();
            if (targetClass.isAnnotationPresent(Before.class)) {
                Before _before = targetClass.getAnnotation(Before.class);
                Clean _clean = __doGetCleanIntercepts(targetMethod);
                //
                if (_clean != null &&
                        (_clean.type().equals(IInterceptor.CleanType.ALL) || _clean.type().equals(IInterceptor.CleanType.BEFORE))) {
                    if (_clean.value().length > 0) {
                        for (Class<? extends IInterceptor> _clazz : _before.value()) {
                            if (ArrayUtils.contains(_clean.value(), _clazz)) {
                                continue;
                            }
                            _classes.add(_clazz);
                        }
                    }
                } else {
                    Collections.addAll(_classes, _before.value());
                }
            }
            //
            if (targetMethod.isAnnotationPresent(Before.class)) {
                Collections.addAll(_classes, targetMethod.getAnnotation(Before.class).value());
            }
            //
            if (!_classes.isEmpty()) {
                __beforeInterceptsCache.put(targetClass, _classes);
            }
            //
            return _classes;
        }
    }

    private List<Class<? extends IInterceptor>> __doGetAfterIntercepts(Class<?> targetClass, Method targetMethod) {
        if (__afterInterceptsCache.containsKey(targetClass)) {
            return __afterInterceptsCache.get(targetClass);
        }
        synchronized (__afterCacheLocker) {
            List<Class<? extends IInterceptor>> _classes = __afterInterceptsCache.get(targetClass);
            if (_classes != null) {
                return _classes;
            }
            _classes = new ArrayList<Class<? extends IInterceptor>>();
            if (targetClass.isAnnotationPresent(After.class)) {
                After _after = targetClass.getAnnotation(After.class);
                Clean _clean = __doGetCleanIntercepts(targetMethod);
                //
                if (_clean != null &&
                        (_clean.type().equals(IInterceptor.CleanType.ALL) || _clean.type().equals(IInterceptor.CleanType.AFTER))) {
                    if (_clean.value().length > 0) {
                        for (Class<? extends IInterceptor> _clazz : _after.value()) {
                            if (ArrayUtils.contains(_clean.value(), _clazz)) {
                                continue;
                            }
                            _classes.add(_clazz);
                        }
                    }
                } else {
                    Collections.addAll(_classes, _after.value());
                }
            }
            //
            if (targetMethod.isAnnotationPresent(After.class)) {
                Collections.addAll(_classes, targetMethod.getAnnotation(After.class).value());
            }
            //
            if (!_classes.isEmpty()) {
                __afterInterceptsCache.put(targetClass, _classes);
            }
            //
            return _classes;
        }
    }

    private Clean __doGetCleanIntercepts(Method targetMethod) {
        if (targetMethod.isAnnotationPresent(Clean.class)) {
            return targetMethod.getAnnotation(Clean.class);
        }
        return null;
    }
}
