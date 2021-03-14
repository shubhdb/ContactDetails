package contact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import exceptions.ContactNotFoundException;

public class ContactService {
	static DbConnect db=new DbConnect();
	
	public static void addContact(Contact contact,List<Contact> contacts) {
		Connection con=db.startConnection();
		
		PreparedStatement stmt;
		String insertContact="Insert into contact_tbl (contactId,contactName,contactEmail,contactList) values (?,?,?,?)";
		try {
			stmt=con.prepareStatement(insertContact);
			stmt.setInt(1, contact.getContactID());
			stmt.setString(2,contact.getContactName());
			stmt.setString(3,contact.getEmail());
			if(contact.getContactNumber()!=null)
				stmt.setString(4, contact.getContactNumber().toString());
			else
				stmt.setString(4, null);
			if(stmt.execute()) {
				contacts.add(contact);
				System.out.println("Contact added successfully");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		
	}
	
	void removeContact(Contact contact, List<Contact> contacts) throws ContactNotFoundException{
		Contact temp = null;
		
		for(Contact con : contacts){
			if(con.getContactID() == contact.getContactID()){
					temp = con;
			}
		}
		
		if(temp == null) {
				throw new ContactNotFoundException("Contact not found");	
			}
		else {
			contacts.remove(temp);
			System.out.println("\nContact Removed\n");
		}
	}
	
	Contact searchContactByName(String name, List<Contact> contacts) throws ContactNotFoundException{
		Contact con=null;
		for(Contact tempcon:contacts) {
			if(name.equalsIgnoreCase(con.getContactName())) {
				con=tempcon;
			}
		}
		if(con==null)
			throw new ContactNotFoundException("No contact found with that name");
		
		return con;
	}
	
	List<Contact> SearchContactByNumber(String number, List<Contact> contacts) throws ContactNotFoundException{
		List<Contact> resulttemp=new ArrayList<Contact>();
		for(Contact con:contacts) {
			if(con.getContactNumber().contains(number))
				resulttemp.add(con);
		}
		
		return resulttemp;
	}
	
	public void addContactNumber(int contactId, String contactNo, List<Contact> contacts){
		 List<String> contactNumber=new ArrayList<String>();
		 for(String contact : contactNo.split(","))
				contactNumber.add(contact);
			for (Contact con : contacts) {
				if (con.getContactID() == contactId) {
					con.getContactNumber().addAll(contactNumber);
				}
			}
	}
	
	void sortContactsByName(List<Contact> contacts) {
		Comparator<Contact> cmp=Comparator.comparing(Contact::getContactName);
		Collections.sort(contacts,cmp);
	}
	//8 readin contact from file and adding to database 
	void readContactsFromFile(List<Contact> contacts, String fileName) {
		File file = new File(fileName);
		Scanner filesc;
		try {
			filesc = new Scanner(file);
			String [] contactInformation = null;
			List<String> contactNumber = new ArrayList<String>();
			Contact obj=null;
			
			while (filesc.hasNextLine()) {
				obj=new Contact();
				Integer contactID = 0;
				String contactName = "", emailAddress = "", contactNumberString = "";
				
				contactInformation = filesc.nextLine().split(",");
				for(int i=0;i<contactInformation.length;i++) 
				{
					if(i==0) {
						contactID = Integer.parseInt(contactInformation[0]);
						obj.setContactID(contactID);
					}
					else if(i==1){
						contactName = contactInformation[1];
						obj.setContactName(contactName);
					}
					else if(i==2) {
						emailAddress = contactInformation[2];
						obj.setEmail(emailAddress);
					}
					else {
						if(contactInformation.length>2){	
							contactNumberString += contactInformation[i] + ",";
							for (String s : contactNumberString.split(","))
								contactNumber.add(s);
						}
						obj.setContactNumber(contactNumber);
					}
				}
				addContact(obj,contacts);
				contacts.add(obj);
			}
			filesc.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}	
		System.out.println("Data inserted!");
	}
	
	void serializeContactDetails(List<Contact> contacts , String fileName) {
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		
		try {
			fout = new FileOutputStream(fileName, true);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(contacts);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Contact> deserializeContact(String fileName){
		ObjectInputStream ois = null;
		List<Contact> contacts = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			ois = new ObjectInputStream(fin);
			contacts = (List<Contact>) ois.readObject();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					
				}
			}
		}
		return contacts;
	}
	

	public Set<Contact> populateContactFromDb(){
		Set<Contact> contactSet = new HashSet<Contact>();
		Connection conn = db.startConnection();	
		try {
			String sql = "SELECT * FROM contact_tbl";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			Contact obj=null;
			while(rs.next()) {
				obj = new Contact();
				List<String> contactNumber = new ArrayList<String>();
				String contactNumberString = "";
				if (rs.getInt("CONTACTID") != 0)
					obj.setContactID(rs.getInt("CONTACTID"));
				if (rs.getString("CONTACTNAME") != null)
					obj.setContactName(rs.getString("CONTACTNAME"));
				if (rs.getString("CONTACTEMAIL") != null)
					obj.setEmail(rs.getString("CONTACTEMAIL"));
				if (rs.getString("CONTACTLIST") != null)
					contactNumberString = rs.getString("CONTACTLIST");
				for (String s : contactNumberString.split(","))
					contactNumber.add(s);
				
				obj.setContactNumber(contactNumber);
				System.out.println("Contact fetched");
				contactSet.add(obj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return contactSet;
	}
	
	boolean addContacts(List<Contact> existingContact,Set<Contact> newContacts) {
		try {
			for (Contact c : newContacts)
				existingContact.add(c);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		List<Contact> contacts = new ArrayList<Contact>();
		ContactService cs = new ContactService();
		int choice;
		Integer contactID;
		String contactName, emailAddress, contactNumberString;
		List<String> contactNumber = null;
		
		do {
			System.out.println("\n1)Display contacts\n2)Add contact\n3)Remove contact\n"
					+ "4)Search by contact name\n5)Search by contact number\n6)Set contact number\n"
					+ "7)Sort contact by name\n8)Add contacts from file\n"
					+ "9)Serialize contact details\n10)Deserialize contact details\n11)Populate from DB\n"
					+ "12)Add new contacts to existing ones");
			System.out.print("Your choice: ");
			choice = sc.nextInt();sc.nextLine();
			switch(choice) {
			
			case 1:
				
				for(Contact c : contacts)
					System.out.println(c.toString());
				if(contacts.size()==0)
					System.out.println("Contacts not found please use step 11 and 12");
				break;
			
			case 2:
				Contact newobj=new Contact();
				System.out.print("\nEnter contact ID: ");
				contactID = sc.nextInt();
				newobj.setContactID(contactID);
				sc.nextLine();
				System.out.print("\nEnter contact name: ");
				contactName = sc.nextLine();
				newobj.setContactName(contactName);
				System.out.print("\nEnter contact email address: ");
				emailAddress = sc.nextLine();
				newobj.setEmail(emailAddress);
				System.out.print("\nEnter contact number: ");
				contactNumberString = sc.nextLine();
				contactNumber = new ArrayList<String>();
				for (String s : contactNumberString.split(","))
						contactNumber.add(s);
				
				newobj.setContactNumber(contactNumber);
				ContactService.addContact(newobj, contacts);
				break;
			
			case 3:
				Contact obj=new Contact();
				System.out.print("\nEnter contact ID: ");
				contactID = sc.nextInt();
				obj.setContactID(contactID);
				sc.nextLine();
				System.out.print("\nEnter contact name: ");
				contactName = sc.nextLine();
				obj.setContactName(contactName);
				System.out.print("\nEnter contact email address: ");
				emailAddress = sc.nextLine();
				obj.setEmail(emailAddress);
				System.out.print("\nEnter contact number: ");
				contactNumberString = sc.nextLine();
				contactNumber = new ArrayList<String>();
				for (String s : contactNumberString.split(","))
						contactNumber.add(s);
				
				obj.setContactNumber(contactNumber);
				
				try {
					cs.removeContact(obj, contacts);
				} catch (ContactNotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 4:
				System.out.print("\nEnter contact name: ");
				contactName = sc.next();
				try {
					Contact c = cs.searchContactByName(contactName, contacts);
					System.out.println(c.toString());
				} catch (ContactNotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 5:
				System.out.print("\nEnter contact number: ");
				contactNumberString = sc.next();
				try {
					List<Contact> c = cs.SearchContactByNumber(contactNumberString, contacts);
					for (Contact ct : c)
						System.out.println(ct.toString());
				} catch (ContactNotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 6:
				System.out.print("\nEnter contact ID: ");
				contactID = sc.nextInt();
				sc.nextLine();
				System.out.print("\nEnter contact number: ");
				contactNumberString = sc.nextLine();
				cs.addContactNumber(contactID, contactNumberString, contacts);
				break;
				
			case 7:
				cs.sortContactsByName(contacts);
				break;
				
			case 8:
				cs.readContactsFromFile(contacts,"Contact.txt");
				break;
			
			case 9:
				cs.serializeContactDetails(contacts, "output.ser");
				break;
				
			case 10:
				List<Contact> c = cs.deserializeContact("output.ser");
				for (Contact ct : c)
					System.out.println(ct.toString());
				break;
				
			case 11:
				Set<Contact> contactSet  =  cs.populateContactFromDb();
				Iterator<Contact> itr = contactSet.iterator();
				while (itr.hasNext()){
					Contact ct = (Contact) itr.next();
					System.out.println(ct.toString());
				}
				break;
			case 12:
				contactSet  = new HashSet<Contact>();
				contactSet = cs.populateContactFromDb();
				if (cs.addContacts(contacts, contactSet)) {
					System.out.println("\nNew contacts added\n");
				}
				else {
					System.out.println("\nError please try again\n");
				}	
				break;
			case 13:
					System.out.println("Thank you!");break;
			default: System.out.println("Please enter correct number");break;
			}
		}while(choice!=13);
		sc.close();
	}
}
