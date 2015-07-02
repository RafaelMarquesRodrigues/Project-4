# Project 4
OOP's fourth project<br><br>

##Online Store

The project consists in two programs: one for the store and one for the user.
It's main goal is to create a online store that can be accessed by clients via internet, making possible to 
buy products, see the available products, etc.<br><br>

####How to use it

First, run a server to grant access to the clients. Then, any person can run the client program and connect to the server.<br>
You can run both programs by using **java -jar *program*.jar** or by just double clicking the jar file.<br><br>

####Commands
* **Server side**
  * *add product*
  
              Adds a product to the store.
  
 * *update stock*
  
              Updates the quantity of an existing product on the store.
 
 * *show stock*
 
              Shows all the products at stock at the store.

  * *show out of stock*
  
              Shows all the products out of stock at the store.

  * *show all*

              Shows all products registered at the store.

  * *show users*

              Shows all users registered at the store.
              
  * *generate monthly report*

              Generates a pdf file that contains all sells made in the current month.

  * *generate daily report*
    
              Generates a pdf file that contains all sells made in the current day.

  * *help*
  
              Prints all available commands on the screen.

  * *exit*
    
              Shutdown the server.

* **Client side**
  * *show products*
          
              Shows the user all the products from the store.

  * *show acquired*
  
              Shows the user all the products he/she has.

  * *buy product*
  
              Let's the user buy a product from the store.

  * *help*
  
              Prints all the available commands on the screen.

  * *exit*
  
              Exits from the program.
    
####GOF patterns
We've utilized the *singleton* pattern designed by the GOF. This project pattern was used to make sure that only one store is available at a time for the server, otherwise the system could operate with two existing stores, which could give the system a lot of problems.
