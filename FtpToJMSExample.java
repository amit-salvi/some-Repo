/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package camelinaction;

import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.BrowsableEndpoint;

/**
 * A route that polls an FTP server for new orders, downloads them, converts the order 
 * file into a JMS Message and then sends it to the JMS incomingOrders queue hosted 
 * on an embedded ActiveMQ broker instance.
 *
 * @author janstey
 *
 */
public class FtpToJMSExample {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // connect to embedded ActiveMQ JMS broker
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                /*from("timer://someOtherTimer?period=2000")//.setBody(simple("<MessageBody>StupidXML</MessageBody>"))
                	.to("jms:incomingOrders");*/

            	from("timer://someTimers?period=5000")
                .process(new Processor(){
                    @Override
                    public void process(Exchange exchange) throws Exception {

                    	BrowsableEndpoint browse = exchange.getContext().getEndpoint("jms:incomingOrders", BrowsableEndpoint.class);

                    	List<Exchange> exchanges = browse.getExchanges();
                        System.out.println("Browsing queue: "+ browse.getEndpointUri() + " size: " + exchanges.size());

                        ConsumerTemplate consume = exchange.getContext().createConsumerTemplate();

                        for (Exchange ex : exchanges) {
                        	String payload = ex.getIn().getBody(String.class);
                            String msgId = ex.getIn().getHeader("JMSMessageID", String.class);
                            System.out.println(msgId + "=" +payload);

                            if(payload == null){
                            	String msg = (String)consume.receiveBody("jms:incomingOrders?selector=JMSMessageID='" + msgId +"'");
                            	System.out.println("MSG --->>>>>>>>>>>  "+msg);
                            }
                        }
                    }
                });
            }
        });
        // start the route and let it do its work
        context.start();
        Thread.sleep(10000);
        // stop the CamelContext
        context.stop();
    }
}
