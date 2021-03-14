package contact;

import java.util.List;

public class Contact {
	private int contactID;
	private String contactName;
	private String email;
	private List<String> contactNumber;
	public int getContactID() {
		return contactID;
	}
	public void setContactID(int contactID) {
		this.contactID = contactID;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(List<String> contactNumber) {
		this.contactNumber = contactNumber;
	}
	@Override
	public String toString() {
		return "Contact [contactID=" + contactID + ", contactName=" + contactName + ", email=" + email
				+ ", contactNumber=" + contactNumber + "]";
	}
	
	
}
