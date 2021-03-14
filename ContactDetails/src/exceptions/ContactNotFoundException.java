package exceptions;

@SuppressWarnings("serial")
public class ContactNotFoundException extends Exception {

	public ContactNotFoundException(String str) {
		System.out.println(str);
	}
}
