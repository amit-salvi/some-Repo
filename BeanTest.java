package camelinaction;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class BeanTest {

	public static void main(String[] args){
		try{
			/*JndiContext cont = new JndiContext();
			cont.bind("someBean", new SomeBean());*/

			CamelContext context = new DefaultCamelContext();
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					from("timer://someTimer?period=1000").setBody(simple("ABC"))
						.bean(Somebean1.class,"doSomething(${body}, String abcd)")
						.to("mock:results");
				}
			});
			context.start();
			Thread.sleep(Long.MAX_VALUE);
			context.stop();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

class SomeBean{
	int i=0;
	public String someMethod(){
		i++;
		System.out.println("I------------> " + i);
		return ""+i;
	}
}