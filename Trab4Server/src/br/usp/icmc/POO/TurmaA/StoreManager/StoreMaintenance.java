package br.usp.icmc.POO.TurmaA.StoreManager;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Collectors;

import br.usp.icmc.POO.TurmaA.Client.Client;
import br.usp.icmc.POO.TurmaA.Client.User;
import br.usp.icmc.POO.TurmaA.ClientConnection.UserData;
import br.usp.icmc.POO.TurmaA.Product.Marketable;
import br.usp.icmc.POO.TurmaA.Product.Product;
import br.usp.icmc.POO.TurmaA.Purchase.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StoreMaintenance {
	protected String stockData;
	protected String usersData;
	protected String loginsData;
	protected String sellsData;
	protected LinkedList<Client> users;
	protected HashMap<String, String> usersPasswords;
	protected HashMap<Marketable, Integer> products;
	protected LinkedList<Purchase> sells;
	
	protected void loadData(){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(stockData));
			String input;
			String[] parts = null;
			
			while((input = br.readLine()) != null){
				parts = input.split(",");
				products.put(new Product(parts[0], Double.parseDouble(parts[1]), parts[2], parts[3]), new Integer(parts[4]));
			}
			
			br.close();
		}
		catch(IOException e){
			System.out.println("Error reading stocks file " + e);
		}
		
		try {
			br = new BufferedReader(new FileReader(usersData));
			String input;
			String[] parts = null;
			
			while((input = br.readLine()) != null){
				parts = input.split(",");
				Client u = new User(new UserData(parts[0], parts[1], parts[2], parts[3], parts[4]));
				
				for(int i = 0; i < Integer.parseInt(parts[5]); i++){
					input = br.readLine();
					String[] productParts = input.split(",");
					u.addProduct(new Product(productParts[0], Double.parseDouble(productParts[1]), productParts[2], productParts[3]), new Integer(Integer.parseInt(productParts[4])));
				}
				
				users.add(u);
			}
			
			br.close();
		}
		catch(IOException e){
			System.out.println("Error reading users file " + e);
		}
		
		try {
			br = new BufferedReader(new FileReader(loginsData));
			String input;
			String[] parts = null;
			
			while((input = br.readLine()) != null){
				parts = input.split(",");
				usersPasswords.put(parts[0], parts[1]);
			}
			
			br.close();
		}
		catch(IOException e){
			System.out.println("Error reading passwords file " + e);
		}
		
		try {
			br = new BufferedReader(new FileReader(sellsData));
			String input;
			String[] parts = null;
			
			while((input = br.readLine()) != null){
				parts = input.split(",");
				sells.add(new Purchase(parts[0], parts[1], parts[2], parts[3], parts[4]));
			}
			
			br.close();
		}
		catch(IOException e){
			System.out.println("Error reading sells file " + e);
		}
	}
	
	//checks if the date given is valid
	private boolean checkDate(int day, int month, int year){
        if (0 >= day | day > 31) return false;
        if (0 >= month | month > 12) return false;
        if (month == 4 | month == 6 | month == 9 | month == 11)
            if (day == 31) return false;
        if (month == 2){
            if (checkLeapYear(year))
                if (day > 29) return false;
            else
                if (day > 28) return false;                    
        }      
        return true;
    }
       
    private boolean checkLeapYear(int year){
        if (year % 4 != 0) return false;
        if (year % 100 != 0) return true;
        return year % 400 == 0;        
    }
    
	//receives a server side command
	public void receiveCommand(String str){
		if(str.equalsIgnoreCase("add product"))
			addProduct();
		else if(str.equalsIgnoreCase("show stock"))
			showStock((Map.Entry<Marketable, Integer> entry) -> entry.getValue().intValue() > 0, "There are no products at stock");
		else if(str.equalsIgnoreCase("show out of stock"))
			showStock((Map.Entry<Marketable, Integer> entry) -> entry.getValue().intValue() == 0, "No products are out of stock");
		else if(str.equalsIgnoreCase("show all"))
			showStock((Map.Entry<Marketable, Integer> entry) -> true, "There are no products in the store.");
		else if(str.equalsIgnoreCase("show users"))
			showUsers();
		else if(str.equalsIgnoreCase("update stock"))
			updateStock();
		else if(str.equalsIgnoreCase("generate monthly report"))
			generateReport((Purchase p) -> monthlyReport(p), "Monthly");
		else if(str.equalsIgnoreCase("generate daily report"))
			generateReport((Purchase p) -> daylyReport(p), "Daily");
		else if(str.equalsIgnoreCase("exit"))
			saveContent();
	}    
	
	public void addProduct(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name, price, validity, provider;
		String[] parts;
		
		try {
			System.out.println("Type the name of the product: ");
			name = br.readLine();
			System.out.println("Type the price of the product: ");
			price = br.readLine();
			
			do{
				System.out.println("Type the validity date of the product (xx/xx/xxxx): ");
				validity = br.readLine();
				parts = validity.split("/");
			}
			while(!checkDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
			
			System.out.println("Type the provider of the product: ");
			provider = br.readLine();
			
			Product p = new Product(name, Double.parseDouble(price), validity, provider);
			
			//if theres already an equal product in stock
			Optional<Marketable> existingProd = getProduct(p);
			
			if(existingProd.isPresent())
				System.out.println("This product is already registered. Try using \"update stock\" to update this product.");
			//adds a new product to the stock
			else
				products.put(p, new Integer(1));
		}
		catch(IOException e){
			System.out.println("Error getting new product information.");
		}
	}
	
	private void updateStock(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String product, provider;
		
		try{
			System.out.println("Type the name of the product");
			product = br.readLine();
			System.out.println("Type the name of the provider");
			provider = br.readLine();
			
			Optional<Marketable> marketable = getProduct(product, provider);
			
			if(marketable.isPresent()){
				System.out.println("Type the new quantity of this product");
				boolean validQuantity = false;
				
				while(!validQuantity){
					Integer quantity = new Integer(Integer.parseInt(br.readLine()));
				
					if(quantity.intValue() >= 0){
						products.put(marketable.get(), quantity);
						validQuantity = true;
					}
					else
						System.out.println("Please enter a number greater or equal to zero.");
				}
			}
			else
				System.out.println("Product not yet registered. Try to register this product before updating it.");
		}
		catch(IOException e){
			System.out.println("Error getting new product information.");
		}
	}
	
	//returns a product with the name "name" and provider "provider"
	protected Optional<Marketable> getProduct(String name, String provider){
		return products.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.filter((key) -> key.getName().equalsIgnoreCase(name) && key.getProvider().equalsIgnoreCase(provider))
					.findFirst();
	}
		
	protected Optional<Marketable> getProduct(String name){
		return products.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.filter((key) -> key.getName().equalsIgnoreCase(name))
					.findFirst();
	}
		
	protected Optional<Marketable> getProduct(Product p){
		return products.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.filter((key) -> key.equals(p))
					.findFirst();
	}
	
	public Client getUser(String id){
		Optional<Client> user = users
				.stream()
				.filter(u -> u.getId().equalsIgnoreCase(id))
				.findFirst();
		
		return user.get();
	}
	
	private void showStock(Predicate<Map.Entry<Marketable, Integer>> filter, String msg){
		Map<Marketable, Integer> productsAux = products
													.entrySet()
													.stream()
													.filter(filter)
													.collect(Collectors.toMap((entry) -> entry.getKey(), (entry) -> entry.getValue()));
		if(productsAux.size() > 0){
			productsAux.entrySet().forEach((entry) -> {
					Marketable p = entry.getKey();
					
					System.out.println("Product " + p.getName() + " - Price: " + p.getPrice() + " - Validity: " + p.getValidity()
							+ " - Provider: " + p.getProvider() + 
							(entry.getValue().intValue() > 0 ?  " - Quantity: " + (entry.getValue() + (entry.getValue() == 1 ? " copy" : " copies")) : " - Out of Stock"));
				});
		}
		else
			System.out.println(msg);
	}
	
	private void showUsers(){
		users
			.stream()
			.forEach(u -> System.out.println("ID: " + u.getId() + " Name: " + u.getName()));
	}
	
	private boolean daylyReport(Purchase p){
		LocalDate date = LocalDate.now();	
		String[] parts = p.getDate().split("/");	
		LocalDate sellDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
		sellDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
		date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
		return date.format(DateTimeFormatter.ofPattern("dd/MM/yy")).equals(sellDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
	}
	
	private boolean monthlyReport(Purchase p){
		String[] sellParts = p.getDate().split("/");
		String[] parts = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy")).toString().split("/");
		return sellParts[2].equals(parts[2]) && sellParts[1].equals(parts[1]);
	}
	
	private void generateReport(Predicate<Purchase> filter, String type){
		try {			
			List<String> reportSells = sells
					.stream()
					.filter(filter)
					.map((p) -> 
						"User " + p.getBuyer() + " bought " + 
						p.getQuantity() + " " + p.getProduct() +
						" from " + p.getProvider() + " at " + p.getDate() + "."
					)
					.collect(Collectors.toList());
			
			if(reportSells.size() == 0){
				System.out.println("No sells to generate report.");
				return;
			}
			
			Document document = new Document();
			LocalDate date = LocalDate.now();
			PdfWriter.getInstance(document, new FileOutputStream("../" + date.format(DateTimeFormatter.ofPattern("dd_MM_yy")) + "_Store" + type + "SellsReport.pdf"));
			document.open();
			
			for(String s : reportSells)
				document.add(new Paragraph(s));
			
			document.close();
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
		catch(DocumentException e){
			System.out.println(e);
		}
	}
	
	private void saveContent(){
		saveStockContent();
		saveUsers();
		saveSells();
	}
	
	private void saveLogins(){
		String data;
		String separator = ",";
		boolean type = false;
		
		for(Map.Entry<String, String> entry : usersPasswords.entrySet()){
			data = "";
			data += entry.getKey() + separator;
			data += entry.getValue();
			writeLog(data, loginsData, type);
			type = true;
		}		
	}
	
	private void saveUsers(){
		String data;
		String separator = ",";
		boolean type = false;
		System.out.println("Saving users content...");		
		
		for(Client u : users){
			data = "";
			data += u.getId() + separator;
			data += u.getName() + separator;
			data += u.getAdress() + separator;
			data += u.getPhone() + separator;
			data += u.getEmail() + separator;
			data += u.getProducts().size();
			
			writeLog(data, usersData, type);
			type = true;
			
			System.out.println("Saving users products...");	
			for(Map.Entry<Marketable, Integer> entry : u.getProducts().entrySet()){
				data = "";
				Marketable m = entry.getKey();
				data += m.getName() + separator;
				data += m.getPrice() + separator;
				data += m.getValidity() + separator;
				data += m.getProvider() + separator;
				data += entry.getValue().intValue();
				writeLog(data, usersData, type);
			}
		}
		
		saveLogins();
	}
	
	private void saveStockContent(){
		String data;
		String separator = ",";
		boolean type = false;
		System.out.println("Saving stock content...");
			
		for(Map.Entry<Marketable, Integer> entry : products.entrySet()){
			data = "";
			Marketable p = entry.getKey();
			Integer i = entry.getValue();
			data += p.getName() + separator;
			data += p.getPrice() + separator;
			data += p.getValidity() + separator;
			data += p.getProvider() + separator;
			data += i;
			writeLog(data, stockData, type);
			type = true;
		}
	}
	
	private void saveSells(){
		String data;
		String separator = ",";
		boolean type = false;
		System.out.println("Saving sells content...");
		
		for(Purchase p : sells){
			data = "";
			data += p.getBuyer() + separator;
			data += p.getQuantity() + separator;
			data += p.getProduct() + separator;
			data += p.getProvider() + separator;
			data += p.getDate();
			writeLog(data, sellsData, type);
			type = true;
		}
	}
	
	private void writeLog(String str, String filename, boolean type){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, type)));
			pw.println(str);
			pw.close();
		}
		catch(IOException e){
			System.out.println("Error writing to file.");
		}
	}
}
