package camelinaction;

public class Somebean1 {
	int i=0;
	public String doSomething(String payload, String highPriority) {
		i++;
		System.out.println("I------------> " + i);
		System.out.println("payload -----> " + payload);
		System.out.println("high priority> " + highPriority);
		System.out.println("New Output statement just to check the git pull request");
		System.out.println("More Output statements .....");
		return ""+i;
	}
}
