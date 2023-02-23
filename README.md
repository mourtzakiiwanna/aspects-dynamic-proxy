# Aspect Weaver using Dynamic Proxy API
In this project we will be creating a simple version of an Aspect-Oriented mechanism with the use of Dynamic Proxy API for the aspect weaver implementation. 
## Prerequisites
• Make sure you have Java 11 installed

## Implementation testing

In order to test our Aspect implementation, we have created a Main class. <br/> 
Run the following command to see the results: 

```bash
  java Main 
```
where, the expected output is the following: 

```bash
-------------------------------------
The normal output is:

Hello Jo!
You have a message: Aspect is OK...

The output after the aspect weaving is:

This is a greeting....
Hello Ioanna! I'm an aspect!
The greeting has been done.

This is a message deliver....
You have a message: Aspect rocks!!
The message has been delivered.
-------------------------------------
```

## Testing Description 

We will use the generated data to implement the Key-Value store.

**KV Server** <br/> 
First of all, we need to launch the servers using the following commands:
```bash
  $ python3 kvServer.py -a 127.0.0.1 -p 8000 
  $ python3 kvServer.py -a 127.0.0.1 -p 8001 
  $ python3 kvServer.py -a 127.0.0.1 -p 8002 
```
where,<br/>
-a: ip_address <br/>
-p: port <br/>

The above commands can run from different terminals in order to set up multiple servers simultaneously. 

**KV Client** <br/> 
After servers are launched, we can populate the database with the generated data. <br/>

To launch the client, use the following command:
```bash
  $ python3 kvClient.py -s serverFile.txt -i dataToIndex.txt -k 3
```
where,<br/>
-s: file (serverFile.txt) containing a list of server IPs and ports that will be listening for queries <br/>
-i: file (dataToIndex.txt) containing data that was output from the previous part of the project <br/>
-k: replication factor, i.e. how many different servers will have the same replicated data <br/>

When the indexing is finished, we can move ahead with performing queries. <br/> 
KV Broker accepts queries from the user, as shown in the examples below: <br/>

```bash
  $ GET key2
  $ DELETE key3
  $ QUERY key7.age
  $ COMPUTE X-2 WHERE X = QUERY key7.age
  $ COMPUTE X^2 WHERE X = QUERY key7.age
  $ COMPUTE 2*X+3 WHERE X = QUERY key7.age
  $ COMPUTE (X+5)*Y WHERE X = QUERY key7.age AND Y = QUERY key10.height
  $ COMPUTE (X+Y)^(X+Y) WHERE X = QUERY key7.age AND Y = QUERY key10.height
  $ COMPUTE 2/(X+3*(Y+Z)) WHERE X = QUERY key7.age AND Y = QUERY key10.height AND Z = QUERY key4.postal_code
  $ COMPUTE log(2*(X+3)) WHERE x = QUERY key7.age
  $ COMPUTE cos(X)-tan(2*Y+3) WHERE X = QUERY key7.age AND Y = QUERY key10.height
  $ EXIT
```
The supported query commands are : GET, DELETE, QUERY, COMPUTE (addition, subtraction, division, multiplication, power, trigonometric/logarithmic functions for up to 3 variables which are queries). </br> </br>
The "EXIT" command is used to exit the kvClient.
