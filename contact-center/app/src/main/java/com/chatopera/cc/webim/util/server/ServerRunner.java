/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.webim.util.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.chatopera.cc.webim.util.server.handler.AiIMEventHandler;
import com.chatopera.cc.webim.util.server.handler.EntIMEventHandler;
import com.chatopera.cc.webim.util.server.handler.IMEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.webim.util.server.handler.AgentEventHandler;

@Component  
public class ServerRunner implements CommandLineRunner {  
    private final SocketIOServer server;
    private final SocketIONamespace imSocketNameSpace ;
    private final SocketIONamespace agentSocketIONameSpace ;
    private final SocketIONamespace entIMSocketIONameSpace ;
    private final SocketIONamespace aiIMSocketIONameSpace ;
    private final SocketIONamespace callCenterSocketIONameSpace ;
    private final SocketIONamespace calloutSocketIONameSpace ;
    
    @Autowired  
    public ServerRunner(SocketIOServer server) {  
        this.server = server;  
        imSocketNameSpace = server.addNamespace(UKDataContext.NameSpaceEnum.IM.getNamespace())  ;
        agentSocketIONameSpace = server.addNamespace(UKDataContext.NameSpaceEnum.AGENT.getNamespace()) ;
        entIMSocketIONameSpace = server.addNamespace(UKDataContext.NameSpaceEnum.ENTIM.getNamespace()) ;
        aiIMSocketIONameSpace = server.addNamespace(UKDataContext.NameSpaceEnum.AIIM.getNamespace()) ;

        if(UKDataContext.model.get("sales") != null && UKDataContext.model.get("sales") == true){
            calloutSocketIONameSpace = server.addNamespace(UKDataContext.NameSpaceEnum.CALLOUT.getNamespace());
        } else {
            calloutSocketIONameSpace = null;
        }

        if(UKDataContext.model.get("callcenter") !=null && UKDataContext.model.get("callcenter") == true){
        	callCenterSocketIONameSpace  = server.addNamespace(UKDataContext.NameSpaceEnum.CALLCENTER.getNamespace()) ;
        }else{
        	callCenterSocketIONameSpace = null ;
        }
    }
    
    @Bean(name="imNamespace")
    public SocketIONamespace getIMSocketIONameSpace(SocketIOServer server ){
    	imSocketNameSpace.addListeners(new IMEventHandler(server));
    	return imSocketNameSpace  ;
    }
    
    @Bean(name="agentNamespace")
    public SocketIONamespace getAgentSocketIONameSpace(SocketIOServer server){
    	agentSocketIONameSpace.addListeners(new AgentEventHandler(server));
    	return agentSocketIONameSpace;
    }

    @Bean(name="entimNamespace")
    public SocketIONamespace getEntIMSocketIONameSpace(SocketIOServer server){
    	entIMSocketIONameSpace.addListeners(new EntIMEventHandler(server));
    	return entIMSocketIONameSpace;
    }
    
    @Bean(name="aiimNamespace")
    public SocketIONamespace getAiIMSocketIONameSpace(SocketIOServer server){
    	aiIMSocketIONameSpace.addListeners(new AiIMEventHandler(server));
    	return aiIMSocketIONameSpace;
    }
    
    @Bean(name="callCenterNamespace")
    public SocketIONamespace getCallCenterIMSocketIONameSpace(SocketIOServer server){
    	if(UKDataContext.model.get("callcenter") !=null && UKDataContext.model.get("callcenter") == true){
    		Constructor<?> constructor;
			try {
				constructor = Class.forName("com.chatopera.cc.webim.util.server.handler.CallCenterEventHandler").getConstructor(new Class[]{SocketIOServer.class});
				callCenterSocketIONameSpace.addListeners(constructor.newInstance(server));
			} catch (NoSuchMethodException | SecurityException
					| ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
    	}
    	return callCenterSocketIONameSpace;
    }

    @Bean(name="calloutNamespace")
    public SocketIONamespace getCalloutIMSocketIONameSpace(SocketIOServer server){
        if(UKDataContext.model.get("sales") !=null && UKDataContext.model.get("sales") == true){
            Constructor<?> constructor;
            try {
                constructor = Class.forName("com.chatopera.cc.webim.util.server.handler.CalloutEventHandler").getConstructor(new Class[]{SocketIOServer.class});
                calloutSocketIONameSpace.addListeners(constructor.newInstance(server));
            } catch (NoSuchMethodException | SecurityException
                    | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return calloutSocketIONameSpace;
    }

    public void run(String... args) throws Exception { 
        server.start();  
        UKDataContext.setIMServerStatus(true);	//IMServer 启动成功
    }  
}  
