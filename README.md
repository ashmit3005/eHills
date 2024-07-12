# eHills
EHills Auction System 
## Introduction
EHills is a robust application designed to facilitate a complex multi-client auction system, drawing inspiration from platforms like eBay. The system is comprised of a server application and a client application, each incorporating various programming concepts to create a seamless and secure auction experience.
## Programming Paradigms
###  1. Socket Programming (TCP)
       The core communication between the server and clients is implemented using Socket Programming with TCP (Transmission Control Protocol). This paradigm ensures reliable, ordered, and error-checked delivery          of messages, crucial for real-time bidding and updates in an auction environment.
###  3. Multithreading
      Multithreading is employed to enhance the concurrency of the system. The server efficiently manages multiple client connections concurrently, allowing for simultaneous bidding activities without                   compromising the responsiveness of the application.
###  4. Observer/Observable Interfaces
      The Observer/Observable design pattern is utilized to implement real-time updates. Clients act as observers, and the server acts as the observable. When an auction item is updated, all connected clients are       notified, ensuring that everyone receives the latest information instantly.
 ### 5. JavaFX GUI
      JavaFX is employed to create an intuitive and user-friendly Graphical User Interface (GUI) for the client application. The GUI provides users with a seamless experience, enabling them to navigate through          items, place bids, and interact with other participants effortlessly.
###  6. Serialization
      Serialization is employed for data persistence and transmission. The application uses serialization to convert complex data structures into a format that can be easily stored or transmitted. This is               particularly useful for saving and loading auction items and user information.
 ### 7. Encryption
      To ensure the security and confidentiality of data during transmission, encryption is implemented. This safeguards sensitive information, such as user credentials and bidding details, from unauthorized            access.
## Architecture
The EHills application follows a client-server architecture. The server is responsible for managing auction items, handling bids, and facilitating communication among clients. Clients interact with the server to place bids, receive real-time updates, and engage in the auction process.
### **Server Architecture**
The server application is designed to handle multiple client connections simultaneously. It uses multithreading to manage client requests concurrently, providing a responsive and scalable solution. The server employs the Observer/Observable pattern to notify clients of changes in auction items, ensuring that everyone stays synchronized with the latest information.
### **Client Architecture**
The client application features a JavaFX-based GUI, providing an intuitive interface for users. Clients connect to the server using TCP sockets, enabling bid placement, real-time updates, and interactive communication through the graphical interface.
### **Data Storage and Transmission**
Auction item information, user details, and other relevant data are stored using serialization. This allows for efficient data management, enabling the application to save and load information seamlessly.
## User Guide
Welcome to EHills, your gateway to a dynamic and engaging multi-client auction experience. EHills is designed to provide users with a seamless and feature-rich platform for buying and bidding on a diverse range of items, reminiscent of popular online auction platforms like eBay.
### Getting Started
1.	Account Creation:                                                                                                                                                                                                          
  •	Begin by creating a user account. Provide a unique username and a secure password.
  •	Your account will be used to track bidding history, purchases, and overall engagement within the EHills auction community.
2.	Browsing Items:
   
  •	Once logged in, explore the wide array of auction items available for bidding.
  •	Detailed information about each item, including starting bid, current bid, and auction status, is conveniently presented.
### Placing Bids
1.	Participating in Auctions:
   
  •	To place a bid, navigate to the desired item and enter your bid amount.
  •	Be strategic! Keep an eye on the countdown timer and the current highest bidder to make informed bidding decisions.
3.	Buy Now Option:

  •	Some items may feature a "Buy Now" option with a fixed price. Use this option for instant purchase without engaging in bidding wars.
### Real-Time Updates
1.	Stay Informed:
   
  •	EHills provides real-time updates on auction items. Witness changes in bid status, highest bidders, and auction countdowns as they happen.
3.	Chat Interaction:

  •	Engage with other bidders through the chat feature. Ask questions, share insights, or simply enjoy the sense of community during the auction process.

## References
**Java Socket Programming:
**
  •	Title: "Java Socket Programming"
  •	Source: GeeksforGeeks - Socket Programming in Java
	Multithreading in Java:

  •	Title: "Multithreading in Java"
  •	Source: Java Tutorials - Multithreading
	Observer/Observable Interfaces:

  •	Title: "Observer/Observable in Java"
  •	Source: JournalDev - Observer Design Pattern in Java
JavaFX GUI:

  •	Title: "Introduction to JavaFX"
  •	Source: Oracle - JavaFX Documentation
Serialization in Java:

  •	Title: "Java Object Serialization"
  •	Source: Baeldung - Guide to Java Object Serialization
	Encryption in Java:

  •	Title: "Java Cryptography Architecture"
  •	Source: Oracle - Java Cryptography Architecture
8.	Working with JSON in Java:

  •	Title: "Working with JSON in Java"
  •	Source: Baeldung - Jackson JSON Tutorial
9.	Java I/O Streams:

  •	Title: "Java I/O Tutorial"
  •	Source: JavaTpoint - Java I/O Tutorial

