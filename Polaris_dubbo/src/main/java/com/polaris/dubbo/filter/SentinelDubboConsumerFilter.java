/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.polaris.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.polaris.core.GlobalContext;
import com.polaris.core.log.ExtendedLogger;
import com.polaris.dubbo.config.DubboConfig;
import com.polaris.dubbo.fallback.DubboFallbackRegistry;

/**
 * <p>Dubbo service consumer filter for Sentinel. Auto activated by default.</p>
 *
 * If you want to disable the consumer filter, you can configure:
 * <pre>
 * &lt;dubbo:consumer filter="-sentinel.dubbo.consumer.filter"/&gt;
 * </pre>
 *
 * @author leyou
 * @author Eric Zhao
 */
@Activate(group = "consumer")
public class SentinelDubboConsumerFilter extends AbstractDubboFilter implements Filter {

    public SentinelDubboConsumerFilter() {
        RecordLog.info("Sentinel Dubbo consumer filter initialized");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	//传递参数
    	RpcContext.getContext().setAttachments(GlobalContext.getContext());
    	RpcContext.getContext().setAttachment(ExtendedLogger.PARENT_ID, ExtendedLogger.getModuleId());

        Entry interfaceEntry = null;
        Entry methodEntry = null;
        try {
            String resourceName = getResourceName(invoker, invocation, DubboConfig.getDubboConsumerPrefix());
            interfaceEntry = SphU.entry(invoker.getInterface().getName(), EntryType.OUT);
            methodEntry = SphU.entry(resourceName, EntryType.OUT);

            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                // Record common exception.
                Tracer.trace(result.getException());
            }
            return result;
        } catch (BlockException e) {
            return DubboFallbackRegistry.getConsumerFallback().handle(invoker, invocation, e);
        } catch (RpcException e) {
            Tracer.trace(e);
            throw e;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit();
            }
            if (interfaceEntry != null) {
                interfaceEntry.exit();
            }
        }
    }
}
