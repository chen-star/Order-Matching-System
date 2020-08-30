# Order Matching Trading System
---
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)

[![ForTheBadge built-with-love](http://ForTheBadge.com/images/badges/built-with-love.svg)](https://GitHub.com/Naereen/)


[![Ask Me Anything !](https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg)](https://GitHub.com/Naereen/ama)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/Naereen/badges/)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)


> GitHub [@Alex Chen](https://github.com/chen-star) &nbsp;&middot;&nbsp;

---

## Architecture


~~~

     Seller/Buyer

          +
          |
          | 1 - place order
          |
          |
  +-------v-------+
  |               |  
  |  API Gateway  |
  |               |             
  +---------------+
          |
          | 2
          |
          |
  +-------v-------+
  |               |      3      +--------------------+           6
  | Order Service +------------->   orders queue     +----------------------+
  |               |             +---------+----------+                      |
  +---------------+                       |                                 |
          | 4                             |5                                |
          |                               |                                 |
     +----v-----+                         |                                 |
     |          |               +---------v----------+            +---------v----------+
     | Database |          7    |                    |   8        |                    |
     |          <---------------+  Trading Service   +------------>  Trading Service   |
     +----------+               |  (order+matching)  |            |  (order+matching)  |
                                |                    |            |      Standby       |
                                |                    |            |                    |
                                +--------------------+            +--------------------+
                                
~~~                                



## User Interface

**Based on Vue.js**

* Implmented
	
	- User Login / Logout
	- Password Setting
	- Nav Bar
	- Side Bar
	- Banking 
	- Orders
	- Transactions
	- Buy / Sell

* Libs & Tools

	- **Electron**: Build cross-platform desktop App with JavaScript
	- **Element UI**: Common UI elements
	- **Vue Bus**: Component communication


## Order Service

**Based on Spring Core + Spring Boot + Spring Cloud**

